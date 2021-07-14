package fr.groupecraft.duel.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelPlayer {

    private int winsMoney,looses,totalBet,totalWin;
    private UUID player;

    public DuelPlayer(Player player){
        this.player=player.getUniqueId();
    }
    public DuelPlayer(DuelPlayer stats){
        player=stats.player;
        winsMoney= stats.winsMoney;
        looses= stats.looses;
        totalBet=stats.totalBet;
        totalWin=stats.totalWin;
    }
    public void changePlayer(Player player){
        this.player=player.getUniqueId();
    }

    public void win(int bet){
        totalBet+=bet;
        winsMoney+=2*bet;
        totalWin++;
    }
    public void loose(int bet){
        totalBet+=bet;
        looses++;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(player);
    }
    public UUID getUUID(){return player;}
    public int getLooses() {
        return looses;
    }
    public int getTotalBet() {
        return totalBet;
    }
    public int getTotalWins() {
        return totalWin;
    }
    public int getWinsMoney() {
        return winsMoney;
    }
    public int getTotalWinMoney() {
        return totalWin-totalBet;
    }
    public int getLooseMoney(){
        return totalBet-totalWin;
    }
}
