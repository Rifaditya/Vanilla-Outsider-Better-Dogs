# Mixin Documentation: Better Dogs

## Target: `net.minecraft.world.entity.animal.wolf.Wolf`

### Injections

1. **Attributes**:
   - Modifies Speed, Health, and Damage based on Personality (Aggressive/Pacificst/Normal).
   - Uses `Identifier` for Attribute Modifier IDs.

2. **AI Goals**:
   - Injects custom goals for "Storm Anxiety" and "Cliff Safety".
   - Modifies target selectors for Aggressive wolves.

## AI Goal Mixins

### Target: `net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal`

- **`OwnerHurtTargetGoalMixin`**: Inhibits goal for Pacifist and non-aggressive Baby wolves.

### Target: `net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal`

- **`OwnerHurtByTargetGoalMixin`**: Inhibits goal for non-aggressive Baby wolves.

## Persistence

- Hooks into `addAdditionalSaveData` and `readAdditionalSaveData` via `WolfPersistentData` attachment.
