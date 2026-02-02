# Core Logic: Better Dogs (v3.1.21)

This document explains the internal logic governing wolf personalities, their impact on gameplay, and the advanced social interaction architecture.

## 1. Personality Selection Logic

Personalities are assigned once per wolf life cycle, typically during taming or spawning.

### Roll Mechanism

- **Individual DNA**: A unique seed derived from the wolf's UUID is used to roll for event participation thresholds.
- **Taming**: Assigned upon successful taming.
- **Breeding**: `WolfBreedingMixin` calculates the offspring personality based on parent genetic data and configurable Game Rule chances.

## 2. Stat Influence & Initial Behaviors

Personalities apply attribute modifiers and determine AI priorities.

| Personality | Health Mod | Speed Mod | Damage Mod | Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **Aggressive** | -10.0 | +0.15 | +0.50 | **The Guardian**. Proactive. |
| **Pacifist** | +20.0 | -0.10 | -0.30 | **The Loyal**. Reactive. |
| **Normal** | +0.0 | +0.0 | +0.0 | **The Classic**. Balanced. |

## 3. Persistent Data (Fabric Attachment API)

To ensure consistency, the mod uses the **Fabric Data Attachment API**.

- `personalityId`: The permanent trait.
- `grudgeTarget`: UUID of entities for Blood Feuds.
- `lastMischiefDay`: Rate-limiting for puppy play.

## 4. Social Interaction Architecture

The mod uses a centralized **WolfScheduler** to manage event-driven behaviors.

### A. The "Snitch" System

If a puppy hits its owner (`BabyBiteBackGoal`), it triggers a `CorrectionDogEvent`. One nearby Aggressive Adult with the correct DNA threshold will intervene via `AdultCorrectionGoal`.

### B. Genetic Inheritance

Breeding roles are now governed by native Game Rules (e.g., `bd_breed_same_chance`). The matrix allows for dominance, recession, and mutation.

### C. Large-Scale Events

- **Zoomies**: Morning/Rain energy bursts.
- **Group Howl**: Night-time atmospheric pack behavior.
- **Play Fight**: Non-lethal training for large packs.

## 5. UI Integration

All logic variables are now exposed through a custom Game Rule category. This allows per-world tuning without restarting the server or manual JSON edits.
