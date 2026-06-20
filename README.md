# House — Spigot Plugin for Minecraft 1.21.11

A lightweight Spigot plugin for Minecraft that instantly builds a simple house with a single command. Run `/build` and the plugin generates a **5×5 two-story house** a few blocks in front of the player.

---

## Features

- **Instant house generation** — use `/build` to create a ready-made structure in seconds
- **Simple gameplay utility** — great for quick shelter, testing, or fun server tools
- **No configuration required** — drop in the jar and start using it immediately
- **Lightweight plugin** — focused on one straightforward feature

---

## Requirements

- **Minecraft / Spigot:** 1.21.1
- **Java:** 21+
- **API:** Spigot API `1.21.11-R0.1-SNAPSHOT`

---

## Installation

1. Build the plugin from source (see [Building](#building)) or download the latest release jar.
2. Place the `.jar` file into your server's `plugins/` folder.
3. Start or reload your server.
4. Use `/build` in-game to generate a house.

---

## Building

This project uses Maven. To build from source:

```bash
git clone https://github.com/USMCsky/House_Spigot_Plugin.git
cd House_Spigot_Plugin
mvn clean package
```

The compiled jar will be output to the `target/` directory.

---

## How It Works

When a player runs `/build`, the plugin creates a predefined house structure several blocks in front of the player's current position. The command is designed to provide an instant small shelter without any setup or configuration.

---

## Command

| Command | Description |
|---------|-------------|
| `/build` | Builds a 5×5 two-story house five blocks in front of you |

---

## Plugin Info

| Field   | Value |
|---------|-------|
| Name    | House |
| Version | 1.21.11 |
| Author  | USMCsky |
| Main    | `com.usmcsky.House` |
| API     | 1.21 |
