package com.warterpl.minecartannoucer;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    public static final Material Rail = Material.RAIL;
    public static final Material RailSetupMat = Material.IRON_BLOCK;
    public static final Material DirectionaRailMat = Material.BONE_BLOCK;



    public static final Material IceActivator = Material.POLISHED_BLACKSTONE_PRESSURE_PLATE;
    public static final Tag IceSetupMats = Tag.ICE;
    public static final Material DirectionalIceMat = Material.TRIPWIRE;

    public static void ReadConfigMenu(JavaPlugin plugin)
    {
        //plugin.getConfig().get("block_under_rail");
    }
}
