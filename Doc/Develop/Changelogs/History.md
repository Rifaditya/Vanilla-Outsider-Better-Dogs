# Better Dogs - Historical Changelog

## [1.7.24+1.21.1] - 2026-11-23

### Fixed
- **Sitting Pose State Desync During Movement & AI Transitions ([BL-BD-069])**:
  - **Reported By**: `Apeirohex` via Discord community support (credited in `CHANGELOG.md`).
  - Added single-purpose `WolfSittingPoseHelper` (against `net.minecraft.world.entity.animal.Wolf`) and `WolfExtensions.betterdogs$isPerformingScoot()` seam for future `BL-BD-070` scoot behavior across Fabric and NeoForge, wired into `WolfMixin.betterdogs$onTick` (`syncSittingPoseOnTick`) to clear non-ordered sitting pose (`setInSittingPose(false)`) only on real server-side transitions when navigating, targeting, or moving beyond `1.0e-4` horizontal sqr / `0.15` vertical delta (preserving resting `-0.0784` gravity, `isOrderedToSit()`, and `isPassenger()`).
  - Updated `PersonalityFollowOwnerGoal` (`start()` unordered pose clear; `stop()` gated by `canEnterLazyRestingPose(...)` and `(0.0, dm.y, 0.0)` horizontal inertia zeroing so tick-0 follow deceleration does not immediately cancel lazy resting).
  - Wired `WolfMobMixin` (`setTarget` `@At("TAIL")`) and `WolfMixin.betterdogs$onHurt` (`clearSittingPoseOnDamage` after cancellation guards) to stand lazy-resting dogs up immediately upon combat targeting or non-cancelled damage.
  - Updated companion AI goals (`TamedWanderNearOwnerGoal`, `WanderlustGoal`, and `WolfSeekWaterOnFireGoal`) to clear stale unordered sitting poses, and bound `wasSitting` in `AvoidGhastFireballGoal`, `FleeExplodingHazardGoal`, and `WolfProjectileInterceptGoal` strictly to `isOrderedToSit()` so evasion never promotes lazy rests into permanent ordered sits.
  - Verified across Fabric and NeoForge with `SittingPoseStateTransitionTest` (`348/348`).

## [1.7.23+1.21.1] - 2026-11-23

### Fixed
- **Wild-Spawn Genetic Scale & Voice Pitch Persistence ([BL-BD-071])**:
  - **Reported By**: `@UnderscoreNash` via GitHub Issue #14 (credited in `CHANGELOG.md`).
  - Updated `WolfMixin.betterdogs$socialScale` initial sentinel from `1.0f` to `0.0f`, rolled `WolfScaleGeneticsHelper.generateWildWolfScale` inside server-side `betterdogs$onFinalizeSpawn`, and persisted via `betterdogs$setSocialScale(wildScale)` updating `Attributes.SCALE`.
  - Guarded lazy `<= 0.0f` rolls in `WolfMixin.betterdogs$getSocialScale()` against client-side worlds (`level().isClientSide()`) so uninitialized client entities return `1.0f` without rolling divergent client genetics.
  - Sanitized `NaN`, non-positive (`<= 0`), and infinite float inputs to `1.0f` in `WolfMixin.betterdogs$setSocialScale`, `WolfPersistentData.writeToNbt`, and `WolfPersistentData.readSocialScaleFromNbt`, preserving legacy `1.0f` defaults when `BetterDogsSocialScale` is absent.
  - Added headless verification suite `ScalePersistenceTest` covering `Unsafe`-allocated `WolfMixin` lifecycle, breeding/runt/cure scale updates, `CompoundTag` round-trips, negative guards, 200-run chaos fuzz, 16-thread concurrency, `/betterdogs inspect`, and Pillar 5 contracts.
