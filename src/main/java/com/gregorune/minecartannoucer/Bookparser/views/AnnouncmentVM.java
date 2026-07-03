package com.gregorune.minecartannoucer.bookparser.views;

import com.gregorune.minecartannoucer.bookparser.Parser;
import com.gregorune.minecartannoucer.bookparser.BookDataTypes;
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
        for (var bb : Bossbars)
            bb.Show(player);
        for(var title : Titles)
            title.Show(player);
    }
}
