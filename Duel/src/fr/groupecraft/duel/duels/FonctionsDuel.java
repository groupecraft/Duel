package fr.groupecraft.duel.duels;

import org.bukkit.entity.Player;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.arena.Arena;

public class FonctionsDuel {

	public static DuelMain main= DuelMain.getInstance();
	
	public static void addDuel(Duel duel) {
		if(hasDuel(duel.getPlayers()[0]) || hasDuel(duel.getPlayers()[1])) {
			return;
		}
		Arena temp2;
		for(int i=0; i<main.arenas.size(); i++) {
			temp2=main.arenas.get(i);
			if(!temp2.hasDuel()) {
				temp2.startDuel(duel);
			}
			
		}
	}
	public static boolean hasDuel(Player player) {
		return getDuel(player)!=null;
	}
	public static Duel getDuel(Player player) {
		Duel temp;
		if(main.waitingList.size()!=0) {
			for(int i=0; i<main.waitingList.size(); i++) {
				temp=main.waitingList.get(i);
				if(temp.hasPlayer(player)) return temp;
			}
		}//regarde si le joueur est un duel dans la liste d'attente
		
		if(main.waitAccept.size()!=0) {
			for(int i=0; i<main.waitAccept.size(); i++) {
				if(main.waitAccept.get(i).hasPlayer(player)) return main.waitAccept.get(i);
			}
		}//regarde si le joueur est en attente d'acceptation d'un duel
		
		Arena temp2;
		if(main.arenas.size()==0)return null;
		for(int i=0; i<main.arenas.size(); i++) {
			temp2=main.arenas.get(i);
			if(temp2.hasDuel()) {
				temp=temp2.getDuel();
				if(temp==null)continue;
				if(temp.hasPlayer(player)) return temp;
			}
			
		}//regarde si le joueur est dans un duel en cours
		return null;
	}
	
}
