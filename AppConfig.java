package com.clipboard;

import java.nio.file.Path;

public class AppConfig {
    private final int maxItems;
    private final Path storePath;
    private final boolean startPaused;

    public AppConfig(int maxItems, Path storePath, boolean startPaused) {
        this.maxItems = maxItems;
        this.storePath = storePath;
        this.startPaused = startPaused;
    }

    public static AppConfig fromArgs(String[] args) {
        int max = 10;
        Path path = Util.defaultStorePath();
        boolean paused = false;
        for (String a : args) {
            if (a.startsWith("--max=")) {
                try { max = Math.max(1, Integer.parseInt(a.substring(6))); } catch (Exception ignored) {}
            } else if (a.startsWith("--store=")) {
                path = Util.expandUser(a.substring(8));
            } else if (a.equals("--paused")) {
                paused = true;
            }
        }
        return new AppConfig(max, path, paused);
    }

    public int getMaxItems() { return maxItems; }
    public Path getStorePath() { return storePath; }
    public boolean isStartPaused() { return startPaused; }
}
