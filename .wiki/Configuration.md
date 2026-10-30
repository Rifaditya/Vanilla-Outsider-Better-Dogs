# Configuration & GUI Integration Guide

*[[Home]] / Configuration*

---

## ⚙️ Configuration Architecture & Server Authority

**Vanilla Outsider: Better Dogs** utilizes a two-tier configuration model that guarantees zero client/server desynchronization while maintaining complete server-side authority:

```
┌─────────────────────────────────────────────────────────┐
│              Global Config File                         │
│           (config/betterdogs.json)                      │
│   Defines baseline defaults for NEW worlds ONLY         │
└────────────────────────────┬────────────────────────────┘
                             │ (World Creation Init)
                             ▼
┌─────────────────────────────────────────────────────────┐
│            Active World GameRules                       │
│             (betterdogs:bd_*)                           │
│   Dynamic Server Authority for Existing Worlds          │
└─────────────────────────────────────────────────────────┘
```

> [!WARNING]
> **Global Configuration File Warning**: Modifying `config/betterdogs.json` on disk or via the main menu GUI **only sets default values for newly created worlds**. To change settings in an existing singleplayer world or multiplayer server, modify rules via `/gamerule betterdogs:<rule> <value>` or the in-game command suite `/betterdogs set <rule> <value>`.

---

## 🎨 Client GUI Integration (ModMenu & YACL v3)

**Better Dogs** provides optional client GUI support when installed alongside **ModMenu** and **YetAnotherConfigLib v3 (YACL)**:

### 1. ModMenu Integration
Access the configuration screen directly from the Fabric ModMenu list by selecting **Vanilla Outsider: Better Dogs** and clicking **Configure**.

### 2. Category Breakdown in YACL GUI
The YACL v3 configuration interface is organized into distinct categories:

* **General Options**: Global speed buffs, storm anxiety toggle, cliff safety toggle, and creeper avoidance.
* **Wolf Personalities**: Per-personality health bonuses, sprint speed modifiers, attack damage tweaks, and follow start distances for Aggressive, Pacifist, and Normal traits.
* **Genetics & Breeding**: Personality inheritance percentages, cross-breeding mutation chances, and inbreeding cure toggles.
* **Wild Territoriality**: Pack cluster sizes, territorial dispute matrices (Aggro vs Aggro, Aggro vs Pacifist, etc.), and search radius bounds.
* **Gifts & Rewards**: Interaction feed thresholds and gift cooldown settings.
* **Visual & Performance Options**: Guard particle density modes (off, low, medium, high) and runt particle toggles.

---

## 🛡️ Server Crash Safety & Gating

All client GUI components (`ModMenuIntegration`, `YaclScreenHelper`) are safely gated behind client entrypoints (`entrypoints.modmenu`) and runtime environment checks. **Better Dogs** can be installed on dedicated headless servers without ModMenu or YACL present, ensuring **zero server-side classloading crashes**.

---

## 📦 Uninstallation & World Save Lifecycle

Because this mod utilizes native vanilla GameRules for per-world configuration (rather than a static global config file), active rule values are saved directly inside each world's data file (`saves/<world>/level.dat` under `Data.GameRules`).

### What Happens If You Remove the Mod:
If you uninstall this mod and load an existing world that was previously played with it:
1. **Informational Log Warning:** Vanilla Minecraft will log warning lines in `latest.log`:
   `Unknown registry key in ResourceKey[minecraft:root / minecraft:game_rule]: betterdogs:...`
2. **`minecraft:root` is an in-memory Java registry, NOT a folder:** `ResourceKey[minecraft:root / ...]` is Mojang's internal code identifier for the game's in-memory registry hierarchy. It is strictly an in-memory data structure in Java, not a physical directory on your drive. Zero files in your `.minecraft` directory or system are modified or overwritten.
3. **Identical to Mojang's Engine Mechanics:** This operates **exactly like how Mojang handles removed, renamed, or changed GameRules between vanilla updates** (or disabled experimental data packs). Minecraft safely skips over unknown keys during gameplay.
4. **Automatic Vanilla Pruning on Save:** Vanilla Minecraft only serializes active, registered GameRules when saving. The moment you save the world (**"Save and Quit to Title"** or after an auto-save), vanilla writes a clean `level.dat` containing only recognized keys, permanently purging all orphaned rules. Subsequent world loads will produce zero warnings.
5. **Harmless:** The warning is completely safe to ignore.

---

*Back to [[Home]]*
