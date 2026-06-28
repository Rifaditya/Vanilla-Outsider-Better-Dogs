# 🐕 Better Dogs - Nemesis (Grudge) System Implementation

- `[x]` Integrate Jade Tooltips
  - [x] Configure dependencies (`modCompileOnly`) and create plugin entrypoint
  - [x] Implement Favorite Treat Tooltip Provider
  - [x] Implement Accurate Health Provider (Max health and Current health overriding Jade's default)
  - [x] Build and test 4.13.0-26.2
- `[x]` **1. NBT Updates (`WolfPersistentData.java`)**
  - Add `String nemesisType` to store the entity type ID (e.g. "minecraft:skeleton").
  - Add `long nemesisExpiry` to store the world tick expiration.
  - Update BaseData Codec, getters, and setters.
- `[x]` **2. Configuration Updates (`BetterDogsGameRules.java`)**
  - Register `bd_nemesis_system` (Boolean, default true).
  - Register `bd_nemesis_duration_days` (Integer, default 3).
- `[x]` **3. Death Hook (`WolfMixin.java`)**
  - Inject into `die(DamageSource)`.
  - On death by `LivingEntity`, scan for nearby tamed wolves of the same owner and apply Nemesis data.
- `[x]` **4. Combat Buffs (`WolfTickHelper.java`)**
  - Verify expiration logic in tick loop.
  - Apply `DAMAGE_BOOST` and `MOVEMENT_SPEED` if attacking Nemesis.
  - Spawn angry particles.
- `[x]` **5. AI Targeting (`WolfNemesisTargetGoal.java`)**
  - Create the new high-priority Goal class for auto-targeting Nemesis mobs.
  - Register the goal in `WolfMixin.java` `registerGoals()`.
- `[x]` **6. Version Bump & Changelog (`gradle.properties`, `fabric.mod.json`, `History.md`)**
  - Update version to `4.11.0`.
  - Populate changelogs and update `RELEASE_QUEUE.md`.
- `[ ]` **7. Parity Backport (v3.12.0)**
  - Mirror all changes to the 26.1.2 workspace.
- `[ ]` **8. Audit & Build**
  - Code Auditor verification.
  - Build successfully using Java 25.

## Memory Read Receipt
Verified against MEMORY.md last updated: 2026-06-26 22:14:06
