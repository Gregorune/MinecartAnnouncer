package com.gregorune.minecartannoucer.Messages;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.MinecartAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.*;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;

public class MessageDisplayer {

    public void SendMessage(Player player, List<String> messages)
    {
        if (player == null || messages == null || messages.isEmpty()) return;

        HandlePage(player, messages);
    }

    void HandlePage(Player player, List<String> messages)
    {
        StringBuilder message = new StringBuilder();
        ArrayList<Pair<String, String>> titleCards = new ArrayList<>();
        ArrayList<BossbarSettings> bossbarCards = new ArrayList<>();

        boolean isFirstUndefinedPage = true;

        for (String s : messages) {
            if (!s.equals(messages.getFirst()) &&
                    !s.startsWith(DataParser.TitlePageDef) &&
                    !s.startsWith(DataParser.BossbarDef) &&
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
        HandleBossbars(bossbarCards, player);
    }
    void sendNonEmptyMessage(Player player, String message) {
        if (message != null && !message.trim().isEmpty()) {
            player.sendMessage(message);
        }
    }

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

    private void HandleBossbars(ArrayList<BossbarSettings> bossbars, Player player) {
        if (bossbars.isEmpty()) return;
        displayNextBossbar(bossbars, player, 0);
    }

    private void displayNextBossbar(ArrayList<BossbarSettings> bossbars, Player player, int index) {
        if (index >= bossbars.size()) return;

        BossbarSettings settings = bossbars.get(index);
        String text = settings.name.replaceAll(DataParser.BossbarRgxTimeLeftDef, Integer.toString(settings.duration));
        BossBar bossBar = Bukkit.createBossBar(text, settings.color, BarStyle.SOLID);
        bossBar.addPlayer(player);
    }
}
