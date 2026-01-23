# Better Dogs - Changelog

## v1.8.2-26.1 (2026-01-23)

### 🎉 New Features

- **Domestic Aggression & Retaliation**:
  - Baby wolves now remember their manners! If punched by their owner, they will retaliate with **2 strikes** before calming down.
- **Aggressive Adult Intervention**:
  - Aggressive adult wolves now act as "enforcers". If they see a baby wolf attacking its owner, they will intervene to protect the owner.
- **Disciplined Combat (v1)**:
  - Aggressive wolves now follow a **3-strike sequence** when intervening in domestic disputes. After 3 hits, they will cease aggression.
- **Puppy Protection**:
  - Other wolves (wild or tamed) will no longer target or attack player-owned baby wolves, except for Aggressive enforcers during an intervention.

### 🛠 Bug Fixes

- **Personality Stats Rebalance**:
  - Fixed a bug where Aggressive and Pacifist stats were swapped in the config defaults.
  - **Aggressive**: +50% Damage, -10 HP (Total 30 HP).
  - **Pacifist**: -30% Damage, +20 HP (Total 60 HP).

### 🔧 Technical Changes

- Added `DomesticRetaliationGoal` for strike-limited counters.
- Added `InterventionGoal` for aggressive enforcer behavior.
- Updated `actuallyHurt` in `WolfMixin` to handle domestic damage rules and escalation.
- Enhanced `wantsToAttack` with safety checks for tamed babies.

### 📦 Build Information

| Loader | File | MC Version |
| :--- | :--- | :--- |
| Fabric | `vanilla-outsider-better-dogs-fabric-1.8.2-26.1.jar` | 26.1-snapshot-4 |
| NeoForge | `vanilla-outsider-better-dogs-neoforge-1.8.2-26.1.jar` | 26.1-snapshot-4 |

---

## v1.8.1-26.1 (Previous)

- Passive baby wolf behavior.
- See `CHANGELOG_v1.8.1.md` for details.
