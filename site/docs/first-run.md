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

The daemon starts in the background and opens its UI in your browser. Then:

1. Open the advanced settings with the cog icon, choose an [AI provider]({% link docs/ai-providers.md %})
   and enter its API key.
2. Choose the language to translate into.
3. With a dialogue on screen, press **Calibrate**, drag over the dialogue window and save the region. Until you do,
   the whole screen is sent.
4. Press the hotkey during a conversation. The default is the forward mouse button (Mouse 5); click **Hotkey** in the
   UI and press another mouse button or key to change it.

To start it from the app menu instead of a terminal, add a [desktop shortcut]({% link docs/desktop-shortcut.md %}).
