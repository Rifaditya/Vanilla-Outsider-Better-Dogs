# Project Architecture: Vanilla Outsider - Better Dogs (Fabric Edition)

## Philosophy

**"Immersion via Systems."**
Better Dogs enhances wolf behavior through hidden complexity. We use unseen AI systems (Schedulers, Events, Personalities) to create behaviors that feel organic and unpredictable, rather than scripted.

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
