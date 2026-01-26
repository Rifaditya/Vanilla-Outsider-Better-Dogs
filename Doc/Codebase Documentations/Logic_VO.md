# Core Logic: Better Dogs (v3.1.13)

This document explains the internal logic governing wolf personalities, their impact on gameplay, and the advanced social interaction architecture.

## 1. Personality Selection Logic

Personalities are assigned once per wolf life cycle, typically during the `finalizeSpawn` event (natural spawning) or when a wolf is tamed.

### Roll Mechanism

- **Natural Spawn**: A random personality is chosen from the `WolfPersonality` enum based on configurable chances (`BetterDogsConfig`).
- **Taming**: If a wolf hasn't had one assigned, it is rolled upon successful taming.
- **Breeding**: Offspring inherit personalities based on dominant/recessive genetic rules defined in the config.

## 2. Stat Influence & Initial Behaviors

Personalities apply attribute modifiers and determine basic AI priorities.

| Personality | Health Mod | Speed Mod | Attack Mod | Special Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **Aggressive** | -10.0 (Def) | +0.15 | +0.50 | **The Guardian**. Proactively attacks hostiles. |
| **Pacifist** | +20.0 (Def) | -0.10 | -0.30 | **The Healer**. Defensive/Pacifist until provoked. |
| **Normal** | +0.0 | +0.0 | +0.0 | **The Classic**. Balanced vanilla-plus behavior. |

## 3. Persistent Data (Fabric Attachment API)

To ensure that a wolf keeps its personality after a server restart or chunk unload, the mod uses the **Fabric Data Attachment API**.

### Data Record: `WolfPersistentData`

- `personalityId`: The String ID of the enum (e.g., "AGGRESSIVE").
- `lastDamageTime`: Tracks healing-over-time cooldowns.
- `socialMode`: Tracks current active events (Scheduler).

## 4. Social Interaction Architecture (v3.0+)

The mod uses a centralized **WolfScheduler** to manage complex, event-driven behaviors.

### A. Baby Retaliation (The Provocation)

- **Concept**: If an owner hurts an Aggressive baby wolf, it enters `RETALIATION` mode.
- **Action**: The baby bites the owner **EXACTLY ONCE** via the `BabyBiteBackGoal`.
- **Optimization**: Uses `RetaliationEvent` to override standard AI for 5 seconds.

### B. Adult Correction (The "Snitch" System)

- **Concept**: Hierarchy enforcement.
- **Logic**: If a baby bites the owner, it broadcasts a `CorrectionEvent` to one nearby Aggressive Adult.
- **Action**: The Adult intervenes with **ONE BITE**.
- **Result**: The baby receives a "Dunce Cap" (Social Stigma/Flag) to prevent ganging up.

### C. Blood Feud (Vendetta)

- **Chance**: 5% (Configurable) when Discipline occurs.
- **Effect**: The Adult and Baby declare a permanent Vendetta (UUID match) and fight to the death.

### D. Play Fighting (Social Enrichment)

- **Trigger**: Occurs in large packs (>10 dogs) with high aggression density.
- **Action**: Two wolves pair up for 10s of safe, non-lethal sparring (capped at 1 HP damage).

## 5. Environment & Utility Logic

- **Zoomies**: Morning/Post-rain hyper-activity (Aggressive wolves are too "stoic" for this).
- **Group Howl**: Night-time pack vocalization led by one leader.

- **Personality-Scaled Teleportation**: Aggressive dogs wander further; Pacifists stick closer to the owner.
