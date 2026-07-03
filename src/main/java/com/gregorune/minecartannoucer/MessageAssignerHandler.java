package com.gregorune.minecartannoucer;

import com.gregorune.minecartannoucer.Bookparser.views.AnnouncmentVM;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class MessageAssignerHandler {
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

        StringBuilder builder = new StringBuilder();
        for (String str : rawPages)
        {
            builder.append(str).append(Config.PageSeparator);
        }
        MinecartAnnouncer.SaveNewAnnouncment(block, builder.toString());

        event.getPlayer().sendMessage("Message has been assigned!");

        item.setAmount(0);
        assert block != null;
        block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0, 0.5), 20);
    }

    public static void RemoveMessage(Block destroyedBlock, BlockBreakEvent event)
    {
        Block block = destroyedBlock.getType() == Config.Rail ||
                destroyedBlock.getType() == Config.IceActivator
                ? destroyedBlock : destroyedBlock.getRelative(0, 1, 0);

        if(!MinecartAnnouncer.msgBlocks.contains(block))
            return;

        AnnouncmentVM msg = MinecartAnnouncer.GetMessageAt(block);
        if(msg == null)
            return;

        String[] rawMessages = msg.RawContent.split(Config.PageSeparator);

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta != null) {
            meta.setPages(rawMessages);
            book.setItemMeta(meta);
        }

        MinecartAnnouncer.RemoveAnnouncment(block);

        block.getWorld().dropItemNaturally(block.getLocation(), book);
        event.getPlayer().sendMessage("Message has been deleted!");
    }
}
