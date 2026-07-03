package com.gregorune.minecartannoucer;

import com.gregorune.minecartannoucer.VehicleHanlders.BoatHandler;
import com.gregorune.minecartannoucer.VehicleHanlders.MinecartHandler;
import com.gregorune.minecartannoucer.VehicleHanlders.VehicleHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class MinecraftListeners implements Listener {

    private final HashMap<UUID, VehicleHandler> vehicleHandlers = new HashMap<>();
    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        UUID vId = vehicle.getUniqueId();

        if(!VehicleHandler.ContainsPlayer(vehicle))
        {
            vehicleHandlers.remove(vId);
            return;
        }
        if(!vehicleHandlers.containsKey(vId))
        {
            if(vehicle instanceof Boat boat)
                vehicleHandlers.put(vId, new BoatHandler(boat, vId));
            if(vehicle instanceof Minecart minecart)
                vehicleHandlers.put(vId, new MinecartHandler(minecart, vId));
        }
        vehicleHandlers.get(vId).Handle();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if(!validateSetup(event.getClickedBlock()))
            return;

        MessageAssignerHandler.AssignMessage(event);
    }
    private boolean validateSetup(Block block) {
        if(block == null) return false;

        if(block.getType() == Config.Rail &&
                block.getRelative(0, -1, 0).getType() == Config.RailSetupMat)
            return true;

        return block.getType() == Config.IceActivator &&
                Config.IceSetupMats.isTagged(block.getRelative(0, -1, 0).getType());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if(block.getType() == Config.Rail || block.getRelative(0, 1, 0).getType() == Config.Rail)
            MessageAssignerHandler.RemoveMessage(block, event);
        if(block.getType() == Config.IceActivator ||
                block.getRelative(0, 1, 0).getType() == Config.IceActivator)
            MessageAssignerHandler.RemoveMessage(block, event);
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        vehicleHandlers.remove(event.getVehicle().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerAnnouncementQueue.Clear(event.getPlayer());
    }

}
