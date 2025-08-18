# Clipboard History Manager (Java-only)

A tiny Java clipboard manager that remembers your last N copies (default 10) and lets you quickly paste them from the system tray.  
**Pure Java, no external libraries.** Works on macOS, Windows, and Linux (tray must be available/enabled).

---

## ✨ Features
- Java-only (AWT/Swing), **no dependencies**
- **System tray** menu with your recent text snippets (newest first)
- **Re-copy with one click**
- **Pause/Resume** capture, **Clear history**
- **Persistence** to a local text file (`~/.clipboard-history/history.txt`)
- Lightweight polling (~400 ms), ignores consecutive duplicates and empty strings

---

## 🎬 Demo
<details>
  <summary>See full screen recording</summary>

  <video src="assets.mov" controls muted playsinline width="720"></video>
</details>

> Quick look at capturing items and re-copying from the tray.

---

## 🧰 Requirements
- **Java 17+** (tested with Java 24)
- Desktop environment with a **system tray** (if unavailable, app still runs headless)

Check your versions:
```bash
java -version
javac -version
```

---

## 📦 Installation

### Run from source (fastest)
```bash
# clone this repository
git clone https://github.com/gcom1221/Clipboard_History_Manager
cd Clipboard_History_Manager

# compile
javac src/com/clipboard/*.java

# run
java -cp src com.clipboard.Main
```


## 🖱️ Usage
- A **tray icon** (clipboard glyph) appears after launch.
- **Click the icon** to open a menu listing your recent items (newest first).
- **Click an item** to copy it back to your clipboard immediately.
- Use **Pause/Resume**, **Clear history**, and **Quit** from the tray menu.

> If your environment doesn’t support a tray, the app still records in the background and you can exit with **Ctrl+C** in the terminal.

---

## 🔒 Privacy
- The app is **local-only**: no telemetry, no network calls.
- Toggle **Pause** before copying sensitive content.
- You can **Clear history** at any time, or manually delete the file:
  - Default path: `~/.clipboard-history/history.txt`

---

## 🐞 Troubleshooting
- **“SystemTray not supported”**  
  Your desktop environment may not expose a tray. On Linux, enable a tray app/extension (e.g., KDE, Cinnamon, or a GNOME extension). You can still run headless and quit with `Ctrl+C`.

- **Clipboard busy / `IllegalStateException`**  
  Some OSes lock the clipboard briefly. The app auto-retries; try again.

- **Nothing appears in the menu**  
  Ensure you’re copying **text** (v1 ignores images/files) and that the app is not **paused**.

---

## 🗂 Project Layout
```
src/com/clipboard/
├─ Main.java            # Entry point
├─ AppConfig.java       # CLI flags & defaults
├─ ClipboardWatcher.java# Polling loop & clipboard I/O
├─ HistoryItem.java     # Model (text, timestamp, pinned)
├─ HistoryStore.java    # In-memory ring + persistence
├─ TrayUI.java          # System tray UI
└─ Util.java            # Helpers: paths, encoding, tray icon
```

---

## 🚧 Roadmap
- **Search window (Swing)** for quick filtering
- **Pin favorites** to keep beyond capacity
- **Optional encrypted storage** (password at launch)
- **Global hotkey** (would require native hooks; kept optional to remain Java-only)

---

## 🤝 Contributing
1. Open an Issue describing the problem/feature.
2. Create a feature branch.
3. Submit a PR with a concise test plan.

---

## 📝 License
MIT © <YOUR NAME>
