package fr.groupecraft.duel.commun;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.arena.Arena;
import fr.groupecraft.duel.arena.FonctionsArena;
import fr.groupecraft.duel.duels.Duel;
import fr.groupecraft.duel.duels.FonctionsDuel;

public class EventListener implements Listener {

	private DuelMain main= DuelMain.getInstance();
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getInventory().getName()=="§dDuel Inventory") {
			ItemStack item=event.getCurrentItem();
			if(item==null) {
				return;
			}
			if(item.getType()==Material.STAINED_GLASS_PANE&&item.getItemMeta().getDisplayName()=="§CRIEN") {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player= event.getEntity();
		Duel duel = FonctionsDuel.getDuel(player);
		if(duel==null) return;
		if(duel.isStarted()) {
			Arena arena= FonctionsArena.getArenaOfDuel(duel);
			event.setDroppedExp(0);
			event.setKeepInventory(true);
			if(duel.getPlayers()[0]==player) {
				arena.endDuel(duel.getPlayers()[1]);
			}else if(duel.getPlayers()[1]==player) {
				arena.endDuel(duel.getPlayers()[0]);
			}else {
				try {
					throw new DuelExeption();
				} catch (Exception e) {e.printStackTrace();}
			}
		}
	}
	
	@EventHandler
	public void OnDisconect(PlayerQuitEvent event) {
		Player player=event.getPlayer();
		Duel duel = FonctionsDuel.getDuel(player);
		Arena arena= FonctionsArena.getArenaOfDuel(duel);
		if(duel==null)return;
		if(duel.isStarted()) {
			if(arena.getDuel()!=duel) {
				try {
					throw new DuelExeption();
				} catch (Exception e) {e.printStackTrace();}
				return;
			}
			if(duel.getPlayers()[0]==player) {
				arena.endDuel(duel.getPlayers()[1]);
			}else if(duel.getPlayers()[1]==player) {
				arena.endDuel(duel.getPlayers()[0]);
			}else {
				try {
					throw new DuelExeption();
				} catch (Exception e) {e.printStackTrace();}
			}
		}else if(duel.isReady()) {
			main.waitingList.remove(duel);
			if(duel.getPlayers()[0]==player) {
				duel.getPlayers()[1].sendMessage("votre duel a été annulé car l'autre joueur c'est déconnécter");
			}else if(duel.getPlayers()[1]==player) {
				duel.getPlayers()[0].sendMessage("votre duel a été annulé car l'autre joueur c'est déconnécter");
			}else {
				try {
					throw new DuelExeption();
				} catch (Exception e) {e.printStackTrace();}
			}
		}else {
			main.waitAccept.remove(duel);
			if(duel.getPlayers()[0]==player) {
				duel.getPlayers()[1].sendMessage("votre duel a été annulé car l'autre joueur c'est déconnécter");
			}else if(duel.getPlayers()[1]==player) {
				duel.getPlayers()[0].sendMessage("votre duel a été annulé car l'autre joueur c'est déconnécter");
			}else {
				try {
					throw new DuelExeption();
				} catch (Exception e) {e.printStackTrace();}
			}
		}
	}
}
