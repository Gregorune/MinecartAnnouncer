package com.gregorune.minecartannoucer.bookparser.views;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import com.gregorune.minecartannoucer.bookparser.BookDataTypes;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TitleView implements IAnnouncable{

    public String Title;
    public String Subtitle;
    public int Duration = 60;

    public TitleView SetVariables(String title, ArrayList<Pair<String, String>> vars)
    {
        Title = title;
        for(var variable : vars)
        {
            switch (variable.Key)
            {
                case BookDataTypes.TitleParams.ParamSubtitle -> Subtitle = variable.Value;
                case BookDataTypes.TitleParams.ParamTime -> SetDuration(variable.Value);
            }
        }

        return this;
    }
    private void SetDuration(String value)
    {
        try
        { Duration = Integer.parseInt(value); }
        catch(Exception ignored)
        { Duration = 60; }
    }

    private void Execute(Player player, long delay)
    {
        new BukkitRunnable()
        {
            @Override
            public void run() {
                player.sendTitle(Title, Subtitle, 10, Duration, 10);
            }
        }.runTaskLater(MinecartAnnouncer.plugin, delay);

    }

    @Override
    public long GetDurationTicks()
    { return Duration + 20; }
    @Override
    public void ShowDelayed(Player player, long delay)
    {
        Execute(
                player,
                delay
        );
    }
}
