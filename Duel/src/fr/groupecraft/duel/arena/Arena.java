package fr.groupecraft.duel.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.duels.Duel;
import net.milkbowl.vault.economy.Economy;

public class Arena {

	private Duel duel;
	private Inventory playerA,playerB;
	private Location tpA,tpB;
	private String name;
	private String worldName;
	private Location plA,plB;
	
	public Arena(String name ,Location tpA, Location tpB) {
		this.name=name;
		worldName=tpA.getWorld().getName();
		tpA.setWorld(null);
		tpB.setWorld(null);
		this.tpA=tpA;
		this.tpB=tpB;
	}
	public Arena(Arena arena) {
		this.name=arena.getName();
		this.tpA=arena.tpA;
		this.tpB=arena.tpB;
		this.worldName=arena.worldName;
	}
	
	public Location[] getTPs() {
		Location locA=new Location(Bukkit.getWorld(worldName),tpA.getX(),tpA.getY(),tpA.getZ());
		Location locB=new Location(Bukkit.getWorld(worldName),tpB.getX(),tpB.getY(),tpB.getZ());
		Location[] locations={locA,locB};
		return locations;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setTPs(Location tpA, Location tpB) {
		tpA.setWorld(null);
		tpA.setWorld(null);
		this.tpA=tpB;
		this.tpB=tpB;
	}
	public boolean startDuel(Duel duel) {
		Inventory inv=DuelMain.getInstance().duelInv;
		if(!DuelMain.getInstance().canStartDuel) {
			return false;
		}
		Economy eco=DuelMain.getInstance().economy;
		if(eco.getBalance(duel.getPlayers()[0])<=duel.getMise()||eco.getBalance(duel.getPlayers()[1])<=duel.getMise()) {
			duel.getPlayers()[0].sendMessage("votre duel a été annulé");
			duel.getPlayers()[1].sendMessage("votre duel a été annulé");
			return false;
		}
		eco.depositPlayer(duel.getPlayers()[0].getName(), -duel.getMise());
		eco.depositPlayer(duel.getPlayers()[1].getName(), -duel.getMise());
		this.duel=duel;
		duel.start();
		plA=duel.getPlayers()[0].getLocation();
		plB=duel.getPlayers()[1].getLocation();
		playerA =Bukkit.createInventory(null, InventoryType.PLAYER);
		playerB =Bukkit.createInventory(null, InventoryType.PLAYER);
		playerA.setContents(duel.getPlayers()[0].getInventory().getContents());
		playerB.setContents(duel.getPlayers()[1].getInventory().getContents());
		duel.getPlayers()[0].getInventory().clear();
		duel.getPlayers()[1].getInventory().clear();
		duel.getPlayers()[0].getInventory().setBoots(inv.getItem(4));
		duel.getPlayers()[0].getInventory().setHelmet(inv.getItem(1));
		duel.getPlayers()[0].getInventory().setChestplate(inv.getItem(2));
		duel.getPlayers()[0].getInventory().setLeggings(inv.getItem(3));
		duel.getPlayers()[0].getInventory().setItemInHand(inv.getItem(0));
		duel.getPlayers()[1].getInventory().setBoots(inv.getItem(4));
		duel.getPlayers()[1].getInventory().setHelmet(inv.getItem(1));
		duel.getPlayers()[1].getInventory().setChestplate(inv.getItem(2));
		duel.getPlayers()[1].getInventory().setLeggings(inv.getItem(3));
		duel.getPlayers()[1].getInventory().setItemInHand(inv.getItem(0));
		Location tpAb= new Location(Bukkit.getWorld(worldName),tpA.getX(),tpA.getY(),tpA.getZ());
		Location tpBb= new Location(Bukkit.getWorld(worldName),tpB.getX(),tpB.getY(),tpB.getZ());
		this.duel.getPlayers()[0].teleport(tpAb);
		this.duel.getPlayers()[1].teleport(tpBb);
		return true;
	}
	public void forceEndDuel() {
		playerA=null;playerB=null;
		if(duel==null) return;
		duel.getPlayers()[0].teleport(plA);
		duel.getPlayers()[1].teleport(plB);
		duel.getPlayers()[0].getInventory().setContents(playerA.getContents());	
		duel.getPlayers()[1].getInventory().setContents(playerB.getContents());
		Economy eco=DuelMain.getInstance().economy;
		eco.bankDeposit(duel.getPlayers()[0].getName(), duel.getMise());
		eco.bankDeposit(duel.getPlayers()[1].getName(), duel.getMise());
		playerA=null;
		playerB=null;
		duel= null;
		plA=null;plB=null;
		if(DuelMain.getInstance().canStartDuel) {
			DuelMain.getInstance().actualizeDuels();
		}
	}
	public boolean endDuel(Player winer) {
		if(!duel.hasPlayer(winer)) {
			return false;
		}
		try {
			duel.getPlayers()[0].teleport(plA);
			duel.getPlayers()[1].teleport(plB);
			duel.getPlayers()[0].getInventory().setContents(playerA.getContents());
			duel.getPlayers()[1].getInventory().setContents(playerB.getContents());
			Economy eco=DuelMain.getInstance().economy;
			if(duel.getPlayers()[0]==winer){
				eco.depositPlayer(duel.getPlayers()[1].getName(), 2*duel.getMise());
			}else {
				eco.depositPlayer(duel.getPlayers()[0].getName(), 2*duel.getMise());
			}
			playerA=null;
			playerB=null;
			duel= null;
		return true;
		} catch (Exception e) {
			return false;
		}finally {
			if(DuelMain.getInstance().canStartDuel) {
				DuelMain.getInstance().actualizeDuels();
			}
		}
	}
	public boolean hasDuel() {
		return duel!=null;
	}
	public Duel getDuel() {
		return duel;
	}

	public void setTpA(Location loc) {
		tpA=loc;
	}
	public void setTpB(Location loc) {
		tpB=loc;
	}
	public void setWorld(World world) {
		worldName=world.getName();
	}
}
