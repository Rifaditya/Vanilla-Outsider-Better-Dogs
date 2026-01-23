# Core Logic: Better Dogs

## Genetics System

- Wolves have a hidden "Genome" determining their personality.
- **Personalities**:
  - **Aggressive**: High Damage, Low Health. Attacks monsters. Will intervene in domestic disputes (3-strike sequence).
  - **Pacifist**: Low Damage, High Health, High Knockback. Runs from danger.
  - **Normal**: Balanced.

## Domestic Aggression & Intervention

- **Baby Retaliation**: If a baby wolf is hit by its owner, it will retaliate with exactly **two attacks**.
- **Untrained Nature**: Tamed baby wolves have **2x follow radius** and **2x teleport distance** by default (configurable in `betterdogs.json`).
- **Baby Curiosity**: Non-aggressive baby wolves are highly curious. They will wander to investigate "inert objects" (flowers, grass, trees) and stare at nearby entities (passive or hostile) for several seconds before moving on.
- **Reckless Aggression**: Aggressive baby wolves are more proactive than adults. They will immediately engage any hostile mob they detect, even if the owner is not nearby (adults typically stay near the owner).
- **Protection**: Tamed baby wolves cannot be targeted by other wolves (wild or tamed).
- **Aggressive Intervention**:
  - If a baby wolf attacks its owner, any nearby **Aggressive** adult wolf belonging to that owner will intervene to protect the owner.
  - Intervening wolves follow a disciplined **3-strike sequence**, after which they cease their aggressive action.
  - This intervention can be lethal to the offending baby wolf.

## Breeding Algorithm

- **Same Parent (80%)**: Offspring inherits dominant personality.
- **Mutation (20%)**: Small chance to flip personality or dilute stats.

## Configuration

- Loaded via `BetterDogsConfig` (GSON).
- Hot-reload capable on some platforms (requires game restart currently).
