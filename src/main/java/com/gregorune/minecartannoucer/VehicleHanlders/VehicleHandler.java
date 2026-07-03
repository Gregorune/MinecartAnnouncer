package com.gregorune.minecartannoucer.VehicleHanlders;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.Objects;
import java.util.UUID;

public abstract class VehicleHandler {
    public final Vehicle vehicle;
    private final UUID id;
    public final UUID GetId() { return id; }
    protected VehicleHandler(UUID _id, Vehicle _vehicle)
    {
        id = _id;
        vehicle = _vehicle;
    }

    public final boolean isDirectional(Block block, Material checker, int yOffset, boolean is8sided)
    {
        boolean _4side = (block.getRelative(-1, yOffset, 0).getType() == checker ||
                block.getRelative(1, yOffset, 0).getType() == checker ||
                block.getRelative(0, yOffset, -1).getType() == checker ||
                block.getRelative(0, yOffset, 1).getType() == checker
        );
        if(is8sided)
            return (_4side ||
                    block.getRelative(1, yOffset, 1).getType() == checker ||
                    block.getRelative(-1, yOffset, -1).getType() == checker ||
                    block.getRelative(1, yOffset, -1).getType() == checker ||
                    block.getRelative(-1, yOffset, 1).getType() == checker
            );
        return _4side;
    }

    public static boolean ContainsPlayer(Vehicle vehicle)
    {
        if(vehicle.getPassengers().isEmpty())
            return false;

        boolean hasPlayer = false;
        for (var passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player) {
                hasPlayer = true;
                break;
            }
        }
        return hasPlayer;
    }

    @Override
    public int hashCode() { return Objects.hash(vehicle); }

    public abstract void Handle();
}