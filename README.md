# 🐕 Better Dogs 1.21.11: Developer Guide

Technical repository for the Minecraft 1.21.11 port of the Better Dogs AI overhaul.

## 🛠️ Porting Architecture

This version maintains feature parity with the 26.1 development branch while adapting to the 1.21.11 ecosystem.

### Key Technical Differences

- **Language Stack**: Kotlin-first implementation.
- **Config Management**: Integrated with **Cloth Config** and **Mod Menu**. No native Game Rule overrides.
- **Persistence**: Employs **Fabric Data Attachment API** V1 for modern NBT-less state storage.

## 🧬 Feature Set

- Modular AI Personalities (Normal, Aggressive, Pacifist)
- Genetic Inheritance Matrices
- Event-Driven Social Behaviors (Mischief & Correction)
- Environmental Response (Storm Anxiety, Cliff Safety)
- Dog Feeding (Ground Scavenging) with Cloth Config toggles.

## 🔗 Documentation

- [Parity Walkthrough](Doc/doc.md)
- [Kotlin Extension Logic](src/main/kotlin/net/vanillaoutsider/betterdogs/WolfPersistence.kt)

## 📜 Legal

- **License**: GNU GPLv3
- **Collection**: Vanilla Outsider

### ☕ Support the Development

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)

---
*Maintained by DasikIgaijin.*
