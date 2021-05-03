package fr.groupecraft.duel.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.duels.Duel;
import net.milkbowl.vault.economy.Economy;

public class Arena {

	private Duel duel;
	private PlayerInventory playerA,playerB;
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
		this.tpA=arena.getTPs()[0];
		this.tpB=arena.getTPs()[1];
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
		eco.bankDeposit(duel.getPlayers()[0].getName(), -duel.getMise());
		eco.bankDeposit(duel.getPlayers()[1].getName(), -duel.getMise());
		this.duel=duel;
		duel.start();
		plA=duel.getPlayers()[0].getLocation();
		plB=duel.getPlayers()[1].getLocation();
		playerA=duel.getPlayers()[0].getInventory();
		playerB=duel.getPlayers()[1].getInventory();
		playerA.clear();
		playerB.clear();
		playerA.setBoots(inv.getItem(4));
		playerA.setHelmet(inv.getItem(1));
		playerA.setChestplate(inv.getItem(2));
		playerA.setLeggings(inv.getItem(3));
		playerA.setItemInHand(inv.getItem(1));
		playerB.setBoots(inv.getItem(4));
		playerB.setHelmet(inv.getItem(1));
		playerB.setChestplate(inv.getItem(2));
		playerB.setLeggings(inv.getItem(3));
		playerB.setItemInHand(inv.getItem(1));
		this.duel.getPlayers()[0].teleport(tpA);
		this.duel.getPlayers()[1].teleport(tpB);
		return true;
	}
	public void forceEndDuel() {
		duel.getPlayers()[0].teleport(plA);
			duel.getPlayers()[1].teleport(plB);
		setInventory(duel.getPlayers()[0], playerA);
		setInventory(duel.getPlayers()[1], playerB);
		Economy eco=DuelMain.getInstance().economy;
		eco.bankDeposit(duel.getPlayers()[0].getName(), duel.getMise());
		eco.bankDeposit(duel.getPlayers()[1].getName(), duel.getMise());
		playerA=null;
		playerB=null;
		duel= null;
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
			setInventory(duel.getPlayers()[0], playerA);
			setInventory(duel.getPlayers()[1], playerB);
			Economy eco=DuelMain.getInstance().economy;
			eco.bankDeposit(duel.getPlayers()[0].getName(), 2*duel.getMise());
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
		return duel==null;
	}
	public Duel getDuel() {
		return duel;
	}
	private void setInventory(Player player, PlayerInventory inv) {
		for(int i =0; i<inv.getSize(); i++) {
			player.getInventory().setItem(i, inv.getItem(i));
		}
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
