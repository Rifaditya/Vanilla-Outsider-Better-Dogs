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
- [x] `[BL-BD-003]` **Full Codebase Audit & Migration to The Clean "1 File, 1 Purpose" Architecture Law** (`4.24.61-63+26.2` & `5.0.63-65+26.3`)
  - [x] Step 1: Combat & Targeting Modularization (`WolfDamageHandler`, `WolfTargetingHandler`, `WolfCombatMixin`) (`4.24.61+26.2` & `5.0.63+26.3`).
  - [x] Step 2: Tick & Health Modularization (`WolfHealingHelper`, `WolfSoundHelper`, `WolfGuardHelper`, `WolfNemesisHelper`, `WolfParticleHelper`) (`4.24.62+26.2` & `5.0.64+26.3`).
  - [x] Step 3: Command & Listener Modularization (`DogEntityCommandListener`, `DogBlockCommandListener`, `DogCommandManager`) (`4.24.63+26.2` & `5.0.65+26.3`).

### Phase 3: Advanced AI & Environmental Features
- [x] `[BL-BD-004]` **Wolf Fire Survival AI: Nearest Water Sprint & Fallback Panic Evasion** (`4.24.64-66+26.2` & `5.0.66-68+26.3`)
  - [x] Step 1: Goal & Extinguishing Logic (`WolfSeekWaterOnFireGoal`, GameRules `bd_wolves_seek_water_on_fire`, `bd_wolves_break_sit_on_fire`) (`4.24.64+26.2` & `5.0.66+26.3`).
  - [x] Step 2: Safe Water Drop Cliff Bypass (`WolfCliffSafetyHelper.isSafeWaterLanding`) (`4.24.65+26.2` & `5.0.67+26.3`).
  - [x] Step 3: Headless JUnit 5 Test Suite Expansion (`WolfFireSurvivalTest`) (`4.24.66+26.2` & `5.0.68+26.3`).
- [x] `[BL-BD-005]` **Morning Gift Qualification Conditions & Personality Foraging Loot Overhaul** (`4.24.67-69+26.2` & `5.0.69-71+26.3`)
  - [x] Step 1: Qualification Conditions & Dual Wake-Up Trigger (`WolfGiftHelper`, `WolfGiftGoal`) (`4.24.67+26.2` & `5.0.69+26.3`).
  - [x] Step 2: Data-Driven JSON Loot Tables & Rare Treasures (`loot_table/morning_gift/`, `WolfGiftHelper`) (`4.24.68+26.2` & `5.0.70+26.3`).
  - [x] Step 3: Headless JUnit 5 Test Suite Expansion (`MorningGiftTest`) (`4.24.69+26.2` & `5.0.71+26.3`).

### Phase 4: Data-Driven & Performance Overhaul
- [x] `[BL-BD-006]` **Modern Sovereign Data-Driven & Zero-Allocation Performance Overhaul** (`4.24.70-72+26.2` & `5.0.72-74+26.3`)
  - [x] Step 1: Data-Driven Tags for Foliage, Treats & Seats (`BetterDogsTags`, `tags/block/`, `tags/item/`) (`4.24.70+26.2` & `5.0.72+26.3`).
  - [x] Step 2: Zero-Allocation FastRandom & Loop Memory Overhaul (`WolfPersonality`, `WolfBreedingMixin`, `WolfLitterHelper`) (`4.24.71+26.2` & `5.0.73+26.3`).
  - [x] Step 3: Headless JUnit 5 Performance & Tag Test Suite (`DataDrivenAndPerformanceTest`) (`4.24.72+26.2` & `5.0.74+26.3`).

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
