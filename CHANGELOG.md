# Changelog

## [1.7.9] - 2026-03-02

### Added

- **Feeding**: Tamed dogs can now eat dropped raw and cooked food from the ground to restore health.
- **Toggles**: Added raw and cooked feeding toggles in the "Feeding" config category.
- **Mod Compatibility**: Features full support for modded foods via tags and name-based heuristics.

## [1.7.8] - 2026-01-27

- **Fix**: Adjusted Pacifist follow distance logic to prevent "Yo-Yo" behavior (constantly wandering and being pulled back). Now enforces a minimum start distance of 10.0 blocks, overriding the default 6.0 config if lower, to give them room to breathe.
- **Tuning**: Reduced the chance of Pacifist dogs deciding to wander randomly by 75%. They will now default to sitting/standing by your side much more often than other personalities.

## [1.7.7] - 2026-01-27

- **Safety Logic Hardening**: Removed `isJumping()` and `onGround()` bypasses for Cliff Safety. Now checks strictly based on horizontal movement to catch "pushed" states.
- **Improved Lookahead**: Increased velocity lookahead from 3.0 to 5.0 blocks to better account for jumping arcs.
- **Debug**: Added internal logging for safety triggers (disabled by default).

## [1.7.6] - 2026-01-27

- **CRITICAL FIX**: Fixed a `ClassCastException` crash occurring on legacy worlds or specific setups where `level()` was being cast to `ServerLevel` on the client side during cliff safety checks.
- **Restriction**: Cliff Safety logic now strictly runs on the logical server.

## [1.7.5] - 2026-01-27

- **Cliff Safety (Smart Brakes)**: Ported from 26.1. Wolves now passively detect cliffs 3 blocks ahead.
- **Safer Zoomies**: Ported from 26.1. Zoomies targets are validated for ground safety.

## [1.7.4] - 2026-01-26

- **Maintainance**: Re-built JAR to ensure all recently implemented Kotlin parity features (Mischief, Correction, Blood Feud) are fully compiled and archived.

## [1.7.3] - 2026-01-26

### Added (Parity Update with 26.1)

- **Wolf Training System**:
  - `Baby Mischief`: Aggressive puppies may randomly pick fights (2.5% chance/day).
  - `Baby Bite-Back`: Aggressive puppies retaliate if hit by their owner.
  - `Adult Correction`: Adult wolves discipline behaving puppies.
  - `Blood Feud`: Wolves can hold persistent grudges.
- **Wolf Events**:
  - `Zoomies`: Happy wolves may run around energetically.
  - `Howl`: Wolves may howl at the sky in groups.
- **Config**: Added configuration options for all new features in `betterdogs.json`.

## [1.7.2] - 2026-01-26

### Fixed

- Fixed a game crash occurring when wolves attempt to check airborne targets during cliff safety logic (Issue #1).
- Added null safety check for wolf target in `WolfMixin`.

## [1.7.1]

- Previous version (Issue #1 reported on this version).
