---
nav_order: 2
title: Install
---

Download the prebuilt x86_64 binary from the latest release. It needs glibc 2.35 or newer (Ubuntu 22.04, Debian 12,
Fedora 36, SteamOS 3 and later) and libcurl, which nearly every desktop distro ships.

```bash
# Installs into ~/.local; run it again to update.
curl -L {{ site.download_url }} \
  | tar -xz -C ~/.local
```

Then continue with the [first run]({% link docs/first-run.md %}).

If your system's glibc is older, or you want to change the code, [build from source]({% link docs/build-from-source.md %}).
