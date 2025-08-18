package com.clipboard;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.fromArgs(args);
        HistoryStore store = new HistoryStore(config.getMaxItems(), config.getStorePath());
        store.load();

        ClipboardWatcher watcher = new ClipboardWatcher(store);
        if (config.isStartPaused()) watcher.pause();

        TrayUI ui = new TrayUI(store, watcher);
        if (!ui.install()) {
            System.out.println("[ClipboardHistory] SystemTray not supported. Running headless watcher. Press Ctrl+C to quit.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { watcher.stop(); } catch (Exception ignored) {}
            store.save();
        }));

        watcher.start();
    }
}
