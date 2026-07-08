# House — Spigot Plugin for Minecraft 1.21.11
![Spigot](https://img.shields.io/badge/Spigot-Plugin-orange?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-Java%20Edition-3C8527?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-100%25-blue?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)

A lightweight Spigot plugin for Minecraft that instantly builds a detailed house with a single command. Run `/build` and the plugin generates a **7 X 7 two-story house with a cross-gable roof** a few blocks in front of the player.

---
## Features
- **Instant house generation** — use `/build` to create a ready-made structure in seconds.
- **Decorative roof logic** — generates a mixed-material cross-gable roof with stairs, slabs, overhangs, dormers, trim, and a chimney. Still needs some rework, but mostly there.
- **Simple gameplay utility** — great for quick shelter, testing, or fun server tools.
- **No configuration required** — drop in the jar and start using it immediately.

## It will clear blocks 4 to 5 blocks around the build.

---
## Requirements
- **Minecraft / Spigot:** 1.21.1
- **Java:** 21+
- **API:** Spigot API `1.21.11`

---
## Installation
1. Build the plugin from source (see [Building](#building)) or download the latest release jar.
2. Place the `.jar` file into your server's `plugins/` folder.
3. Start or reload your server.
4. Use `/build` in-game to generate a house.

---
## How It Works
When a player runs `/build`, the plugin creates a predefined two-story structure several blocks in front of the player's current position. The house now uses layered stairs and slabs, overhanging eaves, intersecting gables, dormers, trim accents, and a chimney to make the generated roofline feel more handcrafted.

---
## Command
| Command | Description |
|---------|-------------|
| `/build` | Builds a 7 X 7 two-story house with a decorative cross-gable roof five blocks in front of you.
