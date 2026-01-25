# Audit Helper for Moderators

## Safety Declaration

- **Network**: No external network calls (except Mojang Auth/Session).
- **Telemetry**: Disabled. No analytics.
- **Filesystem**: Config file only (`config/betterdogs.json`) and `PersistentData` (NBT).

## Technical Justifications (Addressing Potential Flags)

### 1. Explicit Behavior Injection (`WolfScheduler`)

You may see code where one entity (Baby Wolf) calls `injectBehavior` on another entity (Adult Wolf).

- **Purpose**: This is the "Snitch" optimization system. Instead of Adult Wolves scanning for targets every tick (High CPU), the Baby Wolf acts as the trigger and "wakes up" the Adult only when necessary.
- **Safety**: This is strictly limited to `Wolf` entities owned by the same player.

### 2. Mixin "Gatekeeper" (`WolfMixin.setTarget`)

We inject a cancellation check into `setTarget`.

- **Purpose**: To prevent the vanilla AI (which constantly tries to retarget skeletons/zombies) from overriding our custom "Social Interactions" (like discipline).
- **Logic**: If `isSocialModeActive()` is true, all external targeting attempts are blocked until the social interaction completes.

### 3. Friendly Fire Bypass (`WolfCombatHooks`)

We modify damage logic to allow specific friendly fire instances.

- **Purpose**: To allow "Retaliation" (Baby bites Owner) and "Correction" (Adult bites Baby).
- **Limit**: Strictly capped at 1 hit per interaction.

## Commands

- `/betterdogs debug` (Creative Only): Spawns formatted debug text for wolf testing.

## Known Issues

- None critical.
