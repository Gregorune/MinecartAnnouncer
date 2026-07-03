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
    public String Title;
    public BarColor Color;
    public BarStyle Style;

    public long Duration;
    private long SecondsToTicks(long time)
    { return time * 20L; }

    public boolean TickUpdate = false;

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
            TickUpdate = false;
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
    }

    private BukkitRunnable Execute(BossBar bar, Player player, long time)
    {
        return new BukkitRunnable() {
            long timeLeft = time;
            boolean needToUpdate = bar.getTitle().contains(BookDataTypes.BossbarVarDisplay.TimeDisplay);
            @Override
            public void run() {
                if (timeLeft < 0) {
                    bar.removePlayer(player);
                    cancel();
                } else {
                    if(needToUpdate)
                        bar.setTitle(
                                Title.replaceAll(
                                        BookDataTypes.BossbarVarDisplay.TimeDisplay,
                                        Long.toString(timeLeft / 20)
                                )
                        );
                    double progress = (double) timeLeft / time;
                    bar.setProgress(progress);
                    timeLeft--;
                }
            }
        };
    }
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
    private BossBar CreateBossbar()
    { return Bukkit.createBossBar(Title, Color, Style); }
}
