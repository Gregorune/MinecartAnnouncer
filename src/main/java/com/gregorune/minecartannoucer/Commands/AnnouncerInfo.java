package com.gregorune.minecartannoucer.Commands;

import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.Configurations.Permissions;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AnnouncerInfo
    implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 0) {
            commandSender.sendMessage(Parser.InsertFormating("$(4)Invalid usage, command takes at least one param"));
            return true;
        }

        String subCommand = strings[0].toLowerCase();
        switch (subCommand)
        {
            case "version" -> HandleVersion(commandSender, strings);
            case "dev" -> AnnouncerInfo_dev.Handle(commandSender, strings);
            case "my_permissions" -> HandleMyPermissions(commandSender);
            case "reload" -> HandleReload(commandSender);
            //case "12250915199x" -> DeathCounterEasterEgg(commandSender);

            default -> commandSender.sendMessage(Parser.InsertFormating("$(4)This command does not exist"));
        }

        return true;
    }

    private void DeathCounterEasterEgg(CommandSender sender)
    {
        List<String> commands = List.of(
                "scoreboard objectives add killcount deathCount {\"text\":\"☠ Death Count ☠\",\"color\":\"red\"}",
                "scoreboard objectives modify killcount numberformat styled {\"color\":\"red\"}",
                "scoreboard objectives setdisplay list killcount",
                "scoreboard players add @a killcount 0"
        );

        for (String command : commands) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command
            );
        }
    }

    // /announcer reload
    private void HandleReload(CommandSender sender)
    {
        sender.sendMessage(
                Parser.InsertFormating("$(E)Reloading config.yml...")
        );
        Config.Reload(MinecartAnnouncer.plugin);
        sender.sendMessage(
                Parser.InsertFormating("$(E)Reloading message positions...")
        );
        MinecartAnnouncer.msgBlocks = MinecartAnnouncer.dbHandler.GetAllAssignedBlocks();
        sender.sendMessage(
                Parser.InsertFormating("$(A)Finished")
        );
    }

    // /announcer my_permissions
    private void HandleMyPermissions(CommandSender sender)
    {
        List<String> builder = new ArrayList<>();
        builder.add(Parser.InsertFormating(
                "User: $(L)" + sender.getName()
        ));
        builder.add(Parser.InsertFormating(
                "Minecraft Operator: " + (sender.isOp() ? "$(A)Yes" : "$(4)No")
        ));
        builder.add(Parser.InsertFormating(
                "Protection of assigned blocks: " + (Config.GetBool_ProtectAssignedBlocks() ? "$(A)Yes" : "$(4)No")
        ));
        builder.add(PermissionToShow(sender, Permissions.GET_MESSAGES));
        builder.add(PermissionToShow(sender, Permissions.EDIT_MESSAGES));
        builder.add(PermissionToShow(sender, Permissions.DEV));

        for(String res : builder)
            sender.sendMessage(res);
    }
    private String PermissionToShow(CommandSender sender, String permission)
    {
        return Parser.InsertFormating(
                (sender.hasPermission(permission) ? "$(A)" : "$(4)") + permission
        );
    }

    // /announcer version
    private void HandleVersion(CommandSender sender, String[] args)
    {
        sender.sendMessage(
                Parser.InsertFormating("$(L)Minecart Announcer")
        );
        sender.sendMessage(
                Parser.InsertFormating("Version: $(5)" + MinecartAnnouncer.INFO.Version())
        );
        if(MinecartAnnouncer.INFO.Snapshot() != "RELEASE")
            sender.sendMessage(Parser.InsertFormating("Snapshot: $(6)" + MinecartAnnouncer.INFO.Snapshot()));
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggestions = new ArrayList<>();

        boolean isDev = commandSender.hasPermission(Permissions.DEV);
        boolean isOp = commandSender.isOp();

        if(strings.length == 1)
        {
            suggestions.add("version");
            suggestions.add("my_permissions");
            if(isDev || isOp)
            {
                suggestions.add("dev");
                suggestions.add("reload");
            }
        }

        if(strings.length == 2 && strings[0].equalsIgnoreCase("dev"))
        {
            suggestions.add("get_message_positions");
            suggestions.add("get_config_vars");
        }

        return suggestions;
    }
}
