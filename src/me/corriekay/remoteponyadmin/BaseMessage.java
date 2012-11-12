package me.corriekay.remoteponyadmin;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 *
 * @author Fernando
 */
public class BaseMessage
{
		protected static final String[] colors = new String[]
        {
                "#000000", "#0000aa", "#00aa00", "#00aaaa",
                "#aa0000", "#aa00aa", "#ffaa00", "#aaaaaa",
                "#555555", "#5555ff", "#55ff55", "#55ffff",
                "#ff5555", "#ff55ff", "#ffff55", "#ffffff",
        };

        private static enum Token
        {
                MAGIC("<span style=\"background-color: black\">", "</span>"),
                BOLD("<b>", "</b>"),
                STRIKETHROUGH("<del>", "</del>"),
                UNDERLINE("<u>", "</u>"),
                ITALIC("<i>", "</i>");
                private final String start;
                private final String ending;

                private Token(String start, String ending)
                {
                        this.start = start;
                        this.ending = ending;
                }

                public String getStart()
                {
                        return this.start;
                }

                public String getEndingToken()
                {
                        return this.ending;
                }
        }

        public BaseMessage(String message, boolean mustBeWhite)
        {
                this.html = addHtmlColors(HtmlEscape.escapeBr(HtmlEscape.escapeSpecial(HtmlEscape.escapeTags(message.replace("&", "&amp;")))), mustBeWhite ? 15 : 0);
                this.message = message;
        }

        public static String escapeHtml(String input)
        {
                return HtmlEscape.escape(input);
        }

        protected static String addHtmlColors(String input, int defaultcolor)
        {
                try
                {
                        CharArrayReader inputStream = new CharArrayReader(input.toCharArray());
                        int ch = inputStream.read();
                        boolean foundToken = false;
                        int color = defaultcolor;
                        ArrayDeque<Token> stack = new ArrayDeque<Token>();
                        StringBuilder out = new StringBuilder().append("<font color=").append(colors[color]).append(">");
                        while (ch != -1)
                        {
                                if (foundToken)
                                {
                                        int tmp1 = Character.digit((char) ch, 16);
                                        if (tmp1 != -1)
                                        {
                                                Iterator<Token> desending = stack.descendingIterator();
                                                while (desending.hasNext())
                                                {
                                                        out.append(desending.next().getEndingToken());
                                                }
                                                stack.clear();
                                                if (color != tmp1)
                                                {
                                                        out.append("</font><font color=").append(colors[tmp1]).append(">");
                                                        color = tmp1;
                                                }
                                        }
                                        else
                                        {
                                                if (((char) ch) == 'r')
                                                {
                                                        Iterator<Token> desending = stack.descendingIterator();
                                                        while (desending.hasNext())
                                                        {
                                                                out.append(desending.next().getEndingToken());
                                                        }
                                                        stack.clear();
                                                        if (color != 15)
                                                        {
                                                                out.append("</font><font color=").append(colors[15]).append(">");
                                                                color = 15;
                                                        }
                                                }
                                                else if (((char) ch) == 'l')
                                                {
                                                        if (!stack.contains(Token.BOLD))
                                                        {
                                                                stack.add(Token.BOLD);
                                                                out.append(Token.BOLD.getStart());
                                                        }
                                                }
                                                else if (((char) ch) == 'm')
                                                {
                                                        if (!stack.contains(Token.STRIKETHROUGH))
                                                        {
                                                                stack.add(Token.STRIKETHROUGH);
                                                                out.append(Token.STRIKETHROUGH.getStart());
                                                        }
                                                }
                                                else if (((char) ch) == 'n')
                                                {
                                                        if (!stack.contains(Token.UNDERLINE))
                                                        {
                                                                stack.add(Token.UNDERLINE);
                                                                out.append(Token.UNDERLINE.getStart());
                                                        }
                                                }
                                                else if (((char) ch) == 'o')
                                                {
                                                        if (!stack.contains(Token.ITALIC))
                                                        {
                                                                stack.add(Token.ITALIC);
                                                                out.append(Token.ITALIC.getStart());
                                                        }
                                                }
                                        }
                                        foundToken = false;
                                }
                                else
                                {
                                        if (((char) ch) == '\u00a7')
                                        {
                                                foundToken = true;
                                        }
                                        else
                                        {
                                                out.append((char) ch);
                                        }
                                }
                                ch = inputStream.read();
                        }
                        inputStream.close();
                        if (!stack.isEmpty())
                        {
                                Iterator<Token> desending = stack.descendingIterator();
                                while (desending.hasNext())
                                {
                                        out.append(desending.next().getEndingToken());
                                }
                        }
                        return out.append("</font>").toString();
                }
                catch (IOException ex)
                {
                        throw new RuntimeException("The hell broke loose", ex); // stream closed before I wanted to close it is the only case of this, what coun't happend as it is single threaded
                }
        }
        protected final String html;
        protected final String message;

        public String getHTML()
        {
                return this.html;
        }

        public String getPlain()
        {
                return message;
        }
}