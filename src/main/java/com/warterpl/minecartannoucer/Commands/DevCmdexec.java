package com.warterpl.minecartannoucer.Commands;

import com.warterpl.minecartannoucer.MinecartAnnouncer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DevCmdexec
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player player && player.isOp()) {

            if (MinecartAnnouncer.msgBlocks.isEmpty()) {
                player.sendMessage("There aren't any assigned messages.");
            } else {
                player.sendMessage("Assigned messages are on:");
                for (var entry : MinecartAnnouncer.msgBlocks) {
                    player.sendMessage("> " + entry.getLocation());
                }
            }
            return true;
        }
        commandSender.sendMessage("Only players with op privileges can execute that command.");
        return false;
    }
}
