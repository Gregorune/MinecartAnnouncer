package com.gregorune.minecartannoucer.bookparser.views;

import org.bukkit.entity.Player;

public interface IAnnouncable {
    public long GetDurationTicks();
    public void ShowDelayed(Player player, long delay);
}
