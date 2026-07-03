package com.gregorune.minecartannoucer.Bookparser.views;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.Bookparser.BookDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BossbarView implements IAnnouncable {
    public String Title;
    public BarColor Color = BarColor.WHITE;
    public BarStyle Style = BarStyle.SOLID;

    private long Duration = 100;
    private long SecondsToTicks(long time)
    { return time * 20L; }

    public BossbarView SetVariables(String title, ArrayList<Pair<String, String>> vars)
    {
        Title = title;
        for(var variable : vars)
        {
            switch(variable.Key)
            {
                case BookDataTypes.BossbarParams.ParamTime -> SetTime(variable.Value);
                case BookDataTypes.BossbarParams.ParamColor -> SetColor(variable.Value);
                case BookDataTypes.BossbarParams.ParamStyle -> SetStyle(variable.Value);
            }
        }
        return this;
    }
    private void SetStyle(String value)
    {
        Style = switch (value)
        {
            case "DIV_6" -> BarStyle.SEGMENTED_6;
            case "DIV_10" -> BarStyle.SEGMENTED_10;
            case "DIV_12" -> BarStyle.SEGMENTED_12;
            case "DIV_20" -> BarStyle.SEGMENTED_20;
            default -> BarStyle.SOLID;
        };
    }
    private void SetColor(String value)
    {
        Color = switch (value) {
            case "RED" -> BarColor.RED;
            case "BLUE" -> BarColor.BLUE;
            case "PINK" -> BarColor.PINK;
            case "GREEN" -> BarColor.GREEN;
            case "YELLOW" -> BarColor.YELLOW;
            case "PURPLE" -> BarColor.PURPLE;
            default -> BarColor.WHITE;
        };
    }
    private void SetTime(String value)
    {
        if(value.charAt(value.length() - 1) == 's')
        {
            try
            { Duration = SecondsToTicks(Long.parseLong(value.substring(0, value.length() - 1))); }
            catch (Exception ignored)
            { Duration = 100L; }
        }
        else
        {
            try
            { Duration = Long.parseLong(value); }
            catch (Exception ignored)
            { Duration = 100L; }
        }
        if(Duration <= 0)
            Duration = 100;
    }

    private BukkitRunnable Execute(BossBar bar, Player player)
    {
        return new BukkitRunnable() {
            long timeLeft = Duration;
            final boolean needToUpdate = Title.contains(BookDataTypes.BossbarVarDisplay.TimeDisplay);
            boolean init = true;
            private void OnInit()
            { bar.addPlayer(player); init = false; }
            @Override
            public void run() {
                if(!player.isOnline())
                {
                    bar.removeAll();
                    cancel();
                    return;
                }

                if(init) OnInit();

                if (timeLeft <= 0) {
                    bar.removePlayer(player);
                    cancel();
                } else {
                    if(needToUpdate)
                        bar.setTitle(
                                Title.replace(
                                        BookDataTypes.BossbarVarDisplay.TimeDisplay,
                                        Long.toString(timeLeft / 20)
                                )
                        );
                    double progress = (double)timeLeft / (double)Duration;
                    bar.setProgress(progress);
                    timeLeft--;
                }
            }
        };
    }

    private BossBar CreateBossbar()
    { return Bukkit.createBossBar(Title, Color, Style); }

    @Override
    public long GetDurationTicks() {
        return Duration;
    }
    @Override
    public void ShowDelayed(Player player, long delay)
    {
        Execute(
                CreateBossbar(),
                player
        ).runTaskTimer(
                MinecartAnnouncer.plugin,
                delay,
                1L
        );
    }
}
