# Project Architecture: Vanilla Outsider - Better Dogs (v3.1.13)

## Philosophy

**"Immersion via Systems (Config Mastery)."**
Better Dogs enhances wolf behavior through hidden complexity. We use the **WolfScheduler** and **Data Components** to create behaviors that feel organic and unpredictable.

## Structure

- **Core**: Single-module Fabric project (No Common/Loader split).
- **Engine**:
  - `scheduler/`: The heart of the new AI. Handles time-based events.
  - `ai/`: Custom Goals (tasks) that entities execute.
  - `mixin/`: Injections into Vanilla code (WolfMixin, TamableAnimalMixin).

## Key Components

- **WolfScheduler**: The central brain. Manages "Events" (like Wandering, Retaliation, Correction) and injections.
- **WolfExtensions**: Interface injected into `Wolf` to give it memory, social state, and personality.
- **WolfEventRegistry**: Stores all valid behavioral events.
- **WolfPersistentData**: Uses Fabric Attachment API to save personality traits to NBT.
- **BetterDogsConfig**: Cloth Config integration for user settings.
