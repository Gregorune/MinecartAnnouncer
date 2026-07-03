package com.gregorune.minecartannoucer.Messages;

import com.gregorune.helper.Pair;
import org.bukkit.boss.BarColor;

@Deprecated(forRemoval = true)
public class DataParser {
    final static char defCharacter = '#';
    public final static String TitlePageDef = defCharacter + "DEF:TITLE" + defCharacter;
    public final static String SubtitleDef = defCharacter + "DEF:SUBTITLE" + defCharacter;
    public final static String BossbarDef = defCharacter + "DEF:BOSSBAR" + defCharacter;
    public final static String BossbarColorDef = defCharacter + "DEF:BB_COLOR-";
    public final static String BossbarDurationDef = defCharacter + "DEF:BB_DUR-";
    public final static String BossbarRgxTimeLeftDef = "#GET:BB_TIME#";

    public static Pair<String, String> ParseTitlePage(String data)
    {
        int subtitlePos = data.indexOf(SubtitleDef);
        String title, subtitle;
        if(subtitlePos != -1) {
            title = data.substring(TitlePageDef.length(), subtitlePos).trim();
            subtitle = data.substring(subtitlePos + SubtitleDef.length()).trim();
        }
        else {
            title = data.substring(TitlePageDef.length()).trim();
            subtitle = "";
        }

        return new Pair<>(title, subtitle);
    }
    static Pair<String, Integer> ExtractIntegerParam(String bossbarInfo, String argument)
    {
        int intParam = 10;
        int durationPos = bossbarInfo.indexOf(argument);
        if(durationPos == -1)
            return new Pair<>(bossbarInfo.trim(), intParam);

        int endDurationPos = bossbarInfo.indexOf(defCharacter, durationPos+1);
        if(endDurationPos == -1)
        {
            bossbarInfo = bossbarInfo.substring(0, durationPos)
                    + bossbarInfo.substring(durationPos + argument.length());
            return new Pair<>(bossbarInfo.trim(), intParam);
        }

        String durationString = bossbarInfo.substring(durationPos + argument.length(), endDurationPos);
        try
        {
            intParam = Integer.parseInt(durationString);
            intParam = Math.abs(intParam);
        }
        catch (Exception ignored)
        {  }

        bossbarInfo = bossbarInfo.substring(0, durationPos)
                + bossbarInfo.substring(endDurationPos+1);

        return new Pair<>(bossbarInfo.trim(), intParam);
    }
    static Pair<String, BarColor> ExtractBossbarColor(String bossbarInfo)
    {
        BarColor barColor;
        int colorPos = bossbarInfo.indexOf(BossbarColorDef);
        if(colorPos == -1)
            return new Pair<>(bossbarInfo.trim(), BarColor.WHITE);

        int endColorPos = bossbarInfo.indexOf(defCharacter, colorPos+1);
        if(endColorPos == -1)
        {
            barColor = BarColor.WHITE;
            bossbarInfo = bossbarInfo.substring(0, colorPos)
                    + bossbarInfo.substring(colorPos + BossbarColorDef.length());
            return new Pair<>(bossbarInfo.trim(), barColor);
        }

        barColor = switch (bossbarInfo.substring(colorPos + BossbarColorDef.length(), endColorPos)) {
            case "RED" -> BarColor.RED;
            case "BLUE" -> BarColor.BLUE;
            case "PINK" -> BarColor.PINK;
            case "GREEN" -> BarColor.GREEN;
            case "YELLOW" -> BarColor.YELLOW;
            case "PURPLE" -> BarColor.PURPLE;
            default -> BarColor.WHITE;
        };
        bossbarInfo = bossbarInfo.substring(0, colorPos)
                + bossbarInfo.substring(endColorPos+1);

        return new Pair<>(bossbarInfo.trim(), barColor);
    }
}
