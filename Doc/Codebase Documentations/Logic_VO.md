# Logic Documentation: Better Dogs (Vanilla Outsider)

This document explains the internal logic governing wolf personalities and their impact on gameplay.

## 1. Personality Selection Logic

Personalities are assigned once per wolf life cycle, typically during the `finalizeSpawn` event (natural spawning) or when a wolf is tamed.

### Roll Mechanism

- **Natural Spawn**: A random personality is chosen from the `WolfPersonality` enum using a uniform distribution.
- **Taming**: If a wolf somehow lost its personality or hasn't had one assigned, it is rolled upon successful taming.

## 2. Stat Influence

Personalities apply attribute modifiers to the wolf entity. These are applied via `WolfMixin.betterdogs$applyPersonalityStats`.

| Personality | Health Mod | Speed Mod | Attack Mod | Special Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **Aggressive** | +5.0 | +0.05 | +2.0 | Targets hostile mobs from a greater distance. |
| **Pacifist** | +10.0 | -0.02 | -1.0 | Higher health, but less likely to initiate combat. |
| **Lazy** | -4.0 | -0.1 | +0.0 | Slower, prefers sitting. |
| **Protective** | +0.0 | +0.0 | +1.0 | Stays closer to the owner. |

## 3. Persistent Data (Fabric Attachment API)

To ensure that a wolf keeps its personality after a server restart or chunk unload, the mod uses the **Fabric Data Attachment API**.

### Data Record: `WolfPersistentData`

- `personalityId`: The String ID of the enum (e.g., "AGGRESSIVE").
- `lastDamageTime`: Used to track when the wolf was last hurt for "healing over time" logic.

### Technical Flow

1. **Spawn**: `WolfMixin` rolls personality -> Saved to Attachment.
2. **Save**: Fabric API automatically serializes the Attachment to the entity NBT.
3. **Load**: Fabric API automatically de-serializes the Attachment from NBT.
4. **Logic**: Mixin reads the Attachment to decide which AI goals to inject or attributes to modify.
