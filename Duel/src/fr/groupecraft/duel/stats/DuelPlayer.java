package fr.groupecraft.duel.stats;

import org.bukkit.entity.Player;

public class DuelPlayer {

    private int winsMoney,looses,totalBet,totalWin;
    private Player player;

    public DuelPlayer(Player player){
        this.player=player;
    }

    public void changePlayer(Player player){
        this.player=player;
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
        return player;
    }
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
    public int getTotalLooseMoney(){
        return totalBet-totalWin;
    }
}
