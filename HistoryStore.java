package com.clipboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class HistoryStore {
    private final Deque<HistoryItem> items = new ArrayDeque<>();
    private final int capacity;
    private final Path storePath;

    public HistoryStore(int capacity, Path storePath) {
        this.capacity = Math.max(1, capacity);
        this.storePath = storePath;
    }

    public synchronized List<HistoryItem> list() {
        return new ArrayList<>(items);
    }

    public synchronized void add(String text) {
        text = Util.normalize(text);
        if (text.isEmpty()) return;
        HistoryItem head = items.peekFirst();
        if (head != null && head.text.equals(text)) return;
        items.addFirst(new HistoryItem(text, System.currentTimeMillis(), false));
        trimToCapacity();
        save();
    }

    public synchronized void pinToggle(int indexFromTop) {
        HistoryItem it = getByIndex(indexFromTop);
        if (it != null) { it.pinned = !it.pinned; save(); }
    }

    public synchronized HistoryItem getByIndex(int indexFromTop) {
        int i = 0;
        for (HistoryItem it : items) {
            if (i++ == indexFromTop) return it;
        }
        return null;
    }

    private void trimToCapacity() {
        while (countNonPinned() > capacity) {
            Iterator<HistoryItem> it = items.descendingIterator();
            boolean removed = false;
            while (it.hasNext()) {
                HistoryItem h = it.next();
                if (!h.pinned) { it.remove(); removed = true; break; }
            }
            if (!removed) break;
        }
    }

    private int countNonPinned() {
        int c = 0;
        for (HistoryItem h : items) if (!h.pinned) c++;
        return c;
    }

    public synchronized void clearAll() {
        items.clear();
        save();
    }

    public synchronized void load() {
        try {
            Files.createDirectories(storePath.getParent());
            if (!Files.exists(storePath)) return;
            List<String> lines = Files.readAllLines(storePath, StandardCharsets.UTF_8);
            items.clear();
            for (String line : lines) {
                HistoryItem h = Util.parseLineRecord(line);
                if (h != null) items.addLast(h);
            }
            ArrayDeque<HistoryItem> rev = new ArrayDeque<>();
            for (HistoryItem h : items) rev.addFirst(h);
            items.clear();
            items.addAll(rev);
        } catch (Exception e) {
            System.err.println("[HistoryStore] load failed: " + e.getMessage());
        }
    }

    public synchronized void save() {
        try {
            Files.createDirectories(storePath.getParent());
            List<String> out = new ArrayList<>();
            for (HistoryItem h : items) out.add(Util.toLineRecord(h));
            Files.write(storePath, out, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.err.println("[HistoryStore] save failed: " + e.getMessage());
        }
    }
}
