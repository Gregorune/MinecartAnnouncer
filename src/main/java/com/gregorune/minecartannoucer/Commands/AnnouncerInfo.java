package com.gregorune.minecartannoucer.Commands;

import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.Configurations.Permissions;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.RoadSigns.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
            case "signs" -> HandleSigns(commandSender, strings);
            //case "12250915199x" -> DeathCounterEasterEgg(commandSender);

            default -> commandSender.sendMessage(Parser.InsertFormating("$(4)This command does not exist"));
        }

        return true;
    }

    private void HandleSigns(CommandSender sender, String[] args)
    {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Parser.InsertFormating("$(4)Only players can use this command"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Parser.InsertFormating("$(4)Usage: /announcer signs <type> [amount]"));
            return;
        }

        String type = args[1].toLowerCase();

        if (!SkullCreator.getSigns().containsKey(type)) {
            sender.sendMessage(Parser.InsertFormating("$(4)Unknown sign type: " + type));
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Parser.InsertFormating("$(4)Amount must be a number"));
                return;
            }

            if (amount < 1 || amount > 64) {
                sender.sendMessage(Parser.InsertFormating("$(4)Amount must be between 1 and 64"));
                return;
            }
        }

        ItemStack head = SkullCreator.getSign(type);
        head.setAmount(amount);

        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + type.replace("_", " "));
        head.setItemMeta(meta);

        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(head);
        if (!overflow.isEmpty()) {
            overflow.values().forEach(item ->
                    player.getWorld().dropItem(player.getLocation(), item));
            sender.sendMessage(Parser.InsertFormating("$(6)Inventory full, some heads dropped on the ground"));
        }

        sender.sendMessage(Parser.InsertFormating("$(2)Gave " + amount + "x " + type + " sign head"));
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
            suggestions.add("signs");
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

        if(strings.length == 2 && strings[0].equalsIgnoreCase("signs"))
        {
            suggestions.addAll(SkullCreator.getSigns().keySet());
        }

        return suggestions;
    }
}
