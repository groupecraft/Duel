package fr.groupecraft.duel;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.groupecraft.duel.arena.Arena;
import fr.groupecraft.duel.arena.ArenaCmd;
import fr.groupecraft.duel.commun.EventListener;
import fr.groupecraft.duel.commun.FileGSON;
import fr.groupecraft.duel.commun.FilesUsing;
import fr.groupecraft.duel.duels.Duel;
import fr.groupecraft.duel.duels.DuelCmd;
import net.milkbowl.vault.economy.Economy;

public class DuelMain extends JavaPlugin {
	
	private static DuelMain instance;
	public Inventory duelInv;
	public ArrayList<Duel> waitingList=new ArrayList<Duel>();
	public ArrayList<Duel> waitAccept=new ArrayList<Duel>();
	public ArrayList<Arena> arenas= new ArrayList<Arena>();
	public Economy economy;
	public boolean canStartDuel=true;
	public FileGSON<ArrayList<Arena>> gsonArenas=new FileGSON<ArrayList<Arena>>(new File(getDataFolder(),"arenas.json"));
	//public FileGSON<Inventory> gsonInventory=new FileGSON<Inventory>(new File(getDataFolder(),"inventory.json"));
	public FilesUsing fUInventory=new FilesUsing(new File(getDataFolder(),"inventory.json"));
	
	@Override
	public void onEnable() {
		System.out.println("démarrage du système de duel");
		saveDefaultConfig();
		setupEconomy();
		loadArenas();
		instance=this;
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getCommand("duel").setExecutor(new DuelCmd());
		getCommand("arena").setExecutor(new ArenaCmd());
		loadDuelInventory();
		slotBlock();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		waitingList.clear(); waitAccept.clear();
		canStartDuel=false;
		for(int i =0; i<arenas.size();i++) {
			arenas.get(i).forceEndDuel();
		}
		saveArenas();
		saveDuelInventory();
		System.out.println("désactivation du système de duel");
		super.onDisable();
	}
	
	public static DuelMain getInstance() {
		return instance;
	}
	private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	public void saveArenas() {
		ArrayList<Arena> toSave= new ArrayList<Arena>();
		for(int i=0; i<arenas.size();i++) {
			arenas.get(i).forceEndDuel();
			toSave.add(new Arena(arenas.get(i)));
		}
		gsonArenas.serialize(toSave);
	}
	public void loadArenas() {
		arenas=gsonArenas.deSerialize();
		if(arenas==null) {
			arenas=new ArrayList<Arena>();
		}
	}
	public void saveDuelInventory() {
		ArrayList<String> temp=new ArrayList<String>();
		for(int i=0; i<5;i++) {
			if(duelInv.getItem(i)==null) {
				continue;
			}
			temp.add(String.valueOf(duelInv.getItem(i).getTypeId()));
		}
		fUInventory.writeWOK(temp);
	}
	public void loadDuelInventory() {
		Inventory temp=Bukkit.createInventory(null, 9, "§dDuel Inventory");
		ArrayList<String> FileContent= fUInventory.read();
		for(int i=0; i<5;i++) {
			if(i<temp.getSize()) {
				try {
					temp.setItem(i, new ItemStack(Integer.parseInt(FileContent.get(i))));
				} catch (Exception e) {
					temp.setItem(i, null);
				}
			}else {
				temp.setItem(i, null);
			}
			
		}
		duelInv=temp;
	}
	public void slotBlock() {
		ItemStack glass=new ItemStack(Material.STAINED_GLASS_PANE,1);
		MaterialData data=glass.getData();
		data.setData((byte)7);
		glass.setData(data);
		ItemMeta meta= glass.getItemMeta();
		meta.setDisplayName("§CRIEN");
		glass.setItemMeta(meta);
		for(int i =5; i<9;i++) {
			duelInv.setItem(i, glass);
		}
	}
	public void actualizeDuels() {
		for(int i =0; i<arenas.size();i++) {
			if(!arenas.get(i).hasDuel()) {
				boolean done=false;
				while(!(done||waitingList.size()==0)) {
					done =arenas.get(i).startDuel(waitingList.get(0));
					waitingList.remove(0);
				}
				
			}
		}
	}
	public void duelIsReady(Duel duel) {
		if(waitAccept.contains(duel)) {
			duel.ready();
			waitAccept.remove(duel);
			waitingList.add(duel);
			actualizeDuels();
		}
	}
	
}
