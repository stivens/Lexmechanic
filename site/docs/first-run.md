---
nav_order: 3
title: First run
---

```bash
# Lets the daemon read the hotkey from /dev/input; takes effect after you log in again.
# On a Steam Deck, set a password with `passwd` first.
sudo usermod -aG input "$USER"

~/.local/bin/lexmechanic
```

The daemon starts in the background and opens its UI in your browser. Until the first translation, the UI shows a
setup checklist:

1. **AI provider key:** press **Open settings**, choose an [AI provider]({% link docs/ai-providers.md %}) and enter
   its API key. Check the language to translate into while you are there.
2. **Dialogue region:** with a dialogue on screen, press **Calibrate**, drag over the dialogue window and save the
   region. Until you do, the whole screen is sent.
3. **Hotkey:** the default is the forward mouse button (Mouse 5). To use another, press **Change**, then the mouse
   button or key you want.

Then press the hotkey, or **Translate**, during a conversation. Everything in the checklist can be changed later in
the settings (the cog icon).

To start it from the app menu instead of a terminal, add a [desktop shortcut]({% link docs/desktop-shortcut.md %}).
