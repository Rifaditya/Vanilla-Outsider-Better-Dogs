# 🐕 Better Dogs: Technical Overview

Better Dogs is a comprehensive Minecraft wolf AI overhaul, focused on adding depth, personality, and smarter behaviors through a high-performance event-driven architecture.

## 🏗️ Project Architecture

The core of the mod is split into two primary environments:

### 1. Better Dogs 26.1 (Modern Java)

- **Target**: Minecraft 26.1 Snapshot 4+
- **Language**: Java 25
- **Configuration**: Native Minecraft Game Rules (`BetterDogsGameRules.java`)
- **Key Systems**:
  - **Registry Integration**: Uses native `GameRuleRegistry` to group settings under a "Better Dogs" category.
  - **Mixin Hooks**: Extensive use of Sponge Powered Mixins to inject behaviors into vanilla classes (`Wolf`, `AgeableMob`, etc.).
  - **Data Components/Attachments**: Modern persistence using Minecraft's newest data APIs.

### 2. Better Dogs 1.21.11 (Kotlin Parity)

- **Target**: Minecraft 1.21.11
- **Language**: Kotlin 2.3.0
- **Configuration**: Cloth Config + Mod Menu (`BetterDogsConfig.kt`)
- **Key Systems**:
  - **Fabric Data Attachments**: Cross-session persistence for wolf states (Grudges, Submissiveness).
  - **Kotlin Extensions**: Idiomatic Kotlin property extensions for vanilla entities.

---

## 🧠 Core Systems

### Event-Driven Personality AI

Wolves are assigned one of three `WolfPersonality` types (Normal, Aggressive, Pacifist). Behaviors are managed by a modular event system that allows long-running social interactions without polling every tick.

### Genetics Engine

Implemented in `WolfBreedingMixin`, the genetics engine calculates offspring's personality traits based on parent profiles using configurable probability matrices.

### The "Snitch" System (Social AI)

A complex interaction between `BabyBiteBackGoal` and `AdultCorrectionGoal`. Highlights the mod's ability to coordinate multiple entities through shared persistent states.

---

## 🛠️ Development & Tooling

- **Build System**: Gradle 9.3.0
- **Loom**: 1.15 (Native Mojang Mappings)
- **CI/CD**: Semantic versioning and automated release JAR preservation.

## 🔗 Internal Documentation

Complete technical deep-dives can be found in the `Doc/` directory:

- [Logic Overview](Doc/Codebase%20Documentations/Logic_VO.md)
- [Audit Helper for Moderators](Doc/Codebase%20Documentations/Audit_Helper_For_Moderators.md)
- [Project Summary](Doc/doc.md)

---

## 👤 Credits & Support

- **Creator**: DasikIgaijin (Part of the Vanilla Outsider Collection)
- **License**: GNU GPLv3
- **Platform Links**: [Modrinth](https://modrinth.com/mod/vanilla-outsider-better-dogs) | [Issue Tracker](https://github.com/Rifaditya/Vanilla-Outsider-Better-Dogs/issues)

### ☕ Support the Development

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)

*Made with ❤️ for the Minecraft community.*
