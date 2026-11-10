---
nav_order: 7
title: Build from source
---

Build inside a [distrobox](https://distrobox.it) container: it works the same on any distro, including immutable ones
like SteamOS, and keeps the build toolchain off the host.

```bash
# On the host. Needs distrobox with podman or docker.
# The container shares your home directory, so whatever it builds there is usable on the host.
distrobox create --name lexbox --image docker.io/library/archlinux:latest

# Install dependencies
distrobox enter lexbox -- sudo pacman -Syu --needed --noconfirm clang which curl jdk-openjdk sbt git

# Clone the Lexmechanic source code
git clone {{ site.repo_url }}.git ~/Lexmechanic
cd ~/Lexmechanic

# Build inside the container; the binary lands in the shared home directory
distrobox enter lexbox -- sbt release
install -Dm755 target/lexmechanic ~/.local/bin/lexmechanic
install -Dm644 art/emblem.svg ~/.local/share/icons/lexmechanic.svg
```

To update, run `git pull` and repeat the `sbt release` and `install` lines.

The binary links against the container's glibc. If the host's is older, it fails with `GLIBC_2.xx not found`; create
the box from an image of your host distro instead and install the same packages with its package manager.

Then continue with the [first run]({% link docs/first-run.md %}).
