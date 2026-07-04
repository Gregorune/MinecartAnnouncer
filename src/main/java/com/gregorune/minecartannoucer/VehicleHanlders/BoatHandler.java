package com.gregorune.minecartannoucer.VehicleHanlders;

import com.gregorune.minecartannoucer.Configurations.Config;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.Bookparser.views.AnnouncmentVM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class BoatHandler extends VehicleHandler
{

    private final Set<Block> lastBlocksUnderBoat = new HashSet<>();
    private Location prevBoatLocation = null;

    public BoatHandler(Boat _boat, UUID uuid)
    {
        super(uuid, _boat);
    }

    @Override
    public void Handle()
    {
        if (vehicle.getPassengers().isEmpty()) return;

        HashSet<Block> currentBlocks = getBlocksUnderBoat(Config.GetIceActivatorHeightOffset());

        for (Block block : currentBlocks) {
            if (block.getType() != Config.GetIceActivatorMat() || lastBlocksUnderBoat.contains(block))
                continue;

            if (!MinecartAnnouncer.msgBlocks.contains(block))
                continue;

            if(isDirectional(
                    block,
                    Config.GetIceDirectionalMat(),
                    Config.GetIceDirectionalHeightOffset(),
                    true
            ))
            {
                if(prevBoatLocation != null)
                {
                    Vector velocity = vehicle.getLocation().toVector().subtract(prevBoatLocation.toVector());
                    velocity = velocity.normalize();

                    if(isCommingDirectional(block, velocity, Config.GetIceDirectionalHeightOffset()))
                        SendBoat(block);
                }
            }
            else
                SendBoat(block);
        }

        lastBlocksUnderBoat.clear();
        lastBlocksUnderBoat.addAll(currentBlocks);
        prevBoatLocation = vehicle.getLocation();
    }

    void SendBoat(Block block)
    {
        for (Entity entity : vehicle.getPassengers()) {
            if (entity instanceof Player player) {
                AnnouncmentVM avm = MinecartAnnouncer.GetMessageAt(block);
                if(avm != null) avm.Announce(player);
            }
        }
    }
    private HashSet<Block> getBlocksUnderBoat(int heightOffset) {
        HashSet<Block> blocks = new HashSet<>();
        BoundingBox box = vehicle.getBoundingBox();
        World world = vehicle.getWorld();

        double minX = box.getMinX() - 0.1;
        double maxX = box.getMaxX() + 0.1;
        double minZ = box.getMinZ() - 0.1;
        double maxZ = box.getMaxZ() + 0.1;
        int y = ((int) box.getCenterY()) + heightOffset;

        int startX = (int) Math.floor(minX);
        int endX = (int) Math.floor(maxX);
        int startZ = (int) Math.floor(minZ);
        int endZ = (int) Math.floor(maxZ);

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                blocks.add(world.getBlockAt(x, y, z));
            }
        }

        return blocks;
    }

    boolean isCommingDirectional(Block block, Vector velocity, int heightOffset)
    {
        double x = velocity.getX();
        double z = velocity.getZ();

        double threshold = 0.5;

        Material DirectionalIceMat = Config.GetIceDirectionalMat();

        if (z > threshold && Math.abs(x) < threshold)
            return block.getRelative(0, heightOffset, -1).getType() == DirectionalIceMat; // N
        else if (z > threshold && x > threshold)
            return block.getRelative(-1, heightOffset, -1).getType() == DirectionalIceMat; // NE
        else if (Math.abs(z) < threshold && x > threshold)
            return block.getRelative(-1, heightOffset, 0).getType() == DirectionalIceMat; // E
        else if (z < -threshold && x > threshold)
            return block.getRelative(-1, heightOffset, 1).getType() == DirectionalIceMat;// SE
        else if (z < -threshold && Math.abs(x) < threshold)
            return block.getRelative(0, heightOffset, 1).getType() == DirectionalIceMat; // S
        else if (z < -threshold && x < -threshold)
            return block.getRelative(1, heightOffset, 1).getType() == DirectionalIceMat; // SW
        else if (Math.abs(z) < threshold && x < -threshold)
            return block.getRelative(1, heightOffset, 0).getType() == DirectionalIceMat; // W
        else if (z > threshold && x < -threshold)
            return block.getRelative(1, heightOffset, -1).getType() == DirectionalIceMat; // NW

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoatHandler other)) return false;
        return this.GetId() == other.GetId();
    }
}