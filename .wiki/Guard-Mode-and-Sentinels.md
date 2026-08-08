# Guard Mode & Sentinel Patrols

*[[Home]] / [[Player Guide & Mechanics|Player-Guide-and-Mechanics]] / Guard Mode*

---

## 🛡️ Infobox: Guard System Summary

| Feature | Specification |
| :--- | :--- |
| **Activation Trigger** | Shift + Right-Click tamed wolf while holding a **Bone** (`minecraft:bone`) |
| **Required State** | Tamed wolf owned by player |
| **Aggressive Patrol Radius** | **12 blocks** (`bd_guard_patrol_range_aggressive`) |
| **Normal Patrol Radius** | **0 blocks** (Stationary Post) (`bd_guard_patrol_range_normal`) |
| **Pacifist Patrol Radius** | **3 blocks** (`bd_guard_patrol_range_pacifist`) |
| **Pacifist Special Buff** | Regeneration I & Resistance I aura applied to owner within 3 blocks (`bd_pacifist_guard_buffs`) |
| **Particle Indicators** | Guard alarm particles (`off`, `low`, `medium`, `high`) |
| **Associated Advancement** | **On Guard!** (Task) & **A Pack of Guardians** (Goal) |

---

## 📐 1. Patrol Radiuses & Sentinel AI

When Guard Mode is activated, the wolf's current position is recorded as its **Guard Anchor Post** `(guard_x, guard_y, guard_z)`. The wolf's AI switches from owner follow to sentinel patrol:

```
        Aggressive Guard Patrol (12 Blocks)
      ┌─────────────────────────────────────┐
      │   . . . . . . . . . . . . . . . .   │
      │   . . . . . . . . . . . . . . . .   │
      │   . . . . . ┌─────────┐ . . . . .   │
      │   . . . . . │ Pacifist│ . . . . .   │
      │   . . . . . │ (3 Blk) │ . . . . .   │
      │   . . . . . │  [POST] │ . . . . .   │
      │   . . . . . └─────────┘ . . . . .   │
      │   . . . . . . . . . . . . . . . .   │
      └─────────────────────────────────────┘
```

### Patrol Mechanics by Personality
1. **Aggressive Guard**: Patrols within 12 blocks of post. Engages any hostile mob entering range. On Patrol advancement triggers when an Aggressive guard kills a mob in range.
2. **Normal Guard**: Anchors strictly at post (0 block wander). Defends post against approaching threats.
3. **Pacifist Guard**: Wanders within 3 blocks of post. Emits a soothing aura every 20 ticks (1 second) applying **Regeneration I** and **Resistance I** to the owner.

---

## 🔀 2. State Commands & Activation

* **Manual Activation**: Shift + Right-Click with a bone.
* **Goat Horn Activation**: Sounding a **Ponder Horn** within 64 blocks orders all following owned wolves to enter Guard Mode at their current locations.
* **Deactivation**: Shift + Right-Clicking a guarding wolf with a bone deactivates Guard Mode, returning it to owner follow state.

---

*Back to [[Home]] | View [[Wolf-Personalities]]*
