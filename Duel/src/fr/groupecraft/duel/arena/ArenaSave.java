package fr.groupecraft.duel.arena;

import org.bukkit.Location;

public class ArenaSave {

    public Location tpA,tpB;
    public String name;
    public String worldName;

    public ArenaSave(Arena arena){
        tpA=arena.getTPs()[0];
        tpB=arena.getTPs()[1];
        name=arena.getName();
        worldName=arena.getTPs()[0].getWorld().getName();
        tpA.setWorld(null);
        tpB.setWorld(null);
    }
}
