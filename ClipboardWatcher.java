package com.clipboard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.concurrent.*;

public class ClipboardWatcher {
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ClipboardWatcher");
        t.setDaemon(true);
        return t;
    });
    private final HistoryStore store;
    private volatile boolean paused = false;
    private volatile String lastSeen = null;
    private volatile String lastProgrammaticSet = null;
    private volatile long lastProgrammaticAt = 0L;

    public ClipboardWatcher(HistoryStore store) {
        this.store = store;
    }

    public void start() {
        exec.scheduleWithFixedDelay(this::tick, 200, 400, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        exec.shutdownNow();
    }

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public boolean isPaused() { return paused; }

    private void tick() {
        if (paused) return;
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String text = (String) cb.getData(DataFlavor.stringFlavor);
                if (text != null) {
                    text = Util.normalize(text);
                    if (text.isEmpty()) return;

                    long now = System.currentTimeMillis();
                    if (lastProgrammaticSet != null && text.equals(lastProgrammaticSet) && now - lastProgrammaticAt < 1000) {
                        return;
                    }

                    if (!text.equals(lastSeen)) {
                        lastSeen = text;
                        store.add(text);
                    }
                }
            }
        } catch (IllegalStateException ise) {
            // Clipboard temporarily unavailable; ignore and retry next tick
        } catch (Exception e) {
            System.err.println("[ClipboardWatcher] " + e.getMessage());
        }
    }

    public void setClipboard(String text) {
        try {
            StringSelection sel = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
            lastProgrammaticSet = text;
            lastProgrammaticAt = System.currentTimeMillis();
        } catch (Exception e) {
            System.err.println("[ClipboardWatcher] Failed to set clipboard: " + e.getMessage());
        }
    }
}
