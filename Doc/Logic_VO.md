# Core Logic: Better Dogs (v3.0)

## Personality System (Persistent Data)

- Wolves have attached persistent data determining their personality.
- **Personalities**:
  - **Aggressive**: High Damage, Low Health. Attacks monsters. Functions as the "Enforcer" for domestic disputes.
  - **Pacifist**: Low Damage, High Health, High Knockback. Runs from danger. Functions as the "Healer".
  - **Normal**: Balanced.

## Social Interaction Architecture

### 1. Baby Retaliation (The Provocation)

- **Concept**: Tamed wolves are not robots; they have boundaries.
- **Logic**: If an owner hurts a baby wolf (Aggressive personality), the baby enters `RETALIATION` mode.
- **Action**: The baby bites the owner **EXACTLY ONCE**.
- **Optimization**: This is an Event (`RetaliationEvent`) that overrides all other AI.

### 2. Adult Correction (The Intervention)

- **Concept**: The Pack Hierarchy.
- **Logic**: If a baby retaliates against the owner, it "Snitches" on itself by broadcasting a `CorrectionEvent` to one nearby Aggressive Adult.
- **Action**: The Adult intervenes and disciplies the baby with **EXACTLY ONE BITE**.
- **Result**: The baby receives a "Dunce Cap" (Social Stigma) preventing other adults from jumping in. This ensures a clean 1v1 interaction.

### 3. Blood Feud (The Chaos)

- **Concept**: Sometimes, discipline goes too far.
- **Chance**: 5% Global Chance (Configurable).
- **Effect**: The Adult and Baby declare a permanent Vendetta (UUID match). They will fight to the death.

### 4. Play Fighting (Social Enrichment) (v3.1.6)

- **Concept**: Wolves need to burn off energy to reduce chaos.
- **Trigger**: Occurs in large packs (>10 dogs) where >70% are Aggressive.
- **Action**: Two aggressive wolves pair up and "Play Fight" for 10 seconds.
- **Safety**: Damage is strictly capped at 1 HP. Friendly fire is enabled but non-lethal.
- **Optimization**: Staggered checks (Once/Minute) and Sampled Radius (O(1)).

## Breeding Algorithm

- **Same Parent (80%)**: Offspring inherits dominant personality.
- **Mutation (20%)**: Small chance to flip personality or dilute stats.

## Configuration

- Loaded via `BetterDogsConfig` + Cloth Config API.
- Supports Mod Menu integration for in-game editing.
