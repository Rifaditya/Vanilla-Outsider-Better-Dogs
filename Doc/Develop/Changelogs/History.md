# Better Dogs - Historical Changelog

## [1.7.23+1.21.1] - 2026-11-23

### Fixed
- **Wild-Spawn Genetic Scale & Voice Pitch Persistence ([BL-BD-071])**:
  - Updated `WolfMixin.betterdogs$socialScale` initial sentinel from `1.0f` to `0.0f`, rolled `WolfScaleGeneticsHelper.generateWildWolfScale` inside server-side `betterdogs$onFinalizeSpawn`, and persisted via `betterdogs$setSocialScale(wildScale)` updating `Attributes.SCALE`.
  - Guarded lazy `<= 0.0f` rolls in `WolfMixin.betterdogs$getSocialScale()` against client-side worlds (`level().isClientSide()`) so uninitialized client entities return `1.0f` without rolling divergent client genetics.
  - Sanitized `NaN`, non-positive (`<= 0`), and infinite float inputs to `1.0f` in `WolfMixin.betterdogs$setSocialScale`, `WolfPersistentData.writeToNbt`, and `WolfPersistentData.readSocialScaleFromNbt`, preserving legacy `1.0f` defaults when `BetterDogsSocialScale` is absent.
  - Added headless verification suite `ScalePersistenceTest` covering `Unsafe`-allocated `WolfMixin` lifecycle, breeding/runt/cure scale updates, `CompoundTag` round-trips, negative guards, 200-run chaos fuzz, 16-thread concurrency, `/betterdogs inspect`, and Pillar 5 contracts.
