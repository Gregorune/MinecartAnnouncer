package com.gregorune.minecartannoucer.bookparser;

import java.util.ArrayList;

public class Parser {
    private static final char[] Formats = "0123456789ABCDEFLNMOKR".toCharArray();

    public class Result
    {
        public final ArrayList<BossbarView> BossbarViews;
        public final ArrayList<TitleView> TitleViews;
        public final String ChatView;

        public Result(
                ArrayList<BossbarView> bossbars,
                ArrayList<TitleView> titles,
                String view
        )
        {
            BossbarViews = bossbars;
            TitleViews = titles;
            ChatView = view;
        }
    }
    public Result Parse(String contents)
    {
        contents = InsertFormating(contents);
        StringBuilder ChatMessageBuilder = new StringBuilder();
        ArrayList<BossbarView> bossbars = new ArrayList<>();
        ArrayList<TitleView> titles = new ArrayList<>();

        return new Result(bossbars, titles, ChatMessageBuilder.toString());
    }

    private String InsertFormating(String input)
    {
        for(char c : Formats)
            input = input.replace("@f"+c, "§"+Character.toLowerCase(c));
        return input;
    }
    public static String UnformatString(String input)
    {
        for(char c : Formats)
            input = input.replace("§"+Character.toLowerCase(c), "@f"+c);
        return input;
    }
}
