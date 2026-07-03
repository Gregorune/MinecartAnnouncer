package com.gregorune.minecartannoucer.Bookparser.views;

import com.gregorune.minecartannoucer.PlayerAnnouncementQueue;
import com.gregorune.minecartannoucer.Bookparser.Parser;
import com.gregorune.minecartannoucer.Bookparser.BookDataTypes;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AnnouncmentVM {
    public final ArrayList<BossbarView> Bossbars = new ArrayList<>();
    public final ArrayList<TitleView> Titles = new ArrayList<>();
    public final String PlainText;

    public final String RawContent;

    public AnnouncmentVM(String contentToParse)
    {
        RawContent = contentToParse;

        Parser.ParserResult result = Parser.Parse(contentToParse);

        PlainText = result.plainText;
        for(var decl : result.declarations)
        {
            switch (decl.Name())
            {
                case BookDataTypes.BossbarDecl -> Bossbars.add(new BossbarView().SetVariables(decl.Value(), decl.Params()));
                case BookDataTypes.TitleDecl -> Titles.add(new TitleView().SetVariables(decl.Value(), decl.Params()));
            }
        }
    }

    public void Announce(Player player)
    {
        if (PlainText != null && !PlainText.trim().isEmpty()) {
            player.sendMessage(PlainText);
        }

        var queue = PlayerAnnouncementQueue.For(player);

        for (var bb : Bossbars)
            queue.EnqueueBossbar(player, bb);
        for(var title : Titles)
            queue.EnqueueTitle(player, title);
    }
}
