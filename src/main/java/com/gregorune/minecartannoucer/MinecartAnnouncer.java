package com.gregorune.minecartannoucer;

import com.gregorune.helper.Utils;
import com.gregorune.minecartannoucer.Commands.DevCmdexec;

import com.gregorune.minecartannoucer.bookparser.Parser;
import com.gregorune.minecartannoucer.bookparser.views.AnnouncmentVM;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.cache.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;

    public static final DatabaseHandler dbHandler = new DatabaseHandler();
    public static Cache<String, AnnouncmentVM> messageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();



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

        this.getCommand("dev_showMessagesPositions").setExecutor(new DevCmdexec());

        getServer().getPluginManager().registerEvents(new MinecraftListeners(), this);
    }

    @Override
    public void onDisable() {
        dbHandler.Disconnect();
    }
}
