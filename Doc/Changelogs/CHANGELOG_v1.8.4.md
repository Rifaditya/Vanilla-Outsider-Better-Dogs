# Changelog - v1.8.4-26.1

## Baby Wolf Curiosity & Reckless Aggression

This version introduces advanced behavioral patterns for baby wolves, further distinguishing them from adults based on their personality and age.

### Features & Changes

#### 🧠 Baby Curiosity (Passive/Normal)

- **Curiosty AI**: Non-aggressive baby wolves now exhibit natural curiosity.
- **Entity Observation**: They will approach nearby mobs (passive or hostile) and players to "stare" at them for 2-6 seconds.
- **Environmental Focus**: They will wander towards interesting blocks like flowers, grass, and trees to investigate their surroundings.
- **Idle Behavior**: This behavior triggers primarily when the wolf is idle and not following the owner closely.

#### ⚔️ Reckless Aggression (Aggressive)

- **Immediate Engagement**: Aggressive baby wolves now immediately engage and attack hostile mobs they detect.
- **Recklessness**: Unlike adults who typically stay within a certain range of their owner, aggressive babies will chase and attack monsters regardless of owner proximity, provided they are within detection range.

### Technical Details

#### [NEW] [BabyCuriosityGoal.java](common/src/main/java/net/vanillaoutsider/betterdogs/ai/BabyCuriosityGoal.java)

- Implemented state-based AI for movement and looking at points of interest (POI).
- Includes a weighted selection between entity POIs and block POIs (60/40 split).

#### [MODIFY] [WolfMixin.java](common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMixin.java)

- Registered `BabyCuriosityGoal` at priority 7.

#### [MODIFY] [AggressiveTargetGoal.java](common/src/main/java/net/vanillaoutsider/betterdogs/ai/AggressiveTargetGoal.java)

- Waived owner-proximity checks for baby wolves in `canUse` and `canContinueToUse`.

### Build Information

| Parameter | Value |
| :--- | :--- |
| Version | 1.8.4-26.1 |
| Target MC | 26.1-snapshot-4 |
| Loaders | Fabric, NeoForge |
| Build Tool | Gradle 9.2.1 |
