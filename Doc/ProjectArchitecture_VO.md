# Project Architecture: Vanilla Outsider - Better Dogs

## Philosophy

**"One Click, One Action."**
Better Dogs enhances the wolf mechanics to be more immersive and genetically complex without adding GUIs or breaking the vanilla feel.

## Structure

- **Common**: Core logic, Mixins, Config, and Persistent Data.
- **Fabric**: Fabric Loader entrypoint and dependencies.
- **NeoForge**: NeoForge Loader entrypoint and dependencies.

## Key Components

- **WolfMixin**: Injects AI and Attribute logic into `Wolf` entity.
- **WolfPersistentData**: Handles NBT data retention for genes/personalities.
- **BetterDogsConfig**: JSON-based configuration loader (No external libs).
