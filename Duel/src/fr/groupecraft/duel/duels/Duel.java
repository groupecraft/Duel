package fr.groupecraft.duel.duels;

import org.bukkit.entity.Player;

public class Duel {

	private Player a,b;
	private int mise;
	private boolean isReady= false;
	private boolean isStarted=false;
	
	public Duel(Player a, Player b, int mise) {
		this.a=a;
		this.b=b;
		this.mise=mise;
		
	}
	public Duel(Duel duel){
		mise=duel.getMise();
		a=duel.getPlayers()[0];
		b=duel.getPlayers()[1];
	}
	
	public void ready() {
		isReady=true;
	}
	public void start() {
		isStarted=true;
	}
	public boolean isStarted() {
		return isStarted;
	}
	
	public boolean hasPlayer(Player player) {
		return a==player||b==player;
	}
	public Player[] getPlayers() {
		Player[] players= {a,b};
		return players;
	}
	public int getMise() {
		return mise;
	}
	public boolean isReady() {
		return isReady;
	}
	public boolean isWaitingAccept(Player player) {
		return (!isReady&&b==player);
	}
}
