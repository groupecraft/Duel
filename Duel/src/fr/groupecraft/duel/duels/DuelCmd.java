package fr.groupecraft.duel.duels;

import fr.groupecraft.duel.stats.DuelPlayer;
import fr.groupecraft.duel.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.arena.Arena;
import fr.groupecraft.duel.arena.FonctionsArena;

public class DuelCmd implements org.bukkit.command.CommandExecutor {

	DuelMain main=DuelMain.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("�cVous devez �tre un joueur pour lancer un duel.");
			return false;
		}
		Player player=(Player)sender;
		if(args.length==0){
			sender.sendMessage(getDuelHelpMessage());
			return false;
		}
		if(args.length==2) {
			Player target=main.getServer().getPlayer(args[0]);
			if(target==null) {
				sender.sendMessage("�cJoueur incorrect.");
				return false;
			}//arrette la commande si elle ne corr�spond pas � la sytaxe attendue
			if(target==player) {
				sender.sendMessage("�cIl est impossible d'avoir un duel avec soi-m�me.");
				return false;
			}//termine la commande si un joueur esssai de lancer un duel avec lui-m�me
			if(!main.canStartDuel) {
				player.sendMessage("�cIl est impossible de cr�er un duel, un administrateur les a temporairement d�sactiv�s.");
				return false;
			}
			if(FonctionsDuel.hasDuel(player)) {
				player.sendMessage("�cIl est impossible d'avoir plusieurs duels � la fois.");
				return false;
			}//termine la commande si un joueur tente de lancer un duel alors qu'il est en duel
			if(FonctionsDuel.hasDuel(target)) {
				player.sendMessage("�cLe joueur avec lequel vous souhaitez avoir un duel est d�j� en duel.");
				return false;
			}//termine la commande si le jeur � qui le duel est lanc� est d�ja en duel
			int mise;
			try{
				mise=Integer.parseInt(args[1]);
			}catch(Exception e) {
				sender.sendMessage(getDuelHelpMessage());
				return false;
			}//termine la command esi la mise n'est pas rentr�
			if(mise<=0) {
				sender.sendMessage("La mise sur un duel doit �tre �dsup�rieure � 0$�r.");
				return false;
			}//termine la commande si la mise est n�gative
			if(+main.economy.getBalance(player)<=mise) {
					player.sendMessage("�cVous n'avez pas assez d'argent pour lancer ce duel.");
					return false;
				}//termine la commande si le jeur n'as pas assez d'argent pour lancer le duel
			target.sendMessage("�d"+player.getName()+" �rvous a envoy� une demande de duel avec une mise de �d"+mise+"$."
					+ "\n�r Vous pouvez l'�aaccepter�R ou la �crefuser�r avec la commande �d/duel �aaccept�r/�cdecline�r.");
			player.sendMessage("�aDemande de duel envoy�e � �d"+target.getName()+"�a avec une mise de �d"+mise+"$�a.");
			Duel duel=new Duel(player, target, mise);
			main.waitAccept.add(duel);
			return true;
			
		}else if(args[0].equalsIgnoreCase("accept")){
			Duel duel = FonctionsDuel.getDuel(player);
			if(duel==null||!duel.isWaitingAccept(player)) {
				player.sendMessage("�cVous n'avez aucune demande de duel en attente.");
				return false;
			}else if(duel.isWaitingAccept(player)) {
				if(main.economy.getBalance(player)<=duel.getMise()) {
					player.sendMessage("�cVous n'avez pas assez d'argent pour accepter ce duel.");
					duel.getPlayers()[0].sendMessage("�cVotre demande de duel a �t� annul�e.");
					main.waitAccept.remove(duel);
					return false;
				}else if(!main.canStartDuel) {
					player.sendMessage("�cLes duels sont temporairements d�sactiv�s.");
					duel.getPlayers()[0].sendMessage("�cVotre demande de duel a �t� annul�e.");
					main.waitAccept.remove(duel);
					return false;
				}else {
					main.duelIsReady(duel);
					duel.getPlayers()[0].sendMessage("�aVotre demande de duel a �t� accept�e.");
					player.sendMessage("�aDuel accept�r, pr�parez-vous au combat.");
					main.actualizeDuels();
					return false;
				}
			}
		}else if(args[0].equalsIgnoreCase("decline")) {
			Duel duel = FonctionsDuel.getDuel(player);
			if(duel==null||!duel.isWaitingAccept(player)) {
				player.sendMessage("�CVous n'avez pas de duel � refuser.");
				return false;
			}else {
				main.waitAccept.remove(duel);
				player.sendMessage("�cLe duel a bien �t� refus�.");
				duel.getPlayers()[0].sendMessage("�cVotre demande de duel a �t� refus�e.");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("getPosition")) {
			Duel duel=FonctionsDuel.getDuel(player);
			if(duel==null||duel.isStarted()||!duel.isReady()) {
				player.sendMessage("�cVous n'avez pas de duel en liste d'attente.");
				return false;
			}else {
				player.sendMessage("Votre duel est actuellement en �d"+(main.waitingList.indexOf(duel)+1)+"e�r position.");
				return true;
			}
		}else if (args[0].equalsIgnoreCase("cancel")){
			Duel duel =FonctionsDuel.getDuel(player);
			if(duel==null) {
				player.sendMessage("�CVous n'avez pas de duel en cours.");
				return false;
			}
			if(duel.isStarted()) {
				player.sendMessage("�cIl est impossible d'annuler le duel en cours.");
				return false;
			}
			if(duel.isReady()) {
				main.waitingList.remove(duel);
				player.sendMessage("�cDuel annul�.");
				if(duel.getPlayers()[0]==player){
					duel.getPlayers()[1].sendMessage("�CVotre adversaire a annul� votre duel.");
				}else{
					duel.getPlayers()[0].sendMessage("�CVotre adversaire  a annul� votre duel.");
				}
				return true;
			}else {
				main.waitAccept.remove(duel);
				player.sendMessage("�cDuel annul�.");
			}
		}else if(args[0].equalsIgnoreCase("giveUp")) {
			Duel duel =FonctionsDuel.getDuel(player);
			if(duel==null) {
				player.sendMessage("�CVous n'avez pas de duel en cours.");
				return false;
			}
			if(!duel.isStarted()) {
				player.sendMessage("�cIl est impossible d'abandonner un duel qui n'est pas en cours.");
				return false;
			}else {
				Arena arena=FonctionsArena.getArenaOfDuel(duel);
				if(duel.getPlayers()[0]==player) {
					arena.endDuel(duel.getPlayers()[1]);
					player.sendMessage("�cVous avez abandonn� le duel.");
				}else {
					arena.endDuel(duel.getPlayers()[0]);
					player.sendMessage("�cVous avez abandonn� le duel.");

				}
			}
		}else if(args[0].equalsIgnoreCase("getStats")){
			if(args.length==1){
				sender.sendMessage(StatsManager.getInstance().getStatsMessage((Player)sender));
				return true;
			}
			Player trg= Bukkit.getPlayer(args[1]);
			if(trg==null){
				sender.sendMessage("�cJoueur inconu");
				return false;
			}
			sender.sendMessage(StatsManager.getInstance().getStatsMessage(trg));
		}else {
			player.sendMessage(getDuelHelpMessage());
			return true;
		}
		
		return false;
	}
	private String getDuelHelpMessage() {
		String message="�d/duel <joueur> <mise>�R: pour envoyer une demande de duel � un joueur.\n";
		message+="�d/duel accept/decline�R: pour acc�pter/refuser une demande de duel.\n";
		message+="�d/duel getPosition�R: pour conna�tre la position de votre duel dans la file d'atente.\n";
		message+="�d/duel cancel�R: pour annuler un duel en liste d'attente.\n";
		message+="�d/duel giveUp�R: pour abandoner un duel en cours.\n";
		message+="�d/duel getStats [joueur]&r: pour voir ses stats ou celle du joueur vis�.";
		return message;
	}

}
