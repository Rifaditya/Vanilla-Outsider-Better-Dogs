# Concept: Dynamic Follow Range

## Summary

Tamed wolves' `FOLLOW_RANGE` attribute should scale based on personality and pack spread multiplier, enabling wolves to actually chase and attack mobs the player shoots at range.

## Problem

Vanilla wolves have a hardcoded `FOLLOW_RANGE` of 16 blocks. When the player shoots a mob at 30+ blocks, the wolf receives the target via `OwnerHurtTargetGoal` but immediately drops it on the next tick because `canContinueToUse()` checks `FOLLOW_RANGE` and the target is too far. The wolf never even starts walking.

This is worsened by the pack spread system — wolves positioned 20 blocks away from the player have even less effective range to reach a distant target.

## Solution

Set the wolf's `Attributes.FOLLOW_RANGE` dynamically based on:

**Formula**: `FOLLOW_RANGE = personalityBase + spacingOffset`

### Personality Base Values

| Personality | Base Follow Range |
|---|---|
| Aggressive | 32 blocks |
| Normal | 24 blocks |
| Pacifist | 16 blocks (vanilla) |

### Spread Multiplier Scaling

The `spacingOffset` reuses the existing `FollowerSpacingCache` value, so as the pack grows the aggro chase range expands to match the wolf's expanded wander position.

| Pack Size | Spacing Offset | Aggressive Range | Normal Range | Pacifist Range |
|---|---|---|---|---|
| 1 | 0.0 | 32 | 24 | 16 |
| 10 | 3.6 | 35.6 | 27.6 | 19.6 |
| 30 | 6.0 (capped) | 38 | 30 | 22 |

### Implementation Notes

- Update `Attributes.FOLLOW_RANGE` in `PersonalityFollowOwnerGoal` when the spacing offset recalculates (already throttled to ~20-40 ticks).
- No mixin needed — just a vanilla attribute modification.
- Pacifist wolves still won't offensively chase (blocked by `OwnerHurtTargetGoalMixin`) but the range is available for defensive reactions via `OwnerHurtByTargetGoal`.

## Status

Logged — not yet planned or implemented.
