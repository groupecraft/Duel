package fr.groupecraft.duel.arena;

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
				sender.sendMessage("vous devez �tre un jouer pour pouvoir executer ceci");
				return false;
			}
			if(FonctionsArena.getArena(args[1])!=null) {
				sender.sendMessage("�cCette ar�ne existe d�j�");
				return false;
			}
			Player player=(Player)sender;
			Location tpA,tpB;
			try {
				tpA=new Location(player.getWorld(),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));
				tpB=new Location(player.getWorld(),Integer.parseInt(args[5]),Integer.parseInt(args[6]),Integer.parseInt(args[7]));
			}catch(Exception e) {
				sender.sendMessage("�cCoordon�nes invalides");
				return false;
			}
			main.arenas.add(new Arena(args[1], tpA,tpB));
			sender.sendMessage("l'ar�ne a �t� cr��e");
			return true;
		}else if (args[0].equalsIgnoreCase("toggle")) {
			main.canStartDuel=!main.canStartDuel;
			if(main.canStartDuel) {
				sender.sendMessage("il est maintenant possible de cr�er des deuls");
				main.getServer().broadcastMessage("les duels on �t� r�activ�es");
				return true;
			}else {
				sender.sendMessage("il est maintetant impossible de cr�er des duels");
				main.getServer().broadcastMessage(sender.getName()+ " a temporairement d�sactiv� les duels");
				main.waitAccept.clear(); main.waitingList.clear();
				return true;
			}
		}else if (args[0].equalsIgnoreCase("remove")&& args.length==2) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'�xiste pas");
				return false;
			}
			main.arenas.remove(arena);
			sender.sendMessage("l'ar�ne "+arena.getName()+" � �t� supprim�e");
			return true;
		}else if(args[0].equalsIgnoreCase("setWorld")&&args.length==2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("�cvous devez �tre un joueur pour pouvoir executer cette commande");
				return false;
			}
			Player player=(Player)sender;
			Arena arena= FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cAr�ne inconue");
				return false;
			}
			arena.setWorld(player.getWorld());
			player.sendMessage("le monde de "+arena.getName()+" � bien �t� modifi�");
			return true;
		}else if(args[0].equalsIgnoreCase("setTpA")&&args.length==5) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'existe pas");
				return false;
			}
			int x,y,z;
			try {
				x=Integer.parseInt(args[2]);
				y=Integer.parseInt(args[3]);
				z=Integer.parseInt(args[4]);
			} catch (Exception e) {
				sender.sendMessage("�cCoordon�es invalides");
				return false;
			}
			arena.setTpA(new Location(null,x,y,z));
			sender.sendMessage("le point de t�l�portation � bien �t� modifi�");
			return true;
		}else if(args[0].equalsIgnoreCase("setTpB")&&args.length==5) {
			Arena arena=FonctionsArena.getArena(args[1]);
			if(arena==null) {
				sender.sendMessage("�cCette ar�ne n'existe pas");
				return false;
			}
			int x,y,z;
			try {
				x=Integer.parseInt(args[2]);
				y=Integer.parseInt(args[3]);
				z=Integer.parseInt(args[4]);
			} catch (Exception e) {
				sender.sendMessage("�cCoordon�es invalides");
				return false;
			}
			arena.setTpB(new Location(null,x,y,z));
			sender.sendMessage("le point de t�l�portation � bien �t� modifi�");
			return true;
		}else if(args[0].equalsIgnoreCase("save")) {
			main.saveArenas();
			sender.sendMessage("ar�nes sauvegard�es");
			return true;
		}else if(args[0].equalsIgnoreCase("load")) {
			main.loadArenas();
			sender.sendMessage("ar�nes charch�es");
			return true;
		}else if(args[0].equalsIgnoreCase("setDuelInventory")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("�cVous devez �tre un joueur pour executer cette commande");
				return false;
			}
			Player player =(Player)sender;
			if(main.duelInv==null) {
				main.duelInv=Bukkit.createInventory(null, 9, "�dDuel Inventory");
			}
			player.openInventory(main.duelInv);
		}else if(args[0].equalsIgnoreCase("getWaitingList")) {
			if(main.waitingList.size()==0) {
				sender.sendMessage("il n'y a persone en liste d'attente");
				return true;
			}else {
				sender.sendMessage("la liste des duel en liste d'attente est :");
				Duel temp;
				for(int i=0; i<main.waitingList.size();i++) {
					temp=main.waitingList.get(i);
					sender.sendMessage(" - "+temp.getPlayers()[0]+" vs "+ temp.getPlayers()[1]);
				}
			}
		}
		return false;
	}
	private String getHelpMessage() {
		String message="";
		message+="/arena help: pour afficher l'aide de la commande\n";
		message+="/arena arenaList: pour afficher la liste des ar�ne de duel\n";
		message+="/arena add <nom> <x1> <y1> <z1> <x2> <y2> <z2> : pour cr�e une nouvelle ar�ne\n";
		message+="/arena toggle: pour activer/d�sactiv� la possiblilit� de cr�er de nouveaux duels\n";
		message+="/arena remove <ar�ne>: pour supprimer une ar�ne\n";
		message+="/arena setWorld <ar�ne>: pour d�finir le monde de t�l�opration de l'ar�ne au monde o� le joueur se trouve\n";
		message+="/arena <setTpA||setTpB> <ar�ne> <x> <y> <z>: pour d�finir un des endroit o� le joueur est t�l�port� pour une ar�ne\n";
		message+="/arena save: pour sauvegarder les ar�nes\n";
		message+="/arena load: pour charg�es les ar�nes sauvegard�es\n";
		message+="/arena setDuelInventory: pour configurer les items pour les duels\n";
		message+="/arena getWaitingList: pour voir tous les duels en liste d'attente";
		return message;
	}

}