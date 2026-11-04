# HUD & Diagnostics Guide

*[[Home]] / HUD & Diagnostics*

---

## 🔍 Jade (WTHIT) HUD Overlay Integration

**Vanilla Outsider: Better Dogs** embeds a native plugin for **Jade (WTHIT)** (`BetterDogsJadePlugin`) that displays live companion diagnostics when looking at a wolf:

```
┌──────────────────────────────────────────────┐
│  Wolf [Aggressive]                           │
│  Health: ❤❤❤❤❤❤❤❤❤❤ (20/20)               │
│  Favorite Treat: Cooked Porkchop             │
│  Status: Guarding (Post: 12, 64, -145)      │
└──────────────────────────────────────────────┘
```

### Supported Jade HUD Displays

| HUD Component | Localization Key | Description |
| :--- | :--- | :--- |
| **Personality Badge** | `config.jade.plugin_vanilla-outsider-better-dogs.wolf_info` | Displays active personality trait (Normal, Aggressive, Pacifist). |
| **Inbred Runt Warning** | `betterdogs.jade.inbred` | Displays a warning badge if the wolf suffers from linebreeding penalties. |
| **Favorite Treat** | `betterdogs.jade.treat` | Displays the discovered favorite treat item for the targeted wolf. |
| **Dynamic Health Bar** | `config.jade.plugin_vanilla-outsider-better-dogs.wolf_health` | Renders accurate max health bars accounting for personality HP bonuses. |

---

## ✨ Visual Particles & Alarm Densities

### 1. Guard Alarm Particles (`guardParticleDensity`)
Guarding wolves emit subtle visual alarm particles indicating their active sentinel post:

* **Modes**: `off`, `low`, `medium` (default), `high`.
* **Configuration**: Set in client GUI or `config/betterdogs.json`.

### 2. Runt Particles (`bd_show_runt_particles`)
* **Behavior**: If enabled (`/gamerule betterdogs:bd_show_runt_particles true`), tamed inbred runt wolves emit tiny rotten flesh particles to help players identify linebreeding penalties visually.

---

## 🛠️ Debug Stick & Diagnostic Administration

When debugging is enabled via `/gamerule betterdogs:bd_debugging true`, server administrators gain access to diagnostic shortcuts:

### Vanilla Debug Stick Shortcut
* **Personality Cycling**: Right-clicking a wolf with a vanilla **Debug Stick** cycles its personality trait (`NORMAL` -> `AGGRESSIVE` -> `PACIFIST`).
* **Scale Cycling**: Shift+Right-clicking a wolf with a Debug Stick cycles its physical rendering scale.

---

## 📋 Console Diagnostics & Multi-Loader Startup Notices

### NeoForge Advisory Notice: "Identified an attempt to add synced data to a foreign entity"

When launching Better Dogs on NeoForge (**MC 1.20.1, 1.21.1, and 1.21.11**), NeoForge's sync worker logs a one-time advisory notice:
```text
[modloading-sync-worker/WARN]: Identified an attempt to add synced data to a foreign entity: this is highly discouraged.
Entity class: net.minecraft.world.entity.animal.wolf.Wolf
Mixins into entity class: net.vanillaoutsider.betterdogs.mixin.WolfMixin
```

* **Severity**: `WARN` (harmless developer advisory, printed once on startup).
* **Does it cause issues?**: No. It is not an error or crash. Wolves synchronize and behave properly on both singleplayer and multiplayer servers.
* **Why does it appear?**: Better Dogs synchronizes visual defeated postures (`isSubdued`) and Spectator-mode wagging (`isSpectatorWagging`) using vanilla Minecraft's universal `SynchedEntityData`. This allows the exact same `:common` module code to execute seamlessly on both Fabric and NeoForge with zero third-party dependencies.
* **Full Technical FAQ**: See [[Version Compatibility & Lifecycle Policy|Version-Compatibility]] for detailed multi-loader documentation.

---

*Back to [[Home]]*
