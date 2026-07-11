# Concept: Goat Horn Command System

This document outlines the design and implementation specifications for the **Goat Horn Command System** under the Vanilla Outsider (VO) philosophy. It enables players to coordinate packs of wolves using vanilla Goat Horn instruments without menus, hotkeys, or GUI elements.

---

## 🏛️ Core Design Philosophy (Vanilla Outsider)
* **Immersion & Diegesis**: Commands are issued using existing vanilla items, sounds, and use animations.
* **One Click, One Action**: Blowing the horn is the single trigger; wolves respond organically using standard pathfinding rather than artificial teleportation.
* **Respecting Posture**: Sitting dogs remain sitting to preserve guard posts or home anchors, while active dogs respond to the call.
* **Tactical Overrides**: Combat behaviors can be dynamically altered for short tactical windows (30 seconds) without permanently changing the dogs' personalities, attributes, or roaming sizes.
* **Exclusivity & Preemption**: A dog can only execute **one** horn command state at a time. If a new, different horn command is sounded (even during the vanilla 7-second cooldown window), it immediately preempts, cancels, and replaces the current active command state (e.g., blowing a tactical Pacifist horn cancels an active Assemble pathing goal and clears its target coordinates).
* **Feedback via Particles**: To indicate the command was successfully received without relying on UI text, wolves emit subtle, custom-colored particles upon hearing the sound.
  * > [!IMPORTANT]
  * > **Brief & Temporary**: All command particles are single-burst or brief trailing visual confirmations (emitted once via `serverLevel.sendParticles` when the command is received). They do **not** run as continuous loop emitters, ensuring the display remains clean and free of visual noise.
* **Player Discovery**: No custom item tooltips are added; players are left to experiment and discover which horn corresponds to which command.

---

## 📅 Implementation Stages
> [!NOTE]
> Each stage represents a separate minor version update (e.g., v4.15.0, v4.16.0, etc.) in the release queue when we actively build and implement them.

### 🎬 Stage 1: Ponder Horn (Assemble Call & Broadcaster)
The first phase implements the base broadcaster infrastructure and the first operational command—the "Assemble Call".
* **Goat Horn Broadcaster**:
  * Mixin on `GoatHornItem.use` (Server-side only) to intercept the instrument data component (`minecraft:instrument`).
  * Resolves the instrument identifier (e.g., `ponder_goat_horn`).
  * Selects all wolves in loaded chunks that are currently actively following the player:
    * Must be tamed by the player blowing the horn (`wolf.isOwnedBy(player)`).
    * Must not be manually sitting (`!wolf.isOrderedToSit()`).
    * Must not be locked in Guard Mode (`!wolfExt.betterdogs$isGuardMode()`).
* **Transient Target Attachment**:
  * Registers `soundLocationTarget` (BlockPos) via data attachments on the wolf.
  * Setting this coordinate target immediately halts existing pathfinding/wandering goals.
* **Custom AI Goal (`PathToSoundLocationGoal`)**:
  * A high-priority AI task that executes when `soundLocationTarget` is set.
  * Commands the wolf to pathfind directly to the `soundLocationTarget` block coordinate at `1.25x` speed.
  * Expires when the wolf is within 3 blocks of the target, or after a 20-second (400 tick) safety timeout.
* **Particle Feedback**:
  * Wolves emit a brief, trailing burst of **music note / happy** particles upon registering the location target (lasts for a fraction of a second).

---

### 🎬 Stage 2: Feel Horn (Tactical Pacifist - 30s)
Implementing temporary passive state overrides for tactical retreat and threat de-escalation.
* **Feel Horn — Tactical Pacifist Override (30 Seconds)**:
  * Triggers a `passiveOverrideTicks` timer set to `600` ticks (30 seconds) on all currently following wolves of the owner blowing the horn.
  * **Behavior**:
    * Disables all aggressive targeting selectors (such as `NearestAttackableTargetGoal` and `AggressiveTargetGoal`).
    * Wolves immediately halt combat, clear their active attack target, and behave under Pacifist rules (fleeing at low health, emitting warning note particles on hostile approach, and applying the sentinel grace buffs to allies).
  * **Restrictions**:
    * This override affects **only** target selection and combat actions.
    * It strictly does **not** affect follow spacing (`FollowerSpacingCache`), wander boundaries (`TamedWanderNearOwnerGoal`), scale attributes (`Attributes.SCALE`), or native sound variants.
