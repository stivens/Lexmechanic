---
nav_order: 8
title: macOS setup
---

There is no prebuilt macOS release; build it from a clone of the repository:

```bash
xcode-select --install   # clang
brew install sbt libidn2
git clone {{ site.repo_url }}.git ~/Lexmechanic
cd ~/Lexmechanic
sbt release
mkdir -p ~/.local/bin && install -m755 target/lexmechanic ~/.local/bin/lexmechanic
```

- **Hotkey:** the daemon listens through a Quartz event tap, which needs the Input Monitoring permission. On the first
  start macOS asks for it; allow it in System Settings > Privacy & Security > Input Monitoring, then restart. The
  permission goes to the app that launched Lexmechanic, which is your terminal when you start it from one.
- **Screenshots:** `screencapture` needs the Screen Recording permission, granted the same way. Without it, screenshots
  fail or miss the game window. It captures the main display; to use another one, set the screenshot command to
  `screencapture`, `-x`, `-t`, `png`, `-D`, `2`, `{file}` (one argument per line).
