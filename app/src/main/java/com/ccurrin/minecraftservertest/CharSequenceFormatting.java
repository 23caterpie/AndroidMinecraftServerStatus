package com.ccurrin.minecraftservertest;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;

public class CharSequenceFormatting
{
    public static class CodeStyles
    {
        public String formatCode;
        public ArrayList<CharacterStyle> styles;

        public CodeStyles(String newFormatCode, CharacterStyle... cs)
        {
            formatCode = newFormatCode;
            styles = new ArrayList<>();
            for(CharacterStyle style : cs)
            {
                styles.add(style);
            }
        }
    }

    private static final CodeStyles[] minecraftFontStyles = new CodeStyles[22];
    static
    {
        StyleSpan bold   = new StyleSpan( Typeface.BOLD );
        StyleSpan italic = new StyleSpan( Typeface.ITALIC );
        StyleSpan normal = new StyleSpan( Typeface.NORMAL );
        minecraftFontStyles[0]   = new CodeStyles("\u00A70", new ForegroundColorSpan(0xFF000000), normal);//Black
        minecraftFontStyles[1]   = new CodeStyles("\u00A71", new ForegroundColorSpan(0xFF0000AA), normal);//Dark Blue
        minecraftFontStyles[2]   = new CodeStyles("\u00A72", new ForegroundColorSpan(0xFF00AA00), normal);//Dark Green
        minecraftFontStyles[3]   = new CodeStyles("\u00A73", new ForegroundColorSpan(0xFF00AAAA), normal);//Dark Aqua
        minecraftFontStyles[4]   = new CodeStyles("\u00A74", new ForegroundColorSpan(0xFFAA0000), normal);//Dark Red
        minecraftFontStyles[5]   = new CodeStyles("\u00A75", new ForegroundColorSpan(0xFFAA00AA), normal);//Dark Purple
        minecraftFontStyles[6]   = new CodeStyles("\u00A76", new ForegroundColorSpan(0xFFFFAA00), normal);//Gold
        minecraftFontStyles[7]   = new CodeStyles("\u00A77", new ForegroundColorSpan(0xFFAAAAAA), normal);//Gray
        minecraftFontStyles[8]   = new CodeStyles("\u00A78", new ForegroundColorSpan(0xFF555555), normal);//Dark Gray
        minecraftFontStyles[9]   = new CodeStyles("\u00A79", new ForegroundColorSpan(0xFF5555FF), normal);//Blue
        minecraftFontStyles[10]  = new CodeStyles("\u00A7a", new ForegroundColorSpan(0xFF55FF55), normal);//Green
        minecraftFontStyles[11]  = new CodeStyles("\u00A7b", new ForegroundColorSpan(0xFF55FFFF), normal);//Aqua
        minecraftFontStyles[12]  = new CodeStyles("\u00A7c", new ForegroundColorSpan(0xFFFF5555), normal);//Red
        minecraftFontStyles[13]  = new CodeStyles("\u00A7d", new ForegroundColorSpan(0xFFFF55FF), normal);//Light Purple
        minecraftFontStyles[14]  = new CodeStyles("\u00A7e", new ForegroundColorSpan(0xFFFFFF55), normal);//Yellow
        minecraftFontStyles[15]  = new CodeStyles("\u00A7f", new ForegroundColorSpan(0xFFFFFFFF), normal);//White
        minecraftFontStyles[16]  = new CodeStyles("\u00A7k");                         //Obfuscated
        minecraftFontStyles[17]  = new CodeStyles("\u00A7l", bold);                   //Bold
        minecraftFontStyles[18]  = new CodeStyles("\u00A7m", new StrikethroughSpan());//Strikethrough
        minecraftFontStyles[19]  = new CodeStyles("\u00A7n", new UnderlineSpan());    //Underline
        minecraftFontStyles[20]  = new CodeStyles("\u00A7o", italic);                 //Italic
        minecraftFontStyles[21]  = new CodeStyles("\u00A7r", normal);                 //Reset
    }

    public static CharSequence formatTextToMinecraftStyle(String text)
    {
        int tokenLen = 2;
        String token;
        CharSequence newText = text;
        SpannableStringBuilder ssb = new SpannableStringBuilder(newText);
        int tokenIndex = ssb.toString().indexOf("\u00A7");
        while(tokenIndex > -1 && tokenIndex < ssb.toString().length() - 1)
        {
            token = "\u00A7" + ssb.toString().charAt(tokenIndex + 1);
            int fontStyleIndex = -1;
            for(int i = 0; i < minecraftFontStyles.length; i++)
            {
                if(minecraftFontStyles[i].formatCode.equals(token))
                {
                    fontStyleIndex = i;
                    break;
                }
            }
            int start = tokenIndex + tokenLen;
            int end = ssb.toString().length();
            if(fontStyleIndex > -1 && start < end)
                for (CharacterStyle c : minecraftFontStyles[fontStyleIndex].styles)
                    ssb.setSpan(c, start, end, 0);
            ssb.delete(tokenIndex, start);
            tokenIndex = ssb.toString().indexOf("\u00A7");
        }
        newText = ssb;
        return newText;
    }

