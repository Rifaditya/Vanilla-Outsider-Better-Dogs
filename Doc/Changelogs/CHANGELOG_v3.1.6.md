# Changelog v3.1.6

## New Features

### 🏈 Play Fighting

- **Concept**: Large packs of aggressive wolves (>10) will now occasionally "play fight" to burn off energy.
- **Behavior**: Two wolves will chase and attack each other for 10 seconds.
- **Safety**: **100% Non-Lethal**. Health is hard-capped at 1 HP during these events.
- **Optimization**:
  - **Staggered Checks**: Checks occur only once per in-game minute per wolf (modulo based).
  - **Sampling**: Detection creates a sample of max 15 neighbors.
  - **Performance**: O(1) logic ensures zero lag even in massive wolf farms.

## Refactoring

- Renamed `CorrectionEvent` to `CorrectionDogEvent`.
- Renamed `RetaliationEvent` to `RetaliationDogEvent`.
- Renamed `WanderlustEvent` to `WanderlustDogEvent`.
