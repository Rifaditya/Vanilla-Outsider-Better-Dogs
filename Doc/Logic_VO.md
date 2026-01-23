# Core Logic: Better Dogs

## Genetics System

- Wolves have a hidden "Genome" determining their personality.
- **Personalities**:
  - **Aggressive**: High Damage, Low Health. Attacks monsters.
  - **Pacifist**: High Knockback, runs from danger.
  - **Normal**: Balanced.

## Baby Wolf Behavior

- **Passive by Default**: Baby wolves will not participate in combat (attacking owner's targets or defending owner).
- **Exceptions**:
  - **Aggressive Personality**: Baby wolves with the Aggressive personality will still attack monsters.
  - **Self Defense**: Any baby wolf will retaliate if hit by a mob.

## Breeding Algorithm

- **Same Parent (80%)**: Offspring inherits dominant personality.
- **Mutation (20%)**: Small chance to flip personality or dilute stats.

## Configuration

- Loaded via `BetterDogsConfig` (GSON).
- Hot-reload capable on some platforms (requires game restart currently).
