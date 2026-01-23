# Changelog v1.8.7-26.1

**Release Date:** 2026-01-23

## 🛠 Bug Fixes

### Stale Class Cleanup

- Clean rebuild to eliminate stale `betterdogs$afterHurt` mixin injection that was causing crash on game launch.

---

## Technical Details

This release fixes a critical startup crash caused by stale compiled class files that remained in the build artifacts after previous changes.

### Root Cause

The deployed JAR contained an outdated `WolfMixin.class` with a `betterdogs$afterHurt` injection targeting `doHurtTarget` on `Wolf.class`. This injection was removed from source code in v1.8.5 (moved to `MobMixin`), but the old compiled class persisted due to incremental builds.

### Fix Applied

- Ran `gradlew clean build` to eliminate all stale class files
- Rebuilt both Fabric and NeoForge JARs from clean state
- Version bumped to ensure unique build artifacts

### Lesson Learned

Always run `gradlew clean` when mixin targets change to prevent stale injection references.
