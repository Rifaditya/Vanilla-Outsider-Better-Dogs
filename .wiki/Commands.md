# Brigadier Command Suite Reference

*[[Home]] / Commands*

---

## 💻 Brigadier Command Suite & Syntax

Server administrators (Permission Level 2 / `LEVEL_GAMEMASTERS`) can administer server rules and execute debug scenarios using native commands:

### 1. Dynamic GameRule Administration (`/gamerule`)
Server options can be updated dynamically at any time using Minecraft's native `/gamerule` command:

```
/gamerule betterdogs:bd_friendly_fire_protection false
/gamerule betterdogs:bd_wolf_min_scale_percent 80
/gamerule betterdogs:bd_debugging true
```

### 2. Built-In Brigadier Debug Command Tree (`/betterdogs`)

The `/betterdogs` command tree provides advanced debug tools for testing AI behaviors and scenarios. Subcommands are gated by the boolean rule `/gamerule betterdogs:bd_debugging true`:

```
/betterdogs debug personality <targets> <type>
/betterdogs debug action <targets> <actionType> [secondaryTarget]
/betterdogs debug territory
```

* **`/betterdogs debug personality <targets> <normal|aggressive|pacifist>`**: Dynamically forces the personality trait of selected targeted wolves.
* **`/betterdogs debug action <targets> <actionType> [secondaryTarget]`**: Forces targeted wolves to immediately execute specific social actions (e.g. `howl`, `zoomies`, `beg`, `fetch`).
* **`/betterdogs debug territory`**: Spawns a pre-configured wild pack territorial dispute scenario around the player to test pack war, merge, and retreat AI logic.

---

*Back to [[Home]] | View [[GameRules]]*
