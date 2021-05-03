package fr.groupecraft.duel.arena;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.duels.Duel;

public class FonctionsArena {
	
	public static DuelMain main= DuelMain.getInstance();
	
	public static Arena getArena(String name) {
		Arena temp;
		for(int i=0;i<main.arenas.size();i++) {
			temp=main.arenas.get(i);
			if(temp.getName().equalsIgnoreCase(name)) {
				return temp;
			}
		}
		return null;
	}
	public static Arena getArenaOfDuel(Duel duel) {
		for(int i=0; i<main.arenas.size(); i++) {
			if(main.arenas.get(i).getDuel()==duel) {
				return main.arenas.get(i);
			}
		}
		return null;
	}
}
