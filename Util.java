package com.clipboard;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static Path defaultStorePath() {
        String home = System.getProperty("user.home");
        return Paths.get(home, ".clipboard-history", "history.txt");
    }

    public static Path expandUser(String p) {
        if (p.startsWith("~")) {
            return Paths.get(System.getProperty("user.home") + p.substring(1));
        }
        return Paths.get(p);
    }

    public static String normalize(String s) {
        s = s.replace("\r\n", "\n").replace("\r", "\n");
        return s.trim();
    }

    // Simple line record format: epochMillis\\tpinned(0/1)\\tescapedText
    public static String toLineRecord(HistoryItem h) {
        return h.createdAt + "\t" + (h.pinned ? "1" : "0") + "\t" + escape(h.text);
    }

    public static HistoryItem parseLineRecord(String line) {
        try {
            int t1 = line.indexOf('\t');
            int t2 = line.indexOf('\t', t1 + 1);
            if (t1 <= 0 || t2 <= t1) return null;
            long ts = Long.parseLong(line.substring(0, t1));
            boolean pinned = "1".equals(line.substring(t1 + 1, t2));
            String text = unescape(line.substring(t2 + 1));
            return new HistoryItem(text, ts, pinned);
        } catch (Exception e) {
            return null;
        }
    }

    // Escape tabs and newlines so we can store raw text safely
    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\t': sb.append("\\t"); break;
                case '\n': sb.append("\\n"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                if (n == 'n') { sb.append('\n'); i++; continue; }
                if (n == 't') { sb.append('\t'); i++; continue; }
                if (n == '\\') { sb.append('\\'); i++; continue; }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // Simple monochrome tray icon (clipboard glyph)
    public static Image defaultTrayImage() {
        int w = 16, h = 16;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.fillRoundRect(3, 5, 10, 9, 3, 3);
        g.setColor(Color.WHITE);
        g.fillRect(5, 7, 6, 5);
        g.setColor(Color.BLACK);
        g.drawRoundRect(3, 5, 10, 9, 3, 3);
        g.drawRect(5, 7, 6, 5);
        g.fillRoundRect(6, 3, 4, 3, 2, 2);
        g.dispose();
        return img;
    }
}
