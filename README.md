# AutoSell

A Fabric client-side mod for Minecraft 1.21.x that automatically runs `/sellall <blockid>` when a tracked block enters your inventory.

Works with [EconomyShopGUI](https://www.spigotmc.org/resources/economyshopgui.69927/) and any other shop plugin that exposes a `/sellall <blockid>` command (plain block ID, no `minecraft:` namespace required).

---

## Features

- Open the config menu in-game with `.autosell` in chat — the message is intercepted client-side and never sent to the server.
- Set the **Block ID** (e.g. `basalt`) and a **Time Delay (ms)** before the sell fires.
- When the tracked item count in your inventory increases, the mod waits for the configured delay then executes `/sellall <blockid>`.
- A **Disable** button in the menu pauses selling without clearing your settings.
- Config resets on server join to prevent stale state.

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.x |
| Fabric Loader | ≥ 0.19.0 |
| Fabric API | 0.141.3+1.21.11 (or compatible) |
| Java | 21+ |

---

## Building

```bash
./gradlew build
```

The output JAR will be at `build/libs/autosell-<version>.jar`.

---

## Usage

1. Drop the built JAR into your `.minecraft/mods/` folder alongside Fabric API.
2. Join a server that has EconomyShopGUI (or equivalent `/sellall <blockid>` support).
3. Type `.autosell` in chat to open the config screen.
4. Enter the **Block ID** (e.g. `basalt`) and a **Delay (ms)** (e.g. `500`).
5. Click **Save**. The mod will now automatically sell that block whenever it enters your inventory.
6. Open `.autosell` again and click **Disable** to pause selling.
