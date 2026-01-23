# Better Dogs - Changelog

## v1.8.0-26.1 (2026-01-23) - Multi-Loader Release

### 🎉 New Features

- **NeoForge Support**: Better Dogs now supports both Fabric AND NeoForge loaders!
- **Multi-Loader Architecture**: Complete project restructure with common/fabric/neoforge modules

### 🔧 Technical Changes

- Migrated to multi-module Gradle structure:
  - `common/` - Shared logic, mixins, and assets
  - `fabric/` - Fabric loader entry points (Fabric Loom 1.14.7)
  - `neoforge/` - NeoForge loader entry points (NeoGradle moddev 2.0.140)
- Platform Services abstraction for loader-specific code
- NeoForge uses Optional-based NBT API (`getCompound().ifPresent()`)
- Fabric uses Attachment API for entity data persistence

### 📦 Build Information

| Loader | File | MC Version |
|--------|------|------------|
| Fabric | `vanilla-outsider-better-dogs-fabric-1.8.0-26.1.jar` | 26.1-snapshot-4 |
| NeoForge | `vanilla-outsider-better-dogs-neoforge-1.8.0-26.1.jar` | 26.1-snapshot-4 |

### 🗺️ Mappings

- Uses **official Mojang mappings** only (no Yarn, Parchment, or MCP)

---

## v1.7.6-26.1 (Previous)

- Last Fabric-only release
- See previous changelog entries
