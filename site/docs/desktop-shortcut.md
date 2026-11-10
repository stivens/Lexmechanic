---
nav_order: 6
title: Desktop shortcut
---

A `.desktop` launcher puts Lexmechanic in the app menu, and optionally on the desktop, with the emblem as its icon.
Run this on the host after installing:

```bash
mkdir -p ~/.cache/lexmechanic ~/.local/share/applications

# EOF is unquoted on purpose: .desktop files don't expand $HOME, so it's filled in here.
cat > ~/.local/share/applications/lexmechanic.desktop <<EOF
[Desktop Entry]
Type=Application
Name=Lexmechanic
Comment=Translate game dialogue
Exec=sh -c "exec $HOME/.local/bin/lexmechanic >> $HOME/.cache/lexmechanic/daemon.log 2>&1"
Icon=$HOME/.local/share/icons/lexmechanic.svg
Terminal=false
Categories=Utility;
Actions=stop;

[Desktop Action stop]
Name=Stop
Exec=pkill -x lexmechanic
EOF

# Optional desktop icon
desktop=$(xdg-user-dir DESKTOP)
cp ~/.local/share/applications/lexmechanic.desktop "$desktop/" && chmod +x "$desktop/lexmechanic.desktop"
```

- **Start:** click it; the daemon starts in the background and opens the UI. The first time
  you click the desktop icon, KDE may ask whether to trust it.
- **Stop:** right-click the icon or menu entry and choose **Stop**.
- **Log:** there is no terminal, so the output goes to `~/.cache/lexmechanic/daemon.log`.
- Clicking it while the daemon is already running does nothing: the second copy finds the port taken, logs that and
  exits.
