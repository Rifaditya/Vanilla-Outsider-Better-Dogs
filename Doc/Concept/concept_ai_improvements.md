# Concept: AI Dog Improvements & Refinements

This document outlines the design, technical specifications, and implementation pathways for refining and improving the existing AI systems in `Better Dogs`:
1. **Creeper Blast Evasion** (Refining Creeper Awareness)
2. **Cozy Storm Shelter & Comfort** (Refining Storm Anxiety)
3. **Flanking Raycast & Scale Adjustments** (Refining Pack Flanking Tactics)
4. **Nemesis Contagious Grudge Call to Arms** (Refining Nemesis Grudge System)

---

## Feature 1: Creeper Blast Evasion

### 1.1 Description
Enhances the current *Creeper Awareness* feature. Currently, dogs growl and back away from creepers. This improvement ensures that if a nearby creeper begins its swelling fuse countdown (`isSwelling()`), the dog instantly overrides its current goal to sprint radially away from the creeper at maximum speed (`1.5x`). It will actively avoid pathing directly in front of the player's line of sight to prevent blocking the player's escape route.

### 1.2 Implementation Hooks
- **Goal Registry**: In `Wolf.java` (`registerGoals`), register a high-priority `CreeperEvasionGoal` (priority level 1, just below panic/damage responses).
- **Target Selection**: Periodically scan for `Creeper` entities within a 10-block radius. If any found creeper satisfies `creeper.isSwelling()`, activate the goal.
- **Pathing Calculation**:
  ```java
  Vec3 avoidVec = DefaultRandomPos.getPosAway(this.wolf, 16, 7, creeper.position());
  if (avoidVec != null) {
      this.navigation.moveTo(avoidVec.x, avoidVec.y, avoidVec.z, 1.5D);
  }
  ```

### 1.3 Configuration
- **GameRule**: `betterdogs:bd_creeper_evasion_enabled` (Boolean, default: `true`).

### 1.4 Assets & Engine Visuals
- **Particles**: Spawns sprint smoke particles (`minecraft:smoke`) at the feet of the fleeing dog.

---

## Feature 2: Cozy Storm Shelter & Comfort Soothing

### 2.1 Description
Refines the *Storm Anxiety* system. Instead of dogs running to arbitrary shelter coordinates, they will prioritize shelter blocks that are close to the owner's coordinates (within 12 blocks). Furthermore, players can now soothe their anxious dog. Crouching and interacting (`mobInteract`) with an anxious dog (or feeding it their Favorite Treat) will comfort it, applying a `"Soothed"` status which suspends all storm anxiety behaviors for the next 10 minutes (half a Minecraft day).

### 2.2 Implementation Hooks
- **Shelter Logic**: Modify `StormAnxietyGoal` to select shelter targets where `world.canSeeSky(pos)` is false and `pos.closerThan(owner.blockPosition(), 12)`.
- **Comfort Trigger**: Mixin into `Wolf.java` (`mobInteract`):
  ```java
  if (this.isTamed() && this.level().isThundering()) {
      if (player.isSecondaryUseActive() || player.getItemInHand(hand).is(getFavoriteTreat())) {
          this.setSoothedTime(this.level().getGameTime());
          // Play heart particles and low-pitched comfort growl
      }
  }
  ```

### 2.3 Configuration
- **GameRule**: `betterdogs:bd_storm_anxiety_soothing` (Boolean, default: `true`).

### 2.4 Assets & Engine Visuals
- **Particles**: Displays heart particles (`minecraft:heart`) and note particles (`minecraft:note`) when comforted.
- **Sound**: Plays the vanilla wolf whimper sound at a relaxed pitch (e.g. `0.85f`).

---

## Feature 3: Flanking Raycast & Target Scale Adjustments

### 3.1 Description
Implements safety boundaries and dynamic distance scaling for the *Pack Flanking Tactics*. Flanking paths will now perform raycast checks to verify that the target flank coordinates are accessible (not blocked by walls or deep water), preventing dogs from running into dead ends or getting stuck. Flanking radius will scale dynamically based on the bounding box width of the targeted entity.

### 3.2 Implementation Hooks
- **Raycast Check**: In `WolfFlankingGoal.java`, before setting the navigation path, perform a raycast/line-of-sight check to verify that the target block is walkable.
- **Scaling Formula**: Scale flanking distance offset dynamically:
  ```java
  double targetWidth = target.getBbWidth();
  double flankRadius = Math.max(3.0, targetWidth * 2.5);
  ```

### 3.3 Configuration
- **GameRule**: `betterdogs:bd_flanking_raycast_check` (Boolean, default: `true`).

### 3.4 Assets & Engine Visuals
- Uses standard combat movement and visual cues.

---

## Feature 4: Nemesis Contagious Grudge (Call to Arms)

### 4.1 Description
Refines the *Nemesis Grudge System*. When a dog spots and attacks its nemesis mob type, it will coordinate with nearby owned dogs. The dog will emit a howling warning cry, contagiously spreading the "Nemesis Grudge" to all owned dogs within a 16-block radius, focusing the entire pack's target selection on the offending nemesis type.

### 4.2 Implementation Hooks
- **Howl Event**: In `WolfTargetMixin.java` or target selection logic, when a wolf targets its nemesis:
  ```java
  this.level().playSound(null, this.blockPosition(), SoundEvents.ENTITY_WOLF_HOWL, SoundSource.NEUTRAL, 1.0F, 1.1F);
  List<Wolf> pack = this.level().getEntitiesOfClass(Wolf.class, this.getBoundingBox().inflate(16), 
      other -> other.getOwner() == this.getOwner() && other != this);
  for (Wolf member : pack) {
      // Alert packmates to set target or acquire matching temporary target preference
  }
  ```

### 4.3 Configuration
- **GameRule**: `betterdogs:bd_nemesis_call_to_arms` (Boolean, default: `true`).

### 4.4 Assets & Engine Visuals
- **Particles**: Spawns angry villager particles (`minecraft:angry_villager`) in a ring around the howling dog.
- **Sound**: Plays `minecraft:entity.wolf.howl` sound effect.

---

## QA & Verification Plan

### Test Cases
1. **Creeper Fuse Test**: Spawn a creeper near a dog, ignite the creeper's fuse. Verify the dog sprint-flees radially away.
2. **Anxiety comfort Test**: Start a thunder storm. Wait for a dog to whine. Sneak-click the dog. Verify the dog stops whining, hearts display, and it remains calm for the rest of the storm.
3. **Flanking Path Test**: Engage a target near a cliff/wall. Check if dogs avoid pathing into solid blocks and maintain appropriate radius according to target size.
4. **Call to Arms Howl Test**: Attack a nemesis mob type. Verify the attacking dog howls, nearby dogs alert, and they focus target on the nemesis.
