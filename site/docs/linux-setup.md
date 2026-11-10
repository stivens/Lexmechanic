---
nav_order: 5
title: Linux setup
---

- **Hotkey:** the daemon reads mouse buttons and keys from `/dev/input` (evdev), which works under Wayland and while
  another window, such as GeForce NOW, has focus. Without the `input` group, bind `lexmechanic trigger` to a desktop
  shortcut instead.
- **Settings:** the cog icon opens the advanced settings: AI provider, model, API key, player character,
  screenshot and open commands, and port. They are saved to `~/.config/lexmechanic/config.json`.
- **Screenshots:** the default screenshot command is the first installed of `spectacle` (KDE), `gnome-screenshot`
  (GNOME), `grim` (Sway, Hyprland and other wlroots compositors), `scrot` or ImageMagick's `import` (X11). To use
  another, set one that saves the full screen as a PNG, with `{file}` as the output path (one argument per line).
- **API key:** enter it in the advanced settings. A daemon started by autostart or a desktop launcher does not see keys
  exported in your shell profile.
- **Display:** to open the UI full-screen, set **Open the UI with** to `firefox`, `--kiosk`, `{url}`, or `flatpak`,
  `run`, `org.mozilla.firefox`, `--kiosk`, `{url}` for the Flatpak (as on SteamOS). To keep it on a second screen,
  such as the Steam Deck's built-in one, pin the window there with a window rule.
