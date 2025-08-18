package com.clipboard;

public class HistoryItem {
    public final String text;
    public final long createdAt;
    public boolean pinned;

    public HistoryItem(String text, long createdAt, boolean pinned) {
        this.text = text;
        this.createdAt = createdAt;
        this.pinned = pinned;
    }

    public String preview() {
        String t = text.replace("\n", " ⏎ ").trim();
        return t.length() > 80 ? t.substring(0, 80) + "…" : t;
    }
}
