# Better Dogs: Technical Manual (Vanilla Outsider)

**Version**: 3.1.13
**Philosophy**: "Vanilla Outsider" - Mechanics should be invisible, intuitive, and seamlessly integrated into the base game. **Config Mastery** ensures players can tune every "magic number" to their liking.

---

## 1. Personality System

Every Wolf in the world is assigned a unique **Personality** upon spawning (natural), taming, or breeding. This is persistent data stored via the Fabric Attachment API.

### Personality Types

| Type | ID | Behavior |
| :--- | :--- | :--- |
| **Normal** | 0 | Standard Vanilla-plus behavior. Balanced stats. |
| **Aggressive** | 1 | **Guardian**. Proactively attacks hostile mobs near the owner. High Damage. |
| **Pacifist** | 2 | **Healer**. defensive until provoked. High Health/Knockback. |

---

## 2. The Scheduler System (Event-Driven AI)

As of v3.0, the mod uses a centralized **WolfScheduler** to manage complex behaviors.

### Core Concept: "Social Mode"

Instead of "Secondary Targets", wolves now enter **Social Mode**.

- **Social Target**: The entity the wolf is interacting with (e.g., Owner, another Wolf).
- **Social Action**: The *type* of interaction (RETALIATION, DISCIPLINE, PLAY, ZOOMIES, HOWL).
- **Timer**: How long the mode lasts (Oversight by Scheduler).

### The Gatekeeper

The `WolfMixin.setTarget()` method acts as a Gatekeeper.

- If **Social Mode** is Active: It BLOCKS all attempts to change the target (e.g., trying to attack a skeleton) UNLESS the new target matches the **Social Target**.
- This ensures that when a wolf is "busy" (e.g. correcting a baby or howling), it cannot be distracted.

---

## 3. Advanced Behaviors (Events)

### A. Baby Retaliation (The "One-Bite" Rule)

*Requires Aggressive Personality*

- **Trigger**: Owner punches a baby wolf.
- **Logic**: Event injects **Social Mode: RETALIATION**. Baby bites Owner **ONCE**, then event ends.

### B. Adult Correction (The "Snitch" System)

*Requires Aggressive Personality*

- **Trigger**: A baby wolf successfully bites the owner (Retaliation).
- **Logic**: Baby broadcast a `CorrectionEvent` to one nearby Aggressive Adult. Adult enters **Social Mode: DISCIPLINE** and bites baby **ONCE**. Baby gets "Dunce Cap" flag to prevent ganging up.

### C. Social Events (v3.1.x)

- **Play Fighting**: Large packs (>10) engage in safe, non-lethal sparing. Capped at 1 HP damage.
- **Zoomies**: Morning/Post-rain hyper-activity.
- **Group Howl**: Night-time pack vocalization led by one leader.

---

## 4. Safety & Persistence

- **Cliff Awareness**: Detects airborne targets and stops chasing if ground is missing within 4 blocks below.
- **Creeper Flee**: Actively flees hissing creepers.
- **Ultraguard Sync**: Config persistence using atomic writes and auto-backups (`.json.bak`).
- **Passive Healing**: Slow health recovery when out of combat.

---

*Documentation Updated for v3.1.13*
