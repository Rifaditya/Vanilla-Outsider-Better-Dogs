# Core Logic: Better Dogs

## Genetics System

- Wolves have a hidden "Genome" determining their personality.
- **Personalities**:
  - **Aggressive**: High Damage, Low Health. Attacks monsters.
  - **Pacifist**: High Knockback, runs from danger.
  - **Normal**: Balanced.

## Breeding Algorithm

- **Same Parent (80%)**: Offspring inherits dominant personality.
- **Mutation (20%)**: Small chance to flip personality or dilute stats.

## Configuration

- Loaded via `BetterDogsConfig` (GSON).
- Hot-reload capable on some platforms (requires game restart currently).
