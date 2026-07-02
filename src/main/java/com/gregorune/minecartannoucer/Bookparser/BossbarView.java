package com.gregorune.minecartannoucer.bookparser;

import com.gregorune.minecartannoucer.Messages.DataParser;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossbarView {
    public String Name;
    public BarColor Color;
    public BarStyle Style;

    public long DurationS;
    private long SecondsToTicks(long time)
    { return time * 20L; }
    private long TicksToSeconds(long time)
    { return time / 20; }

    public boolean TickUpdate = false;

    public void Show(Player player)
    {
        BossBar bar = CreateBossbar();
        bar.addPlayer(player);

        Execute(
                bar,
                player,
                TickUpdate ? SecondsToTicks(DurationS) : DurationS
        ).runTaskTimer(
                MinecartAnnouncer.plugin,
                0L,
                TickUpdate ? 1L : 20L
        );
    }

    private BossBar CreateBossbar()
    { return Bukkit.createBossBar(Name, Color, Style); }

    private BukkitRunnable Execute(BossBar bar, Player player, long time)
    {
        return new BukkitRunnable() {
            long timeLeft = time;

            @Override
            public void run() {
                if (timeLeft < 0) {
                    bar.removePlayer(player);
                    cancel();
                } else {
                    //TO DO - FIX
                    bar.setTitle(Name.replaceAll(DataParser.BossbarRgxTimeLeftDef, Long.toString(timeLeft)));
                    double progress = (double) timeLeft / time;
                    bar.setProgress(progress);
                    timeLeft--;
                }
            }
        };
    }
}
