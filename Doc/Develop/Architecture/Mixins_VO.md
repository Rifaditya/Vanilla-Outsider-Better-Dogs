# Mixin Documentation: Better Dogs (v3.1.21)

## Target: `net.minecraft.world.entity.animal.wolf.Wolf`

### Injections

1. **Attributes**:
   - Modifies Speed, Health, and Damage based on Personality (Aggressive/Pacifist/Normal).
   - Injected into `applyTamingSideEffects` to guarantee stat randomization upon ownership.

2. **AI Goal Management**:
   - **The Gatekeeper Pattern**: Injects into `targetSelector` and `goalSelector` to prioritize Social Mode tasks (Correction/Play) over generic combat.
   - **Environment Awareness**: Injects pathfinding logic to avoid Hazard blocks (Lava/Fire) and manage cliff safety.

3. **Breeding Genetics**:
   - **Target**: `targetSelector` / `getBreedOffspring`
   - **WolfBreedingMixin**: Implements the inheritance matrix, calculating baby personality from parent genetics stored in Data Attachments.

## AI Goal Mixins

### Target: `net.minecraft.world.entity.LivingEntity`

- **WolfMobMixin**: Redirects `setTarget` to handle domestic retaliation and intervention logic without polluting the base `Wolf` class directly.

### Target: `net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal`

- **OwnerHurtTargetGoalMixin**: Inhibits protection instinct for Pacifist wolves and ensures aggressive babies don't trigger "owner defense" loops.

---

## persistence (Data Attachments)

- **Official Mappings**: Targeting **Minecraft 26.x (Post-Obfuscation)**.
- **Attachments**: Uses the Fabric Attachment API to store social state, personality IDs, and grudge lists. Replaces legacy NBT hooks for all new mod features.
