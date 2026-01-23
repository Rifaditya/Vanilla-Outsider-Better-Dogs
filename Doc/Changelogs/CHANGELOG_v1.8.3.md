# Changelog - v1.8.3-26.1

## Baby Wolf Range & Wander Enhancements

This version focuses on further distinguishing baby wolves from adults by emphasizing their "untrained" and unruly nature through significantly increased follow and teleport distances.

### Features & Changes

#### 🐾 Untrained Baby Wolf Behavior

- **Configurable Multipliers**: The following behaviors are now adjustable via `babyFollowMultiplier` and `babyTeleportMultiplier` in `betterdogs.json`.
- **2x Follow Radius (Default)**: Tamed baby wolves now have double the `startDistance` and `stopDistance` in their following behavior compared to adults of the same personality.
  - This reflects their tendency to wander further and not stay as close to their owner as a trained adult would.
- **2x Teleport Threshold (Default)**: The distance at which a baby wolf will teleport to its owner has been doubled.
  - Baby wolves will now stay in the world longer before snapping back to their owner, allowing them more room to explore (or get lost).
  - Default teleport threshold: 12 blocks (144.0 squared).
  - Baby wolf teleport threshold: **24 blocks** (576.0 squared) by default.

### Technical Details

#### [MODIFY] [PersonalityFollowOwnerGoal.java](file:///e:/Minecraft%20Project/Vanilla%20Outsider%20Collections/Better%20Dogs/common/src/main/java/net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java)

- Updated `getStartDistance()` and `getStopDistance()` to check `wolf.isBaby()` and apply a `2.0f` multiplier.
- Updated `tick()` to use a dynamic `teleportThreshold` (576.0 if baby, 144.0 otherwise) and a dynamic check against `startDist`.

### Build Information

| Parameter | Value |
| :--- | :--- |
| Version | 1.8.3-26.1 |
| Target MC | 26.1-snapshot-4 |
| Loaders | Fabric, NeoForge |
| Build Tool | Gradle 9.2.1 |
