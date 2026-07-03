package com.gregorune.minecartannoucer.bookparser.views;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.bookparser.BookDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BossbarView {
    public String Name;
    public BarColor Color;
    public BarStyle Style;

    public long Duration;
    private long SecondsToTicks(long time)
    { return time * 20L; }

    public boolean TickUpdate = false;

    public void Show(Player player)
    {
        BossBar bar = CreateBossbar();
        bar.addPlayer(player);

        Execute(
                bar,
                player,
                TickUpdate ? Duration : SecondsToTicks(Duration)
        ).runTaskTimer(
                MinecartAnnouncer.plugin,
                0L,
                1L
        );
    }
    public BossbarView SetVariables(ArrayList<Pair<String, String>> vars)
    {
        for(var variable : vars)
        {
            switch(variable.Key)
            {
                case BookDataTypes.BossbarParams.ParamTime:
                    SetTime(variable.Value);
                    break;
                case BookDataTypes.BossbarParams.ParamColor:
                    break;
                case BookDataTypes.BossbarParams.ParamStyle:
                    break;
            }
        }
        return this;
    }
    private void SetTime(String value)
    {
        if(value.charAt(value.length() - 1) == 's')
        {
            TickUpdate = false;
            try
            { Duration = SecondsToTicks(Long.parseLong(value)); }
            catch (Exception ignored)
            {  }
        }
        else
        {
            try
            { Duration = Long.parseLong(value); }
            catch (Exception ignored)
            {  }
        }
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
