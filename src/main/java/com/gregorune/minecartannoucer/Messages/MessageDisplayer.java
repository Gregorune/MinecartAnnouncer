package com.gregorune.minecartannoucer.Messages;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.*;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval = true)
public class MessageDisplayer {

    @Deprecated(forRemoval = true)
    public void SendMessage(Player player, List<String> messages)
    {
        if (player == null || messages == null || messages.isEmpty()) return;

        HandlePage(player, messages);
    }

    @Deprecated(forRemoval = true)
    void HandlePage(Player player, List<String> messages)
    {
        StringBuilder message = new StringBuilder();
        ArrayList<Pair<String, String>> titleCards = new ArrayList<>();

        boolean isFirstUndefinedPage = true;

        for (String s : messages) {
            if (!s.equals(messages.getFirst()) &&
                    !s.startsWith(DataParser.TitlePageDef) &&
                    !isFirstUndefinedPage)
                message.append("\n§r");

            if (s.startsWith(DataParser.TitlePageDef))
                titleCards.add(DataParser.ParseTitlePage(s));
            else if (s.startsWith(DataParser.BossbarDef))
                bossbarCards.add(DataParser.ParseBossbarPage(s));
            else if (!s.isEmpty()) {
                message.append(s);
                isFirstUndefinedPage = false;
            }
        }

        sendNonEmptyMessage(player, message.toString());
        HandleTitlePages(titleCards, player);
    }
    void sendNonEmptyMessage(Player player, String message) {
        if (message != null && !message.trim().isEmpty()) {
            player.sendMessage(message);
        }
    }

    @Deprecated(forRemoval = true)
    void HandleTitlePages(ArrayList<Pair<String, String>> titleCards, Player player)
    {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {

                if (index < titleCards.size()) {
                    Pair<String, String> card = titleCards.get(index);
                    String title = card.first;
                    String subtitle = card.second;

                    player.sendTitle(title, subtitle, 10, 40, 10);
                    index++;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(MinecartAnnouncer.plugin, 0L, 60L);
    }

    @Deprecated(forRemoval = true)
    private void displayNextBossbar(int index) {
        String text = settings.name.replaceAll(DataParser.BossbarRgxTimeLeftDef, Integer.toString(settings.duration));
    }
}
