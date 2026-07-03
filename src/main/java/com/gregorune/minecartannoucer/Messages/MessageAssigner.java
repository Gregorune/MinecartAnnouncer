package com.gregorune.minecartannoucer.Messages;

import com.gregorune.helper.Utils;
import com.gregorune.minecartannoucer.Config;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval = true)
public class MessageAssigner {
    public static void AssignMessage(PlayerInteractEvent event)
    {
        Block block = event.getClickedBlock();

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return;

        BookMeta meta = (BookMeta) item.getItemMeta();
        if (meta == null || !meta.hasPages()) return;

        if(MinecartAnnouncer.msgBlocks.contains(block))
        {
            event.getPlayer().sendMessage("This block already has a message!");
            return;
        }

        List<String> rawPages = meta.getPages();
        List<String> formattedMessages = rawPages.stream()
                .filter(s -> !s.isEmpty())
                .map(TextFormater::formatText)
                .toList();

        InsertNewEntryToDb(block, formattedMessages);

        event.getPlayer().sendMessage("Message has been assigned!");

        item.setAmount(0);
        assert block != null;
        block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0, 0.5), 20);
    }
    private static void InsertNewEntryToDb(Block block, List<String> messages)
    {
        MinecartAnnouncer.msgBlocks.add(block);
        MinecartAnnouncer.messageCache.put(Utils.getBlockKey(block), messages);
        for(int page = 0; page < messages.size(); page++)
        {
            MinecartAnnouncer.dbHandler.InsertMessage(
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getWorld().getName(),
                page,
                messages.get(page)
            );
        }
    }
    private static void RemoveEntryFromDb(Block block)
    {
        MinecartAnnouncer.msgBlocks.remove(block);
        MinecartAnnouncer.dbHandler.DeleteMessagesFromBlock(block);
    }


    public static void RemoveMessage(Block destroyedBlock, BlockBreakEvent event)
    {
        Block block = destroyedBlock.getType() == Config.Rail ||
                destroyedBlock.getType() == Config.IceActivator
                ? destroyedBlock : destroyedBlock.getRelative(0, 1, 0);

        if (MinecartAnnouncer.msgBlocks.contains(block)) {
            List<String> formatedMessages = MinecartAnnouncer.GetMessages(block);
            List<String> rawMessages = new ArrayList<>();
            for(String s : formatedMessages) rawMessages.add(TextFormater.unformatText(s));

            ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();

            if (meta != null) {
                meta.setPages(rawMessages);
                book.setItemMeta(meta);
            }

            RemoveEntryFromDb(block);
            block.getWorld().dropItemNaturally(block.getLocation(), book);

            event.getPlayer().sendMessage("Message has been deleted!");
        }
    }
}
