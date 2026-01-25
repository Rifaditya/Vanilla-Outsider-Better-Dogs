# Better Dogs: Technical Manual (Vanilla Outsider)

**Version**: 3.1.4
**Philosophy**: "Vanilla Outsider" - Mechanics should be invisible, intuitive, and seamlessly integrated into the base game without custom GUIs.

---

## 1. Personality System

Every Wolf in the world is assigned a unique **Personality** upon spawning (natural) or taming. This is persistent data stored via the Fabric Attachment API.

### Personality Types

| Type | ID | Behavior |
| :--- | :--- | :--- |
| **Normal** | 0 | Standard Vanilla behavior. Attacks what owner attacks. Balanced stats. |
| **Aggressive** | 1 | **Guardian**. Proactively attacks hostile mobs near the owner. Higher Damage/Speed. |
| **Pacifist** | 2 | **Healer**. Will NOT attack when player attacks (unless player is hurt). Higher Health, lower Damage. |

---

## 2. The Scheduler System (Event-Driven AI)

As of v3.0, the mod uses a centralized **WolfScheduler** to manage complex behaviors. This replaces the old "Secondary Channel" system.

### Core Concept: "Social Mode"

Instead of "Secondary Targets", wolves now enter **Social Mode**.

- **Social Target**: The entity the wolf is interacting with (e.g., Owner, another Wolf).
- **Social Action**: The *type* of interaction (RETALIATION, DISCIPLINE, PLAY).
- **Timer**: How long the mode lasts (e.g., 60 ticks).

### The Gatekeeper

The `WolfMixin.setTarget()` method acts as a Gatekeeper.

- If **Social Mode** is Active: It BLOCKS all attempts to change the target (e.g., trying to attack a skeleton) UNLESS the new target matches the **Social Target**.
- This ensures that when a wolf is "busy" (correcting a baby), it cannot be distracted.

---

## 3. Advanced Behaviors (Events)

### A. Baby Retaliation (The "One-Bite" Rule)

*Requires Aggressive Personality*

- **Trigger**: Owner punches a baby wolf.
- **Event**: `RetaliationEvent` (ID: `betterdogs:retaliation`).
- **Logic**:
    1. Event injects **Social Mode: RETALIATION** (Duration: 5s).
    2. `BabyBiteBackGoal` sees this state and activates.
    3. Baby bites Owner **ONCE**, then the event ends (or goal forces stop).

### B. Adult Correction (The "Snitch" System)

*Requires Aggressive Personality*

- **Trigger**: A baby wolf successfully bites the owner (Retaliation).
- **Event**: `CorrectionEvent` (ID: `betterdogs:correction`).
- **Logic**:
    1. Baby "snitches" on itself by scanning for *one* nearby Aggressive Adult.
    2. Baby injects `CorrectionEvent` into that specific adult.
    3. Adult enters **Social Mode: DISCIPLINE**.
    4. Adult bites Baby **ONCE**.
    5. Baby gets "Dunce Cap" (Flag) so no other adults join in.

### C. Blood Feud (Vendetta)

*Global Mechanic*

- **Trigger**: 5% chance (Configurable) when Discipline occurs.
- **Logic**: Use **Main Channel** (Vanilla Target).
- **Result**: The Adult and Baby declare a permanent Vendetta (UUIDs stored).
- **Safety Bypass**: `WolfCombatHooks` explicitly allows friendly fire for Blood Feud participants.

---

## 4. Safety & Utilities

- **Cliff Awareness**: Wolves will stop chasing targets if it leads them off a dangerous cliff.
- **Creeper Flee**: Wolves will avoid Creepers unless they are detonating.
- **Wolf Gift**: Wolves bringing items when waking up.
- **Passive Healing**: Wolves heal slowly when out of combat.

---

*Documentation Generated via Antigenic Agent - v3.1.4*
