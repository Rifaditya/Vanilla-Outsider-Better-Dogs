# Mixin Documentation: Better Dogs (v3.1.13)

## Target: `net.minecraft.world.entity.animal.wolf.Wolf`

### Injections

1. **Attributes**:
   - Modifies Speed, Health, and Damage based on Personality (Aggressive/Pacifist/Normal).
   - Injected into `applyTamingSideEffects` to guarantee stat randomization upon ownership.

2. **AI Goal Management**:
   - **The Gatekeeper Pattern**: Injects into `targetSelector` and `goalSelector` to prioritize Social Mode tasks (Correction/Play) over generic combat.
   - **Environment Awareness**: Injects pathfinding logic to avoid Hazard blocks (Lava/Fire) and manage cliff safety.

## AI Goal Mixins

### Target: `net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal`

- **`OwnerHurtTargetGoalMixin`**: Inhibits protection instinct for Pacifist wolves and ensures aggressive babies don't trigger "owner defense" loops.

### Target: `net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal`

- **`OwnerHurtByTargetGoalMixin`**: Prevents untamed "Ghost Aggression" and ensures personality-scaled defense ranges.

## Persistence (Data Components)

- **Official Mappings**: Targeting **Minecraft 26.x (Post-Obfuscation)**.
- **Attachments**: Uses the Fabric Attachment API to store social state and personality IDs. replaces legacy NBT `read/writeAdditionalSaveData` hooks for all new mod features.
