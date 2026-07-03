package com.gregorune.minecartannoucer.bookparser;

import com.gregorune.helper.Pair;
import com.gregorune.minecartannoucer.bookparser.views.BossbarView;
import com.gregorune.minecartannoucer.bookparser.views.TitleView;
import org.checkerframework.checker.units.qual.N;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

public class Parser {
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
    private static final char[] Formats = "0123456789ABCDEFLNMOKR".toCharArray();

    public Result Parse(String content)
    {
        content = InsertFormating(content);
        content = content.replace("\u001F", "");
        return ParseMessage(new StringStream(content));
    }

    private static String ReadIdentifier(StringStream stream)
    {
        StringBuilder buffer = new StringBuilder();
        while (!stream.IsEndOfStream())
        {
            Character chr = stream.Peek();
            if(chr == null || chr == '(' || IsWhitespace(chr))
                break;

            buffer.append((char)chr);
            stream.Get();
        }
        return buffer.toString();
    }
    private static boolean ParseParamList(StringStream stream, ArrayList<Pair<String, String>> outParams)
    {
        SkipWhitespace(stream);

        Character first = stream.Peek();
        //No params
        if(first != null && first == ')')
        {
            stream.Get();
            return true; //Empty params (success)
        }

        //Params
        while (true)
        {
            //  ============================
            //  GET KEY / FLAG NAME
            //  ============================
            SkipWhitespace(stream);
            String key = ReadUntil(stream, ':', ')', ',');
            SkipWhitespace(stream);

            //  ============================
            //  DETERMINE WHAT TO DO
            //  ============================
            Character separator = stream.Peek();
            if(separator == null) return false; //Stream ended (fail)
            if(separator == ')') //Key without value - end (success)
            {
                stream.Get();
                outParams.add(new Pair<>(key, ""));
                return true;
            }
            if(separator == ',') //Key without value - flag
            {
                stream.Get();
                outParams.add(new Pair<>(key, ""));
                continue;
            }
            if(separator != ':') return false; //Something unexpected happened (fail)

            stream.Get(); //eat ':'
            SkipWhitespace(stream);

            //  ============================
            //  GET VALUE FOR THAT KEY
            //  ============================
            Character valueStart = stream.Peek();
            if(valueStart == null) return false; //End of stream (fail)

            String value;
            if(valueStart == '"') //string literal
            {
                stream.Get();
                value = ParseStringLiteral(stream);
            }
            else
                value = ReadUntil(stream, ',', ')', null);

            //  ============================
            //  SAVE PARAM
            //  ============================
            outParams.add(new Pair<>(key, value));

            SkipWhitespace(stream);

            //  ============================
            //  CHECK FOR NEXT PARAMS
            //  ============================
            Character after = stream.Peek();
            if(after == null) return false; //End of stream (fail)
            if(after == ')') //End of params (success)
            {
                stream.Get();
                return true;
            }
            if(after != ',') return false; //Unexpected char (fail)

            //More params
            stream.Get();
        }
    }
    private static String ParseStringLiteral(StringStream stream)
    {
        StringBuilder buffer = new StringBuilder();

        while (!stream.IsEndOfStream())
        {
            Character c = stream.Get();
            if(c == null) break;
            if(c == '\\')
            {
                Character escaped = stream.Get();
                if(escaped == null)
                    continue;

                if(escaped == '"' || escaped == '\\')
                    buffer.append((char) escaped);
                else if(escaped == 'n')
                    buffer.append('\n');
                else if(escaped == 't')
                    buffer.append('\t');
                else
                    buffer.append('\\').append((char)escaped);

            }
            if(c == '"')
                break;

            buffer.append((char)c);
        }

        return buffer.toString();
    }

    private static String ReadUntil(StringStream stream,
                                    @Nullable Character stop1,
                                    @Nullable Character stop2,
                                    @Nullable Character stop3)
    {
        StringBuilder buffer = new StringBuilder();
        while (!stream.IsEndOfStream())
        {
            Character c = stream.Peek();
            boolean cNotNull = c != null;

            boolean useStop1 = stop1 != null && cNotNull;
            useStop1 = useStop1 && (char)c == (char)stop1;

            boolean useStop2 = stop2 != null && cNotNull;
            useStop2 = useStop2 && (char)c == (char)stop2;

            boolean useStop3 = stop3 != null && cNotNull;
            useStop3 = useStop3 && (char)c == (char)stop3;

            if(!cNotNull || useStop1 || useStop2 || useStop3 || IsWhitespace(c))
                break;

            buffer.append((char)c);
            stream.Get();
        }

        return buffer.toString();
    }

    private static void SkipWhitespace(StringStream stream)
    {
        while (!stream.IsEndOfStream())
        {
            Character c = stream.Peek();
            if(!IsWhitespace(c))
                break;

            stream.Get();
        }
    }
    private static boolean IsWhitespace(Character c)
    {
        if(c == null) return false;
        return Character.isWhitespace(c) || c == '\u001F';
    }

    private Result ParseMessage(StringStream stream)
    {
        ArrayList<Declaration> declarations = new ArrayList<>();
        StringBuilder plainText = new StringBuilder();

        while (!stream.IsEndOfStream())
        {
            char chr = stream.IKnowGetNotNull();

            if(chr != '@')
            {
                plainText.append(chr);
                continue;
            }

            //  ============================
            //  Identifier
            //  ============================
            String name = ReadIdentifier(stream);

            Character next = stream.Peek();
            if(next == null || next != '(')
            {
                plainText.append('@').append(name);
                continue;
            }

            stream.Get(); //Eat '('

            //  ============================
            //  Param list
            //  ============================
            ArrayList<Pair<String, String>> params = new ArrayList<>();
            if(!ParseParamList(stream, params))
            {
                //Forgot to close parenthesis
                plainText.append('@').append(name).append('(');
                continue;
            }

            //  ============================
            //  Value
            //  ============================
            SkipWhitespace(stream);
            String value = "";
            Character maybeQuoute = stream.Peek();
            if(maybeQuoute != null && maybeQuoute == '"')
            {
                stream.Get();
                value = ParseStringLiteral(stream);
            }
        }

        return null;//new Result(bossbars, titles, plainText.toString());
    }


    private Declaration ParseDeclaration(StringStream stream)
    {
        return new Declaration("TEST", new ArrayList<>(), "");
    }

    private static class Declaration
    {
        public final String Name;
        public final ArrayList<Pair<String, String>> Params;
        public final String Value;

        public Declaration(String name, ArrayList<Pair<String, String>> params, String value)
        {
            Name = name;
            Params = params;
            Value = value;
        }
    }

    /* example:

     @Bossbar(Time:50,Color:RED)"TEST BOSSBAR";
     @Title(Subtitle:"$(B)Boat City: Widzew")"$(C)Next Station";
     $(K)Some hidden $(R)text
     $(E)Herobrine joined game
    */

    private BossbarView InitBossbar(String content, ArrayList<Pair<String, String>> params)
    { return new BossbarView().SetVariables(params); }

    private TitleView InitTitle(String content, ArrayList<Pair<String, String>> params)
    { return new TitleView().SetVariables(params); }

    public static String InsertFormating(String input)
    {
        for(char c : Formats)
            input = input.replace("$("+c+")", "§"+Character.toLowerCase(c));
        return input;
    }
    public static String UnformatString(String input)
    {
        for(char c : Formats)
            input = input.replace("§"+Character.toLowerCase(c), "$("+c+")");
        return input;
    }
}
