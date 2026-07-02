package com.warterpl.minecartannoucer;

import com.warterpl.helper.Utils;
import com.warterpl.minecartannoucer.Commands.DevCmdexec;
import com.warterpl.minecartannoucer.Messages.MessageDisplayer;

import com.warterpl.minecartannoucer.VehicleHanlders.VehicleHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.cache.*;

import java.util.List;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;

    private static final MessageDisplayer messageDisplayer = new MessageDisplayer();
    public static MessageDisplayer getMessageDisplayer() { return messageDisplayer; }

    public static final DatabaseHandler dbHandler = new DatabaseHandler();
    public static Cache<String, List<String>> messageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public static HashSet<Block> msgBlocks = new HashSet<>();
    public MinecartAnnouncer()
    {
        plugin = this;
    }

    public static List<String> GetMessages(Block block)
    {
        List<String> cached = messageCache.getIfPresent(Utils.getBlockKey(block));
        if (cached != null) return cached;

        List<String> fromDb = dbHandler.GetMessagesAt(block);
        if (!fromDb.isEmpty()) messageCache.put(Utils.getBlockKey(block), fromDb);
        return fromDb;
    }


    @Override
    public void onEnable() {
        dbHandler.Connect();
        msgBlocks = dbHandler.GetAllAssignedBlocks();

        this.getCommand("dev_showMessagesPositions").setExecutor(new DevCmdexec());

        getServer().getPluginManager().registerEvents(new MinecraftListeners(), this);
    }

    @Override
    public void onDisable() {
        dbHandler.Disconnect();
    }
}
