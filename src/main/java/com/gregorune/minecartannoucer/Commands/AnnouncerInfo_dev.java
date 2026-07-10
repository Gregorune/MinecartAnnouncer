package com.gregorune.minecartannoucer.Commands;

import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.Configurations.Permissions;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.command.CommandSender;

// announcer dev
public class AnnouncerInfo_dev {

    public static void Handle(CommandSender sender, String[] args)
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

    // get_config_vars
    private static void Get_Config_Vars_Execute(CommandSender sender)
    {
        sender.sendMessage(
                Parser.InsertFormating("Rail Activator: $(6)" + Config.GetRailActivatorMat().toString())
        );
        sender.sendMessage(
                Parser.InsertFormating("Rail Directional: $(6)" + Config.GetRailDirectionalMat().toString())
        );
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

        sender.sendMessage(
                Parser.InsertFormating("$(L)=== PROTECTION ===")
        );
        sender.sendMessage(
                Parser.InsertFormating("Protect assigned blocks: " + (Config.GetBool_ProtectAssignedBlocks() ? "$(A)true" : "$(4)false"))
        );
    }

    // get_message_positions
    private static void Get_Message_Positions_Execute(CommandSender sender)
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
}
