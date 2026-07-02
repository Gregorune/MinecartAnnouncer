package com.warterpl.helper;

import org.bukkit.block.Block;

public class Utils {
    public static String getBlockKey(Block block) {
        return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();
    }
}
