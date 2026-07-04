package com.gregorune.minecartannoucer.VehicleHanlders;

import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.Bookparser.views.AnnouncmentVM;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MinecartHandler extends VehicleHandler {
    private Block lastRail = null;

    public MinecartHandler(Minecart _minecart, UUID uuid)
    {
        super(uuid, _minecart);
    }

    @Override
    public void Handle()
    {
        if (vehicle.getPassengers().isEmpty()) return;
        Entity passenger = vehicle.getPassengers().get(0);
        if (!(passenger instanceof Player player)) return;

        Block currentBlock = vehicle.getLocation().getBlock().getRelative(0, 0, 0);

        if(lastRail == null)
        {
            lastRail = currentBlock;
            return;
        }

        boolean repeating = lastRail.equals(currentBlock);
        if (!repeating) {
            boolean directionalCondition = lastRail.getRelative(0, -1, 0).getType() == Config.GetRailDirectionalMat();
            boolean directional = isDirectional(currentBlock, Config.GetRailDirectionalMat(), -1, false);
            if (!directional || directionalCondition) {
                if (MinecartAnnouncer.msgBlocks.contains(currentBlock)) {
                    AnnouncmentVM avm = MinecartAnnouncer.GetMessageAt(currentBlock);
                    if(avm != null) avm.Announce(player);
                }
            }
        }
        lastRail = currentBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinecartHandler other)) return false;
        return this.GetId() == other.GetId();
    }
}