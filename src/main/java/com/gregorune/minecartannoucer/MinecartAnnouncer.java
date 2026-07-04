package com.gregorune.minecartannoucer;

import com.gregorune.helper.Utils;
import com.gregorune.minecartannoucer.Commands.AnnouncerInfo;

import com.gregorune.minecartannoucer.Bookparser.views.AnnouncmentVM;
import com.gregorune.minecartannoucer.Configurations.Commands;
import com.gregorune.minecartannoucer.Configurations.Config;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.cache.*;
import javax.annotation.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static class PluginInfo
    {
        public static final String Version = "3.1.0";
        public static final String Snapshot = "RELEASE";
    }

    public static JavaPlugin plugin;

    public static final DatabaseHandler dbHandler = new DatabaseHandler();
    public static Cache<String, AnnouncmentVM> messageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public static class TickCounter {
        private static long currentTick = 0L;

        public static void Start(Plugin plugin) {
            Bukkit.getScheduler().runTaskTimer(plugin, () -> currentTick++, 0L, 1L);
        }

        public static long Get() {
            return currentTick;
        }
    }


    public static HashSet<Block> msgBlocks = new HashSet<>();
    public MinecartAnnouncer()
    {
        plugin = this;
    }

    @Nullable
    public static AnnouncmentVM GetMessageAt(Block block)
    {
        AnnouncmentVM cached = messageCache.getIfPresent(Utils.getBlockKey(block));
        if(cached != null) return cached;

        AnnouncmentVM fromDb = dbHandler.GetMessageAt(block);
        if(fromDb != null) messageCache.put(Utils.getBlockKey(block), fromDb);
        return fromDb;
    }

    public static void SaveNewAnnouncment(Block block, String message)
    {
        msgBlocks.add(block);
        messageCache.put(Utils.getBlockKey(block), new AnnouncmentVM(message));
        dbHandler.InsertMessage(
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getWorld().getName(),
                message
        );
    }
    public static void RemoveAnnouncment(Block block)
    {
        msgBlocks.remove(block);
        messageCache.invalidate(Utils.getBlockKey(block));
        dbHandler.DeleteMessageFromBlock(block);
    }


    @Override
    public void onEnable() {
        dbHandler.Connect();
        msgBlocks = dbHandler.GetAllAssignedBlocks();

        Config.Load(this);
        RegisterCommands();

        TickCounter.Start(this);

        getServer().getPluginManager().registerEvents(new MinecraftListeners(), this);
    }

    private void RegisterCommands()
    {
        PluginCommand announcerInfoCMD = getCommand(Commands.announcer);
        if(announcerInfoCMD != null)
        {
            AnnouncerInfo announcerInfoExec = new AnnouncerInfo();
            announcerInfoCMD.setExecutor(announcerInfoExec);
            announcerInfoCMD.setTabCompleter(announcerInfoExec);
        }
    }

    @Override
    public void onDisable() {
        dbHandler.Disconnect();
    }
}
