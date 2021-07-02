package fr.groupecraft.duel.arena;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.duels.Duel;

public class ArenaCmd implements CommandExecutor {

	private DuelMain main= DuelMain.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage("�cLe processeur SpaceCore ne reconna�t pas le langage extraterrestre !");
			return false;
		}
		if(args.length==0||args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(getHelpMessage());
		}else if(args[0].equalsIgnoreCase("arenaList")) {
			sender.sendMessage("les ar�nes :");
			if(main.arenas.size()==0) return false;
			for(int i =0; i<main.arenas.size();i++) {
				sender.sendMessage(" - "+main.arenas.get(i).getName());
			} return true;
		}else if(args[0].equalsIgnoreCase("add")&&args.length==8) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("�cCous devez �tre un joueur pour pouvoir executer cette commande.");
				return false;
			}
			if(FonctionsArena.getArena(args[1])!=null) {
				sender.sendMessage("�cCette ar�ne existe d�j�.");
				return false;
			}
			Player player=(Player)sender;
			Location tpA,tpB;
			try {
				tpA=new Location(player.getWorld(),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));
				tpB=new Location(player.getWorld(),Integer.parseInt(args[5]),Integer.parseInt(args[6]),Integer.parseInt(args[7]));
			}catch(Exception e) {
				sender.sendMessage("�cCoordonn�es invalides.");
				return false;
			}
			main.arenas.add(new Arena(args[1], tpA,tpB));
			sender.sendMessage("�aL'ar�ne a �t� cr��e.");
			return true;
		}else if (args[0].equalsIgnoreCase("toggle")) {
			main.canStartDuel=!main.canStartDuel;
			if(main.canStartDuel) {
				sender.sendMessage("�aIl est maintenant possible de cr�er des duels.");
				main.getServer().broadcastMessage("�d�lSpace�5�lSky �7� �f�lLa fonction �d/duel �f�lest d�sormais r�activ�.");
				return true;
			}else {
				sender.sendMessage("Il est maintetant impossible de cr�er de nouveaux duels.");
				main.getServer().broadcastMessage("�d�lSpace�5�lSky �7� �f�lLa fonction �d/duel �f�lest d�sormais d�sactiv�.");
				main.waitAccept.clear(); main.waitingList.clear();
				return true;
			}
		}else if (args[0].equalsIgnoreCase("remove")&& args.length==2) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'�xiste pas.");
				return false;
			}
			main.arenas.remove(arena);
			sender.sendMessage("L'ar�ne �d"+arena.getName()+"�R � �t� supprim�e.");
			return true;
		}else if(args[0].equalsIgnoreCase("setWorld")&&args.length==2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("�cVous devez �tre un joueur pour pouvoir ex�cuter cette commande.");
				return false;
			}
			Player player=(Player)sender;
			Arena arena= FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cAr�ne inconnue.");
				return false;
			}
			arena.setWorld(player.getWorld());
			player.sendMessage("Le monde de �d"+arena.getName()+"�r � bien �t� modifi�.");
			return true;
		}else if(args[0].equalsIgnoreCase("setTpA")&&args.length==5) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'existe pas.");
				return false;
			}
			int x,y,z;
			try {
				x=Integer.parseInt(args[2]);
				y=Integer.parseInt(args[3]);
				z=Integer.parseInt(args[4]);
			} catch (Exception e) {
				sender.sendMessage("�cCoordon�es invalides.");
				return false;
			}
			arena.setTpA(new Location(null,x,y,z));
			sender.sendMessage("Le point de t�l�portation a bien �t� modifi�.");
			return true;
		}else if(args[0].equalsIgnoreCase("setTpB")&&args.length==5) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'existe pas.");
				return false;
			}
			int x,y,z;
			try {
				x=Integer.parseInt(args[2]);
				y=Integer.parseInt(args[3]);
				z=Integer.parseInt(args[4]);
			} catch (Exception e) {
				sender.sendMessage("�cCoordon�es invalides.");
				return false;
			}
			arena.setTpB(new Location(null,x,y,z));
			sender.sendMessage("Le point de t�l�portation a bien �t� modifi�.");
			return true;
		}else if(args[0].equalsIgnoreCase("save")) {
			main.saveArenas();
			sender.sendMessage("�aAr�nes sauvegard�es.");
			return true;
		}else if(args[0].equalsIgnoreCase("load")) {
			main.loadArenas();
			sender.sendMessage("�aAr�nes charg�es.");
			return true;
		}else if(args[0].equalsIgnoreCase("setDuelInventory")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("�cVous devez �tre un joueur pour ex�cuter cette commande.");
				return false;
			}
			Player player =(Player)sender;
			if(main.duelInv==null) {
				main.duelInv=Bukkit.createInventory(null, 9, "�dDuel Inventory");
			}
			player.openInventory(main.duelInv);
		}else if(args[0].equalsIgnoreCase("getWaitingList")) {
			if(main.waitingList.size()==0) {
				sender.sendMessage("�cIl n'y a personne en liste d'attente.");
				return true;
			}else {
				sender.sendMessage("Les duels en file d'attente sont :");
				Duel temp;
				for(int i=0; i<main.waitingList.size();i++) {
					temp=main.waitingList.get(i);
					sender.sendMessage(" - "+temp.getPlayers()[0]+" vs "+ temp.getPlayers()[1]);
				}
			}
		}else if(args[0].equalsIgnoreCase("clear")) {
			main.arenas=new ArrayList<Arena>();
			sender.sendMessage("�cLes ar�nes on �t� supprim�es.");
		}else if(args[0].equalsIgnoreCase("forceEndDuel")&&args.length==2) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cAr�ne incorrecte.");
				return false;
			}
			arena.forceEndDuel();
			sender.sendMessage("�CDuel arr�t�.");
		}else if(args[0].equalsIgnoreCase("actualizeDuels")){
			main.actualizeDuels();
			sender.sendMessage("�aDuel actualis�.");
		}else{
			sender.sendMessage(getHelpMessage());
		}
		return false;
	}
	private String getHelpMessage() {
		StringBuilder builder= new StringBuilder();
		builder.append("�d/arena help�R: pour afficher l'aide du /arena.\n");
		builder.append("�d/arena arenaList�R: pour afficher la liste des ar�nes de duel.\n");
		builder.append("�d/arena add <nom> <x1> <y1> <z1> <x2> <y2> <z2> �R: pour cr�er une nouvelle ar�ne.\n");
		builder.append("�D/arena toggle�r: pour activer/d�sactiver la possiblilit� de cr�er de nouveaux duels.\n");
		builder.append("�d/arena remove <ar�ne>�r: pour supprimer une ar�ne.\n");
		builder.append("�d/arena setWorld <ar�ne>�r: pour d�finir le monde de t�l�opration de l'ar�ne au monde o� le joueur se trouve.\n");
		builder.append("�d/arena <setTpA||setTpB> <ar�ne> <x> <y> <z>�r: pour d�finir un des endroit o� le joueur est t�l�port� lors d'un duel.\n");
		builder.append("�D/arena save�r: pour sauvegarder les ar�nes.\n");
		builder.append("�d/arena load�r: pour charger les ar�nes sauvegard�es.\n");
		builder.append("�d/arena setDuelInventory�r: pour configurer les items pour les duels.\n");
		builder.append("�d/arena getWaitingList�R: pour voir tous les duels en liste d'attente.\n");
		builder.append("�d/arena clear�r: pour supprimer toutes les ar�nes.\n");
		builder.append("�d/arena forceEndDuel <ar�ne>�R: pour arr�ter un duel en cours.\n");
		builder.append("�d/arena artualizeDuels�r: pour mettre un duel dans toutes les ar�nes qui n'en ont pas.");
		return builder.toString();
	}

}
