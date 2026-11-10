---
nav_order: 3
title: First run
---

On Linux, first let the daemon read the hotkey from `/dev/input`. It takes effect after you log in again.

```bash
# On a Steam Deck, set a password with `passwd` first.
sudo usermod -aG input "$USER"
```

Then start it:

```bash
~/.local/bin/lexmechanic
```

The daemon starts in the background and opens its UI in your browser. Whenever there are no translations to show,
which is after each start of the daemon and after **Clear history**, the UI shows a setup checklist:

1. **AI provider key:** press **Open settings**, choose an [AI provider]({% link docs/ai-providers.md %}) and enter
   its API key.
2. **Dialogue region:** with a dialogue on screen, press **Calibrate**, drag over the dialogue window and save the
   region. Until you do, the whole screen is sent.
3. **Hotkey:** the default is the forward mouse button (Mouse 5). To use another, press **Change**, then the mouse
   button or key you want within 5 seconds.

Check the language to translate into in the top bar, next to the interface language. Then press the hotkey, or
**Translate**, during a conversation. Everything in the checklist can be changed later in the settings (the cog icon).

On Linux, to start it from the app menu instead of a terminal, add a
[desktop shortcut]({% link docs/desktop-shortcut.md %}).
