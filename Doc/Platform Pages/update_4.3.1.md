# 🐕 Better Dogs - Version 4.3.1 (The Range Stats Update)

Welcome to the **Range Stats Update**! Version 4.3.1 introduces a fully dynamic, personality-based stats system for tamed wolves. Instead of every dog of a certain personality getting identical static stat bonuses, each wolf now has its own unique, randomized attributes determined by its personality ranges.

---

## 📊 Personality-Based Range Stats

When a wolf is tamed (or on first load for existing tamed dogs), it rolls its Max Health, Attack Damage, and Movement Speed based on its personality. 

### 1. The Stat Ranges
* **Normal**: 
  * Max Health: `[-12.0, +8.0]` HP (Worst: **28 HP** / Average: **38 HP** / Best: **48 HP**)
  * Attack Damage: `[-30%, +20%]` (Worst: **2.8** / Average: **3.8** / Best: **4.8**)
  * Movement Speed: `[-20%, +15%]` (Worst: **0.240** / Average: **0.293** / Best: **0.330**)
* **Aggressive**:
  * Max Health: `[-16.0, +6.0]` HP (Worst: **24 HP** / Average: **35 HP** / Best: **46 HP**)
  * Attack Damage: `[-10%, +40%]` (Worst: **3.6** / Average: **4.6** / Best: **5.6**)
  * Movement Speed: `[-10%, +25%]` (Worst: **0.270** / Average: **0.323** / Best: **0.375**)
* **Pacifist**:
  * Max Health: `[-4.0, +26.0]` HP (Worst: **36 HP** / Average: **51 HP** / Best: **66 HP**)
  * Attack Damage: `[-50%, +10%]` (Worst: **2.0** / Average: **3.2** / Best: **4.4**)
  * Movement Speed: `[-35%, +5%]` (Worst: **0.195** / Average: **0.255** / Best: **0.315**)

### 2. Symmetric Triangular Distribution
Stats are rolled using Minecraft's built-in **triangular distribution**, meaning the rolled values cluster heavily around the average (mean) for each personality. Extreme values (such as an exceptionally overpowered dog or a weak, "handicapped" dog) are statistically rare but possible.

### 3. Dynamic Overlap
Because the stat ranges are wide and overlap, you can have unique outcomes:
* An exceptionally lucky **Normal** dog can end up deal higher damage and run faster than a low-rolled **Aggressive** dog.
* A high-rolled **Pacifist** dog can outclass a low-rolled **Normal** dog in combat performance while having up to **66 HP (33 full hearts)**.
* Unlucky rolls will result in weaker or slower dogs, encouraging you to spend time and bonds to discover each dog's unique traits!

---

## 🧬 Deterministic UUID Seeding & Backward Compatibility

* **UUID-Seeded Rolls**: Stats are rolled using the wolf's unique UUID. This ensures the roll is 100% deterministic and persistent; a wolf will keep the exact same stats across level reloads, server restarts, and mod updates.
* **Seamless Migration**: When upgrading from a previous version of the mod, existing tamed wolves will automatically roll and lock in their stats on first load.
* **Dynamic Re-rolling on Personality Change**: If a wolf's personality is cycled via the Debug Stick or updated using the `/betterdogs personality` command, its rolled stats are automatically cleared and re-rolled to match the new personality.

---

## 📡 Vanilla Client Compatibility

Like all features in the mod, the range stats system is **100% server-side compatible**. Unmodded vanilla clients can join your server or multiplayer world and experience the dynamic health differences (reflected in their heart bars and collar tails) and speed changes with zero mod files installed locally.

---

## 📜 Full Changelog

* **Range-Based Stats**: Implemented personality-defined attribute modifier ranges for tamed wolves.
* **Triangular Distribution**: Configured rolls using a symmetric triangular distribution to make extreme high/low rolls rare.
* **UUID Seeding**: Linked rolls to the wolf's unique UUID for persistent and deterministic stat saving.
* **Compatibility & Upgrades**: Implemented clean upgrade pathways and automatic re-rolling on personality updates.
