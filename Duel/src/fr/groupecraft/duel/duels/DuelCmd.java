package fr.groupecraft.duel.duels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.groupecraft.duel.DuelMain;

public class DuelCmd implements org.bukkit.command.CommandExecutor {

	DuelMain main=DuelMain.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("vous devez être un jouer pour lancer un duel");
			return false;
		}
		Player player=(Player)sender;
		if(args.length==2) {
			Player target=main.getServer().getPlayer(args[0]);
			if(target==null) {
				sender.sendMessage("joueur incorect");
				return false;
			}//arrette la commande si elle ne corréspond pas à la sytaxe attendue
			if(target==player) {
				sender.sendMessage("§cil est impossible d'avoir un duel avec sois-même");
				return false;
			}//termine la commande si un joueur esssai de lancer un duel avec lui-même
			if(!main.canStartDuel) {
				player.sendMessage("il est impossible de créer un duel, un administrateur les a temporairement désactivés");
				return false;
			}
			if(FonctionsDuel.hasDuel(player)) {
				player.sendMessage("il est d'avoir plusieurs duels a la fois");
				return false;
			}//termine la commande si un joueur tente de lancer un duel alors qu'il est en duel
			if(FonctionsDuel.hasDuel(target)) {
				player.sendMessage("le joueur avec lequel vous souhaité avoir un duel est déjà en duel");
				return false;
			}//termine la commande si le jeur à qui le duel est lancé est déja en duel
			int mise;
			try{
				mise=Integer.parseInt(args[1]);
			}catch(Exception e) {
				sender.sendMessage(getDuelHelpMessage());
				return false;
			}//termine la command esi la mise n'est pas rentré
			if(mise<=0) {
				sender.sendMessage("la mise sur un duel doit être supérieur à 0");
				return false;
			}//termine la commande si la mise est négative
			if(+main.economy.getBalance(player)<=mise) {
					player.sendMessage("vous n'avez pas assez d'argent pour lancer ce duel "+main.economy.getBalance(player));
					return false;
				}//termine la commande si le jeur n'as pas assez d'argent pour lancer le duel
			target.sendMessage(player.getName()+" vous a envoillé une demande de duel avec une mise de "+mise+"$"
					+ "\n vous pouvez l'accepter ou la refuser avec la commande /duel accept/decline");
			Duel duel=new Duel(player, target, mise);
			main.waitAccept.add(duel);
			return true;
			
		}else if(args[0].equalsIgnoreCase("accept")){
			Duel duel = FonctionsDuel.getDuel(player);
			if(duel==null||!duel.isWaitingAccept(player)) {
				player.sendMessage("vous n'avez pas de duel a accepter");
				return false;
			}else if(duel.isWaitingAccept(player)) {
				if(main.economy.getBalance(player)<=duel.getMise()) {
					player.sendMessage("vous n'avez pas assez d'argent pour accepter ce duel");
					duel.getPlayers()[0].sendMessage("votre demande de duel a été annulé");
					main.waitAccept.remove(duel);
					return false;
				}else if(!main.canStartDuel) {
					player.sendMessage("les duels sont temporairement désactivés");
					duel.getPlayers()[0].sendMessage("votre demande de duel a été annulé");
					main.waitAccept.remove(duel);
					return false;
				}else {
					main.duelIsReady(duel);
					player.sendMessage("duel accpété");
					main.actualizeDuels();
					return false;
				}
			}
		}else if(args[0].equalsIgnoreCase("decline")) {
			Duel duel = FonctionsDuel.getDuel(player);
			if(duel==null||!duel.isWaitingAccept(player)) {
				player.sendMessage("vous n'avez pas de duel a refuser");
				return false;
			}else {
				main.waitAccept.remove(duel);
				player.sendMessage("le duel a bien été refusé");
				duel.getPlayers()[0].sendMessage("votre demande de duel a été refusée");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("getPosition")) {
			Duel duel=FonctionsDuel.getDuel(player);
			if(duel==null||duel.isStarted()||!duel.isReady()) {
				player.sendMessage("vous n'avez pas de duel en liste d'attente");
				return false;
			}else {
				player.sendMessage("votre duel est actuellement en "+(main.waitingList.indexOf(duel)+1)+"e position");
				return true;
			}
		}else {
			player.sendMessage(getDuelHelpMessage());
			return true;
		}
		
		return false;
	}
	private String getDuelHelpMessage() {
		String message="/duel <joueur> <mise>\n";
		message+="/duel accept/decline\n";
		message+="/duel getPosition";
		return message;
	}

}