* **Particle Feedback**:
  * Wolves emit a soft, single-burst **green/teal dust particle ring** (`0x00FF88`) to signify peaceful compliance.

---

### 🎬 Stage 3: Sing Horn (Hold Command)
Implementing posture-based stationary controls.
* **Sing Horn — The "Hold" Command**:
  * Forces all currently following wolves of the owner to halt and sit down (`setOrderedToSit(true)`).
* **Particle Feedback**:
  * Wolves emit a tiny, single-burst **gold/yellow dust particle puff** (`0xFFD700`) as they settle in place.

---

### 🎬 Stage 4: Yearn Horn (Stand Up & Follow)
Implementing a local command to mass-mobilize sitting pets while leaving sentinel guards stationed.
* **Yearn Horn — The "Stand Up & Follow" Command**:
  * **Trigger**: Blowing the **Yearn Goat Horn** (`minecraft:yearn_goat_horn`).
  * **Target Selection**:
    * Scans a **32-block radius** around the player blowing the horn.
    * Targets wolves owned by the player (`wolf.isOwnedBy(player)`).
    * **State Gating**: Must be currently sitting (`wolf.isOrderedToSit()`) and must **not** be locked in Guard Mode (`!wolfExt.betterdogs$isGuardMode()`).
  * **Action**:
    * Forces all matched sitting dogs to stand up (`setOrderedToSit(false)`) and join the active following pack.
* **Particle Feedback**:
  * Wolves emit a brief, single-burst **green happy/heart** particle effect as they stand up to join the owner.

---

### 🎬 Stage 5: Tactical Aggressive Override & Emergency Recall
Implementing the temporary tactical attack state and fallback recovery.
* **Seek Horn — Tactical Aggressive Override (30 Seconds)**:
  * Triggers an `aggressiveOverrideTicks` timer set to `600` ticks (30 seconds) on all currently following wolves of the owner blowing the horn.
  * **Behavior**:
    * Temporarily enables the `AggressiveTargetGoal` for **all** wolves (including native Pacifists and Normals).
    * Wolves will actively scan for and attack hostile mobs in range, bypassing their normal personality-based constraints for the duration of the 30-second window.
  * **Particle Feedback**:
    * Wolves emit a brief, single-burst **red dust / flame particle trail** (`0xFF3333`) during their tactical combat window.
* **Call/Dream Horn — The "Emergency Recall"**:
  * Forces all tamed wolves of the owner in loaded chunks (regardless of sitting or guard status) to stand up and pathfind (or teleport if obstructed/further than 12 blocks) directly to the owner's feet.
  * **Particle Feedback**:
    * Wolves emit standard, single-burst **white teleportation / cloud** particles upon arrival.

---

## 🔧 Configuration (GameRules)
* `bd_horn_command_range` (Default: `64`): Maximum block distance for sound propagation.
* `bd_horn_pathing_timeout` (Default: `400`): Maximum ticks a wolf will try to reach a horn's sounded location before giving up.
* `bd_horn_override_duration` (Default: `600`): Tactical override duration in ticks (30 seconds).

---

## 🧪 QA & Test Plan

### Test Case 1: The Assemble Call
1. Spawn 5 wolves and tame them.
2. Order 2 wolves to sit, and leave 3 standing.
3. Walk 30 blocks away and blow a **Ponder Goat Horn**.
4. **Expected Result**: Only the 3 standing wolves pathfind to the exact block where you blew the horn. The sitting wolves remain seated.

### Test Case 2: Stand Up & Follow Command (Yearn Horn)
1. Set up 1 Aggressive wolf on **Guard Mode** (anchor set).
2. Order 2 other tamed wolves to **sit** manually.
3. Walk next to them and blow the **Yearn Goat Horn**.
4. **Expected Result**: The 2 manually sitting wolves stand up and follow you. The guarding Aggressive wolf remains seated at its station.

### Test Case 3: Tactical Pacifist Override (Feel Horn)
1. Have an Aggressive dog actively attacking a Zombie.
2. Blow the **Feel Goat Horn**.
3. **Expected Result**: The Aggressive dog instantly stops attacking, clears its target, and retreats/paces nearby. After 30 seconds, it resumes its aggressive nature.
