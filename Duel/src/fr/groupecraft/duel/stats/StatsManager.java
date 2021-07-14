package fr.groupecraft.duel.stats;

import com.google.gson.reflect.TypeToken;
import fr.groupecraft.duel.DuelMain;
import fr.groupecraft.duel.commun.FileGSON;
import fr.groupecraft.duel.duels.Duel;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class StatsManager {

    private ArrayList<DuelPlayer> players;
    private static StatsManager instance;
    private final FileGSON<ArrayList<DuelPlayer>> fileGSON= new FileGSON<ArrayList<DuelPlayer>>(new File(DuelMain.getInstance().getDataFolder(), "stats.json"));

    private StatsManager(){
        players=fileGSON.deSerialize(new TypeToken<ArrayList<DuelPlayer>>(){});
        if(players==null)players=new ArrayList<DuelPlayer>();
    }
    private DuelPlayer getPlayer(Player player){
        for(DuelPlayer temp: players){
            if(temp.getUUID()==player.getUniqueId())return temp;
        }
        return null;
    }

    public DuelPlayer getStats(Player player){
        DuelPlayer stats=getPlayer(player);
        if(stats==null)return null;
        return new DuelPlayer(stats);
    }
    public void save(){
        fileGSON.serialize(players);
    }
    public void load(){
        fileGSON.deSerialize(new TypeToken<ArrayList<DuelPlayer>>(){});
    }
    public void win(Player player, int bet){
        DuelPlayer stats=getPlayer(player);
        if(stats==null){
            stats=new DuelPlayer(player);
            players.add(stats);
        }
        stats.win(bet);
    }
    public void loose(Player player, int bet){
        DuelPlayer stats=getPlayer(player);
        if(stats==null){
            stats=new DuelPlayer(player);
            players.add(stats);
        }
        stats.loose(bet);
    }
    public void createStats(Player player){
        DuelPlayer stats= getPlayer(player);
        if(stats!=null) return;
        players.add(new DuelPlayer(player));
    }
    public void delStats(Player player){
        DuelPlayer stats= getPlayer(player);
        if(stats==null) return;
        players.remove(stats);
    }
    public void resetAllstats(){
        players=new ArrayList<DuelPlayer>();
    }
    public static StatsManager getInstance(){
        if(instance==null){
            instance=new StatsManager();
        }
        return instance;
    }
    public String getStatsMessage(Player player){
        DuelPlayer stats = getPlayer(player);
        if(stats==null)return "§cCe joueur n'a pas de statisiques actuellement";
        StringBuilder message=new StringBuilder("Statistiques de " + player.getName()+" :\n");
        message.append(" - ").append(stats.getTotalWins()).append(" victoires\n");
        message.append(" - ").append(stats.getLooses()).append(" défaite\n");
        message.append(" - ").append(stats.getTotalWins()+stats.getLooses()).append(" combats\n");
        message.append(" - ").append(stats.getWinsMoney()).append(" $ gagné lors de duels\n");
        message.append(" - ").append(stats.getLooseMoney()).append(" $ perdu lors de duels\n");
        message.append(" - ").append(stats.getTotalWinMoney()).append(" $ global gagné lors de la totalité des duel\n");
        message.append(" - ").append(stats.getTotalBet()).append(" $ mit en jeux lors de duels\n");
        return message.toString();
    }

}
