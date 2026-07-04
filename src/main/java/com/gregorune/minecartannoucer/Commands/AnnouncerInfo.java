package com.gregorune.minecartannoucer.Commands;

import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Configurations.Commands;
import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.Configurations.Permissions;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            case "dev" -> HandleDev(commandSender, strings);
            case "my_permissions" -> HandleMyPermissions(commandSender);
            case "reload" -> HandleReload(commandSender);

            default -> commandSender.sendMessage(Parser.InsertFormating("$(4)This command does not exist"));
        }

        return true;
    }

    private void HandleReload(CommandSender sender)
    {
        sender.sendMessage(
                Parser.InsertFormating("$(E)Reloading config.yml...")
        );
        Config.Reload(MinecartAnnouncer.plugin);
        sender.sendMessage(
                Parser.InsertFormating("$(A)Finished")
        );
    }

    private void HandleMyPermissions(CommandSender sender)
    {
        List<String> builder = new ArrayList<>();
        builder.add(Parser.InsertFormating(
                "User: $(L)" + sender.getName()
        ));
        builder.add(Parser.InsertFormating(
                "Minecraft Operator: " + (sender.isOp() ? "$(A)Yes" : "$(4)No")
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

    private void HandleVersion(CommandSender sender, String[] args)
    {
        sender.sendMessage(
                Parser.InsertFormating("$(L)Minecart Announcer")
        );
        sender.sendMessage(
                Parser.InsertFormating("Version: $(5)" + MinecartAnnouncer.PluginInfo.Version)
        );
        if(MinecartAnnouncer.PluginInfo.Snapshot != "RELEASE")
            sender.sendMessage(Parser.InsertFormating("Snapshot: $(6)" + MinecartAnnouncer.PluginInfo.Snapshot));
    }

    private void HandleDev(CommandSender sender, String[] args)
    {
        if(!(sender.hasPermission(Permissions.DEV) || sender.isOp()))
        {
            sender.sendMessage(Parser.InsertFormating("$(4)You don't have permission to use that command"));
            return;
        }

        if(args.length < 2)
        {
            sender.sendMessage(Parser.InsertFormating("$(4)Invalid usage, action not specified"));
            return;
        }
        String action = args[1].toLowerCase();
        switch (action)
        {
            case "get_message_positions" -> Get_Message_Positions_Execute(sender);
            case "get_config_vars" -> Get_Config_Vars_Execute(sender);
            default -> sender.sendMessage(Parser.InsertFormating("$(4)Invalid usage, action does not exist"));
        }
    }
    private void Get_Config_Vars_Execute(CommandSender sender)
    {
        sender.sendMessage(
                Parser.InsertFormating("Rail Activator: $(6)" + Config.GetRailActivatorMat().toString())
        );
        sender.sendMessage(
                Parser.InsertFormating("Rail Directional: $(6)" + Config.GetRailDirectionalMat().toString())
        );
        sender.sendMessage("");
        sender.sendMessage(
                Parser.InsertFormating("Ice Activator: $(6)" + Config.GetIceActivatorMat().toString())
        );
        sender.sendMessage(
                Parser.InsertFormating("Ice Activator yOffset: $(6)" + Config.GetIceActivatorHeightOffset())
        );
        sender.sendMessage(
                Parser.InsertFormating("Ice Directional: $(6)" + Config.GetIceDirectionalMat().toString())
        );
        sender.sendMessage(
                Parser.InsertFormating("Ice Directional yOffset: $(6)" + Config.GetIceDirectionalHeightOffset())
        );
    }
    private void Get_Message_Positions_Execute(CommandSender sender)
    {
        if (MinecartAnnouncer.msgBlocks.isEmpty()) {
            sender.sendMessage(Parser.InsertFormating("$(6)There aren't any assigned messages."));
        } else {
            sender.sendMessage(Parser.InsertFormating("$(B)Assigned messages are on:"));
            for (var entry : MinecartAnnouncer.msgBlocks) {
                sender.sendMessage("> " + entry.getLocation());
            }
        }
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
