package com.gregorune.minecartannoucer;

import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Bookparser.views.AnnouncementVM;
import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.Configurations.Permissions;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class MessageAssignerHandler {
    public static void AssignMessage(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if(!(player.hasPermission(Permissions.EDIT_MESSAGES) || player.isOp()))
            return;

        Block block = event.getClickedBlock();

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return;

        BookMeta meta = (BookMeta) item.getItemMeta();
        if (meta == null || !meta.hasPages()) return;

        if(MinecartAnnouncer.msgBlocks.contains(block))
        {
            event.getPlayer().sendMessage(
                    Parser.InsertFormating("$(E)This block already has a message!")
            );
            return;
        }

        List<String> rawPages = meta.getPages();

        StringBuilder builder = new StringBuilder();
        for (String str : rawPages)
        {
            builder.append(str).append(Config.PageSeparator);
        }
        MinecartAnnouncer.SaveNewAnnouncement(block, builder.toString());

        player.sendMessage("Message has been assigned!");

        item.setAmount(0);
        assert block != null;
        block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 1, 0.5), 40);
    }

    public static void RemoveMessage(Block destroyedBlock, BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(!(player.hasPermission(Permissions.EDIT_MESSAGES) || player.isOp()))
        {
            if(Config.GetBool_ProtectAssignedBlocks())
            {
                player.sendMessage(Parser.InsertFormating(
                        "$(4)You do not have permissions to remove this block!"
                ));
                event.setCancelled(true);
            }
            return;
        }

        Block relativeAbove = destroyedBlock.getRelative(0, 1, 0);
        Block block = Config.RailsMats.isTagged(relativeAbove.getType()) ? relativeAbove : destroyedBlock;

        if(!MinecartAnnouncer.msgBlocks.contains(block))
            return;

        AnnouncementVM msg = MinecartAnnouncer.GetMessageAt(block);
        if(msg == null)
            return;

        MinecartAnnouncer.RemoveAnnouncement(block);

        String[] rawMessages = msg.RawContent.split(Config.PageSeparator, -1);
        if(rawMessages.length > 100)
        {
            player.sendMessage(Parser.InsertFormating(
                    "$(E)!!! CORRUPTED DATA !!! - $(R)Amount of pages exceeds 100, book will not drop"
            ));
            return;
        }

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta != null) {
            meta.setPages(rawMessages);
            book.setItemMeta(meta);
        }



        block.getWorld().dropItemNaturally(block.getLocation(), book);
        player.sendMessage("Message has been deleted!");
    }
}
