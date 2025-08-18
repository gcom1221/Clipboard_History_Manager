package com.clipboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TrayUI {
    private final HistoryStore store;
    private final ClipboardWatcher watcher;
    private TrayIcon trayIcon;

    public TrayUI(HistoryStore store, ClipboardWatcher watcher) {
        this.store = store;
        this.watcher = watcher;
    }

    public boolean install() {
        if (!SystemTray.isSupported()) return false;
        try {
            PopupMenu menu = buildMenu();
            Image img = Util.defaultTrayImage();
            trayIcon = new TrayIcon(img, "Clipboard History", menu);
            trayIcon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(trayIcon);
            new java.util.Timer(true).schedule(new java.util.TimerTask(){
                @Override public void run(){ refreshMenu(); }
            }, 1000, 1500);
            return true;
        } catch (Exception e) {
            System.err.println("[TrayUI] install failed: " + e.getMessage());
            return false;
        }
    }

    private PopupMenu buildMenu() {
        PopupMenu menu = new PopupMenu();
        menu.add(new MenuItem("(loading...)"));
        menu.addSeparator();
        MenuItem pause = new MenuItem(watcher.isPaused()? "Resume capture" : "Pause capture");
        pause.addActionListener(e -> {
            if (watcher.isPaused()) watcher.resume(); else watcher.pause();
            refreshMenu();
        });
        MenuItem clear = new MenuItem("Clear history");
        clear.addActionListener(e -> store.clearAll());
        MenuItem quit = new MenuItem("Quit");
        quit.addActionListener((ActionEvent e) -> {
            try { SystemTray.getSystemTray().remove(trayIcon);} catch (Exception ignored) {}
            System.exit(0);
        });
        menu.add(pause);
        menu.add(clear);
        menu.addSeparator();
        menu.add(quit);
        return menu;
    }

    private void refreshMenu() {
        if (trayIcon == null) return;
        PopupMenu menu = new PopupMenu();
        List<HistoryItem> items = store.list();
        int index = 0;
        if (items.isEmpty()) {
            MenuItem none = new MenuItem("(no items yet)");
            none.setEnabled(false);
            menu.add(none);
        } else {
            for (HistoryItem h : items) {
                final int idx = index++;
                String label = (h.pinned ? "📌 " : "") + h.preview();
                MenuItem mi = new MenuItem(label);
                mi.addActionListener(e -> {
                    watcher.setClipboard(h.text);
                });
                menu.add(mi);
            }
        }
        menu.addSeparator();
        MenuItem pause = new MenuItem(watcher.isPaused()? "Resume capture" : "Pause capture");
        pause.addActionListener(e -> {
            if (watcher.isPaused()) watcher.resume(); else watcher.pause();
            refreshMenu();
        });
        MenuItem clear = new MenuItem("Clear history");
        clear.addActionListener(e -> store.clearAll());
        MenuItem quit = new MenuItem("Quit");
        quit.addActionListener(e -> {
            try { SystemTray.getSystemTray().remove(trayIcon);} catch (Exception ignored) {}
            System.exit(0);
        });
        menu.add(pause);
        menu.add(clear);
        menu.addSeparator();
        menu.add(quit);

        trayIcon.setPopupMenu(menu);
    }
}
