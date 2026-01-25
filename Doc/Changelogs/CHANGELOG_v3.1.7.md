# Changelog v3.1.7

## [v3.1.7] - 2026-01-25

### ✨ New Features

- **Individual DNA System**:
  - Implemented a unique "Characteristic Roll" for every dog derived from its UUID.
  - This roll is combined with their Personality Threshold to determine if they participate in social events.
  - Result: Each dog feels like a unique individual with its own likes/dislikes.

- **Zoomies Event**:
  - **Behavior**: Hyperactive random running for 5-8 seconds.
  - **Triggers**:
    - Morning (0-2000 ticks): 10% chance.
    - Wet (Rain/Water): 20% chance.
  - **Participation**:
    - Babies: 100% (Always love to play).
    - Normal: 50%.
    - Pacifist: 20%.
    - Aggressive: 0% (Too cool for this).

- **Group Howl Event**:
  - **Behavior**: A Leader starts howling, and nearby pack members sit and join in.
  - **Triggers**:
    - Full Moon: 100% chance.
    - Regular Night: 5% chance.
  - **Participation**:
    - Aggressive: 100% (Love to assert dominance).
    - Normal: 80%.
    - Pacifist: 40%.
