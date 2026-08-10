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

*Back to [[Home]]*
