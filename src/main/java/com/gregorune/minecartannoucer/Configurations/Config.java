package com.gregorune.minecartannoucer.Configurations;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    public static final String PageSeparator = "\u001F";
    public static final Tag<Material> IceSetupMats = Tag.ICE;
    public static final Tag<Material> RailsMats = Tag.RAILS;

    private static Material BlockUnderRail = Material.IRON_BLOCK;
    public static Material GetRailActivatorMat() { return BlockUnderRail; }
    private static void LoadBlockUnderRail()
    {
        BlockUnderRail = Material.matchMaterial(
                config.getString(ConfigPaths.BlockSettings.RailSetup, "iron_block")
        );
        if(BlockUnderRail == null) BlockUnderRail = Material.IRON_BLOCK;
    }

    private static Material BlockForDirectionalSetup = Material.BONE_BLOCK;
    public static Material GetRailDirectionalMat() { return BlockForDirectionalSetup; }
    private static void LoadBlockForDirectionalSetup()
    {
        BlockForDirectionalSetup = Material.matchMaterial(
                config.getString(ConfigPaths.BlockSettings.RailDirectional, "bone_block")
        );
        if(BlockForDirectionalSetup == null) BlockForDirectionalSetup = Material.BONE_BLOCK;
    }

    private static Material IceActivator = Material.POLISHED_BLACKSTONE_PRESSURE_PLATE;
    public static Material GetIceActivatorMat() { return IceActivator; }
    private static void LoadIceActivatorMat()
    {
        IceActivator = Material.matchMaterial(
                config.getString(ConfigPaths.BlockSettings.BoatSetup, "polished_blackstone_pressure_plate")
        );
        if(IceActivator == null) IceActivator = Material.POLISHED_BLACKSTONE_PRESSURE_PLATE;
    }

    private static Material IceDirectionalMat = Material.TRIPWIRE;
    public static Material GetIceDirectionalMat() { return IceDirectionalMat; }
    private static void LoadIceDirectionalMat()
    {
        IceDirectionalMat = Material.matchMaterial(
                config.getString(ConfigPaths.BlockSettings.BoatDirectional, "tripwire")
        );
        if(IceDirectionalMat == null) IceDirectionalMat = Material.TRIPWIRE;
    }

    private static int IceActivatorYOffset = 0;
    public static int GetIceActivatorHeightOffset() { return IceActivatorYOffset; }
    private static void LoadIceActivatorHeightOffset()
    {
        IceActivatorYOffset = config.getInt(ConfigPaths.BlockSettings.BoatSetupYOffset, 0);
    }

    private static int IceDirectionalYOffset = 0;
    public static int GetIceDirectionalHeightOffset() { return IceDirectionalYOffset; }
    private static void LoadIceDirectionalHeightOffset()
    {
        IceDirectionalYOffset = config.getInt(ConfigPaths.BlockSettings.BoatDirectionalYOffset, 0);
    }


    private static FileConfiguration config;
    public static void Load(JavaPlugin plugin)
    {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        SetVariables();
    }

    public static void Reload(JavaPlugin plugin)
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        SetVariables();
    }

    private static void SetVariables()
    {
        LoadBlockUnderRail();
        LoadBlockForDirectionalSetup();

        LoadIceActivatorMat();
        LoadIceActivatorHeightOffset();

        LoadIceDirectionalMat();
        LoadIceDirectionalHeightOffset();
    }

    public static void ReadConfigMenu(JavaPlugin plugin)
    {
        //plugin.getConfig().get("block_under_rail");
    }
}
