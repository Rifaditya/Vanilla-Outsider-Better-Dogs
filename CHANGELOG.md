# Changelog

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
