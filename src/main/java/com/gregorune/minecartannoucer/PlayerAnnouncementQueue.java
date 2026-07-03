package com.gregorune.minecartannoucer;

import com.gregorune.minecartannoucer.Bookparser.views.BossbarView;
import com.gregorune.minecartannoucer.Bookparser.views.IAnnouncable;
import com.gregorune.minecartannoucer.Bookparser.views.TitleView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAnnouncementQueue {

    private static final HashMap<UUID, PlayerAnnouncementQueue> queues = new HashMap<>();

    public static PlayerAnnouncementQueue For(Player player) {
        PlayerAnnouncementQueue exists = queues.get(player.getUniqueId());
        if(exists != null)
            return exists;
        exists = new PlayerAnnouncementQueue();
        queues.put(player.getUniqueId(), exists);
        return exists;
    }
    public static void Clear(Player player) {
        queues.remove(player.getUniqueId());
    }

    private long bossbarBusyUntilTick = 0L;
    private long titleBusyUntilTick = 0L;

    private long CurrentTick() {
        return Bukkit.getServer().getWorlds().get(0).getFullTime();
    }

    public void EnqueueBossbar(Player player, BossbarView view)
    { bossbarBusyUntilTick = Enqueue(player, view, bossbarBusyUntilTick); }
    public void EnqueueTitle(Player player, TitleView view)
    { titleBusyUntilTick = Enqueue(player, view, titleBusyUntilTick); }
    private long Enqueue(Player player, IAnnouncable view, long busyUntil)
    {
        long now = CurrentTick();
        if (busyUntil < now) {
            busyUntil = now;
        }
        long delay = busyUntil - now;

        view.ShowDelayed(player, delay);

        return busyUntil + view.GetDurationTicks();
    }
}
