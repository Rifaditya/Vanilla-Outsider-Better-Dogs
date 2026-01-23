# Changelog v1.8.5-26.1

**Release Date:** 2026-01-23

## 🛠 Bug Fixes

### Persistent Crash Fix

- Stabilized Mixin transformation in the 26.1 snapshot environment by adding a mandatory `refmap` and using `remap = false` for inherited `doHurtTarget` injections.

### License Alignment

- Aligned mod metadata with the project's GPL-3.0 license in both loaders.

---

## Technical Details

This release addresses a critical mixin injection issue where the `doHurtTarget` method could not be resolved correctly due to missing refmap and remapping conflicts in the 26.1 snapshot environment.

### Changes Made

- Added `remap = false` to `MobMixin.doHurtTarget` injection
- Ensured mixin configuration includes proper refmap settings
- Verified metadata license matches LICENSE file (GPL-3.0-or-later)
