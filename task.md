# 🐕 Better Dogs - Active & Backlog Tracking (MC 26.2 & 26.3+)

## Memory Read Receipt
- **Read Timestamp**: 2026-08-25T19:33:00+07:00
- **Source**: `E:\Minecraft Project\.core\logs\MEMORY.md`

---

## 📌 Backlog Tasks

### Phase 1: Targeted Bugfixes
- [x] `[BL-BD-001]` **Minecart & Vehicle Dismount Interaction Interception + Re-boarding Collision Fix** (`4.24.8+26.2` & `5.0.21+26.3`)
  - [x] Add direct passenger dismount check in `DogCommandManager.registerEvents()` when clicking a vehicle containing owned dogs.
  - [x] Apply safe lateral offset positioning on dismount to prevent vanilla `AbstractMinecart` collision re-boarding.
  - [x] Clear `DogCommandManager` vehicle targets and reset `MoveToVehicleGoal` on dismount.
  - [x] Verify dismount audio, particles, and overlay notifications.
- [x] `[BL-BD-002]` **Bone Consumption Guarding & Taming Gating Fix** (`4.24.58-60+26.2` & `5.0.60-62+26.3`)
  - [x] Step 1: Require `player.isSecondaryUseActive()` and `MAIN_HAND` in `WolfGuardHelper.canToggleGuard()`, dual bone consumption, sitting posture preservation (`4.24.58+26.2` & `5.0.60+26.3`).
  - [x] Step 2: Purge legacy duplicate bone guard handling block in `WolfInteractionHelper.java` (lines 281–321) and allow non-sneak sitting (`4.24.59+26.2` & `5.0.61+26.3`).
  - [x] Step 3: Expand JUnit 5 test coverage in `GuardModePatrolTest.java` (`4.24.60+26.2` & `5.0.62+26.3`).

### Phase 2: Foundational Architecture Modularization
- [ ] `[BL-BD-003]` **Full Codebase Audit & Migration to The Clean "1 File, 1 Purpose" Architecture Law**
  - [ ] Deconstruct `WolfCombatHooks.java` into `WolfFlankingCoordinator`, `WolfNemesisCombatHelper`, and `WolfCreeperEvasionHandler`.
  - [ ] Decouple `DogCommandManager.java` into `SeatEntityManager`, `DogCommandEntityListener`, and `DogCommandBlockListener`.
  - [ ] Modularize `WolfTickHelper.java` into `WolfShelterSeeker` and `WolfStormAnxietyManager`.
  - [ ] Streamline `WolfInteractionHelper.java` to a lean delegation router.

### Phase 3: Advanced AI & Environmental Features
- [ ] `[BL-BD-004]` **Wolf Fire Survival AI: Nearest Water Sprint & Fallback Panic Evasion**
  - [ ] Create `WolfSeekWaterOnFireGoal.java` (Priority 1 emergency water seeking for burning wolves).
  - [ ] Implement `WolfCliffSafetyHelper.isSafeWaterJump()` trajectory and fluid depth validation for safe ledge drops into water.
  - [ ] Implement fallback erratic panic running (`1.3x` speed) in safe directions when no water is nearby, with continuous water rescanning.
  - [ ] Whitelist safe water jumps in `WolfSafetyMixin` to prevent cliff drop false-positive freezing.
  - [ ] Register namespaced GameRule `betterdogs:bd_wolves_seek_water_on_fire`.
- [ ] `[BL-BD-005]` **Morning Gift Qualification Conditions & Personality Foraging Loot Overhaul**
  - [ ] Require proximity to sleeping owner in bed (8–10 blocks).
  - [ ] Require 100% full dog health (`wolf.getHealth() >= wolf.getMaxHealth()`).
  - [ ] Enforce once-per-day calendar limit via `WolfPersistentData.getLastGiftDay()`.
  - [ ] Require peaceful environment (no hostile monsters within 16 blocks).
  - [ ] Implement Personality-Themed Foraging loot pools (Aggressive mob trophies, Pacifist nature finds, Normal domestic foraging).
  - [ ] Implement universal 5% Rare Treasure chance (Gold Nuggets, Name Tags, Emeralds, Leads).
  - [ ] Implement datapack loot tables under `data/vanilla-outsider-better-dogs/loot_table/morning_gift/`.

### Phase 4: Data-Driven & Performance Overhaul
- [ ] `[BL-BD-006]` **Modern Sovereign Data-Driven & Zero-Allocation Performance Overhaul**
  - [ ] Replace hardcoded item/block string checks with vanilla datapack tags (`#betterdogs:seats`, `#betterdogs:treats`, `#c:chairs`).
  - [ ] Eliminate hot-path object allocations (`new Random()`, transient `ArrayList` allocations) across all AI goals using `FastRandom` and static reusable buffers.
  - [ ] Delegate generic group leader logic and genetics math cleanly to `DasikLibrary`.

### Phase 5: Automated Test Suite Integration
- [ ] `[BL-BD-007]` **Automated Headless JUnit & GameTest Suite Integration**
  - [ ] Implement JUnit 5 tests under `src/test/java/` for personality math, command parsers, and interaction logic.
  - [ ] Integrate headless Fabric Loom GameTest suites for AI goal verification per Automated GameTest Verification Law.

---

## 🚀 Completed Tasks
- [x] **Nemesis (Grudge) System Implementation** (`4.11.0` / `4.13.0`)
- [x] **Jade Tooltips Integration**
- [x] **Goat Horn Command Suite** (`4.19.0` - `4.23.0`)
- [x] **Creeper Blast Evasion** (`4.24.0`)
- [x] **Dynamic Climate-Aware Wolf Coat Variants** (`4.24.2`)
- [x] **Litematica Compatibility & Command Item Expansion** (`4.24.3`)
- [x] **Minimal Tame Particle Polish** (`4.24.7+26.2` & `5.0.22+26.3`)
- [x] **Minecart Dismount Collision Fix** (`4.24.8+26.2` & `5.0.21+26.3`)
- [x] **Guard Mode Gating & 6D Interaction Overhaul (Step 1)** (`4.24.58+26.2` & `5.0.60+26.3`)
