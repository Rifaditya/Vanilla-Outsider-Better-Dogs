# Better Dogs - Changelog

## v1.8.1-26.1 (2026-01-23)

### 🎉 New Features

- **Passive Baby Wolves**: Baby wolves are now passive by default to protect them from unnecessary combat.
  - Baby wolves will NOT attack what the owner attacks.
  - Baby wolves will NOT defend the owner if the owner is hit.
  - **Exception**: Baby wolves with the **Aggressive** personality will still hunt monsters as usual.
  - **Exception**: Any baby wolf will still defend itself if directly hit by a mob.

### 🔧 Technical Changes

- Added `OwnerHurtByTargetGoalMixin` to inhibit defensive behavior in babies.
- Updated `OwnerHurtTargetGoalMixin` to inhibit offensive behavior in babies.
- Updated `PacifistRevengeGoal` and `WildWolfHuntGoal` to exclude babies.
- Bumped version to `1.8.1-26.1`.

### 📦 Build Information

| Loader | File | MC Version |
| :--- | :--- | :--- |
| Fabric | `vanilla-outsider-better-dogs-fabric-1.8.1-26.1.jar` | 26.1-snapshot-4 |
| NeoForge | `vanilla-outsider-better-dogs-neoforge-1.8.1-26.1.jar` | 26.1-snapshot-4 |

---

## v1.8.0-26.1 (Previous)

- Multi-loader support (Fabric & NeoForge)
- See `CHANGELOG_v1.8.0.md` for details.