    /*
    private static final CodeStyles[] minecraftFontStyles = new CodeStyles[16];
    static
    {
        minecraftFontStyles[0]  = new CodeStyles("\u00A70", new ForegroundColorSpan(0xFF000000));//Black
        minecraftFontStyles[1]  = new CodeStyles("\u00A71", new ForegroundColorSpan(0xFF0000AA));//Dark Blue
        minecraftFontStyles[2]  = new CodeStyles("\u00A72", new ForegroundColorSpan(0xFF00AA00));//Dark Green
        minecraftFontStyles[3]  = new CodeStyles("\u00A73", new ForegroundColorSpan(0xFF00AAAA));//Dark Aqua
        minecraftFontStyles[4]  = new CodeStyles("\u00A74", new ForegroundColorSpan(0xFFAA0000));//Dark Red
        minecraftFontStyles[5]  = new CodeStyles("\u00A75", new ForegroundColorSpan(0xFFAA00AA));//Dark Purple
        minecraftFontStyles[6]  = new CodeStyles("\u00A76", new ForegroundColorSpan(0xFFFFAA00));//Gold
        minecraftFontStyles[7]  = new CodeStyles("\u00A77", new ForegroundColorSpan(0xFFAAAAAA));//Gray
        minecraftFontStyles[8]  = new CodeStyles("\u00A78", new ForegroundColorSpan(0xFF555555));//Dark Gray
        minecraftFontStyles[9]  = new CodeStyles("\u00A79", new ForegroundColorSpan(0xFF5555FF));//Blue
        minecraftFontStyles[10] = new CodeStyles("\u00A7a", new ForegroundColorSpan(0xFF55FF55));//Green
        minecraftFontStyles[11] = new CodeStyles("\u00A7b", new ForegroundColorSpan(0xFF55FFFF));//Aqua
        minecraftFontStyles[12] = new CodeStyles("\u00A7c", new ForegroundColorSpan(0xFFFF5555));//Red
        minecraftFontStyles[13] = new CodeStyles("\u00A7d", new ForegroundColorSpan(0xFFFF55FF));//Light Purple
        minecraftFontStyles[14] = new CodeStyles("\u00A7e", new ForegroundColorSpan(0xFFFFFF55));//Yellow
        minecraftFontStyles[15] = new CodeStyles("\u00A7f", new ForegroundColorSpan(0xFFFFFFFF));//White
    }
    public static CharSequence formatTextToMinecraftStyle(String text) {
        int tokenLen = 2;
        CharSequence newText = text;
        SpannableStringBuilder ssb = new SpannableStringBuilder(newText);

        int fontStyleIndex = -1;
        int startTokenIndex = ssb.toString().indexOf("\u00A7");
        while(fontStyleIndex == -1 && startTokenIndex != -1 && startTokenIndex < ssb.length() - 1)
        {
            for (int i = 0; i < minecraftFontStyles.length; i++)
            {
                if (minecraftFontStyles[i].formatCode.equals("\u00A7" + ssb.toString().charAt(startTokenIndex + 1)))
                {
                    fontStyleIndex = i;
                    break;
                }
            }
            startTokenIndex = ssb.toString().indexOf("\u00A7", startTokenIndex + tokenLen);
        }
        int start = startTokenIndex + tokenLen;
        if(fontStyleIndex != -1)
        {

        }
        else
        {
            //there are no color codes
        }

        while (tokenIndex > -1 && tokenIndex < ssb.toString().length() - 1) {
            token = "\u00A7" + ssb.toString().charAt(tokenIndex + 1);
            int fontStyleIndex = -1;
            for (int i = 0; i < minecraftFontStyles.length; i++)
            {
                if (minecraftFontStyles[i].formatCode.equals(token))
                {
                    fontStyleIndex = i;
                    break;
                }
            }
            int start = tokenIndex + tokenLen;
            int end = ssb.toString().length();
            if (fontStyleIndex > -1 && start < end)
                for (CharacterStyle c : minecraftFontStyles[fontStyleIndex].styles)
                    ssb.setSpan(c, start, end, 0);
            ssb.delete(tokenIndex, start);
            tokenIndex = ssb.toString().indexOf("\u00A7");
        }
        newText = ssb;
        return newText;
    }
    */
}