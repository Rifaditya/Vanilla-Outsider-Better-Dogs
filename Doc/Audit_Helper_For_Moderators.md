# Moderator Audit Helper: Better Dogs

**Mod Name:** Vanilla Outsider: Better Dogs
**Mod ID:** `vanilla-outsider-better-dogs` (Fabric)
**Version:** 3.1.13
**Creator:** DasikIgaijin

## 🛡️ Safety & Compliance Statement

To assist Platform Moderators (Modrinth/CurseForge) in auditing this project, I certify the following:

1. **No External Network Connections**: This mod runs entirely offline within the Minecraft game loop. It does **NOT** make any HTTP/Web requests.
2. **No Data Collection**: No telemetry, analytics, or user-data tracking.
3. **No Binary Execution**: No OS-level commands or external binary execution.
4. **Filesystem Hygiene**: Only writes to `config/betterdogs.json` (via **Ultraguard Sync**) and uses native Fabric Attachments for world data.

## 📂 Codebase Overview for Reviewers

| Feature | Source File | Description |
| :--- | :--- | :--- |
| **Data Persistence** | `WolfPersistentData` | Records personality and social state via **Fabric Attachment API** (Modern Data Components replacement). |
| **Core Logic** | `mixin.WolfMixin` | Handles randomization, stat buffs, and social state transition hooks. |
| **Scheduler** | `ai.WolfScheduler` | Centralized O(1) sampling to trigger social behaviors without per-tick entity scans. |
| **Persistence Safety** | `config.BetterDogsConfig` | Implements **Ultraguard Sync** with atomic writes and `.bak` auto-redundancy. |

## 🔍 "Behavioral Injection" Explained

Reviewers auditing the Mixins may notice entities "injecting" states into other entities (e.g., Baby to Adult).

- **The "Snitch" System**: Instead of Adult Wolves constantly scanning (High CPU), a Baby Wolf broadcasts a `CorrectionEvent` when it bites the owner. This "wakes up" exactly one adult enforcer.
- **Gatekeeper Logic**: `WolfMixin.setTarget()` redirects vanilla AI while `isSocialModeActive()` is true, ensuring social interactions (discipline/play) are not interrupted by generic mob combat.

## 🔍 Technical Justifications (Post-Obfuscation Era)

- **Official Mappings**: This mod targets the **Minecraft 26.x Post-Obfuscation Era**. All method calls use Official Mojang names.
- **Friendly Fire Bypass**: `WolfCombatHooks` explicitly authorizes specific attacks (Retaliation/Correction) that vanilla hardcodes to block. These are strictly capped at 1 hit per interaction and gated by UUID/Ownership checks.
- **Persistence Hygiene**: I use `AttachmentType` (Fabric) to store data. This is the modern, state-safe replacement for legacy NBT handling, ensuring data survives version jumps and prevents "Ghost Data" corruption.

## 🛠️ Build & Dependencies

- **Loader**: Fabric Loader (>=0.16.10)
- **Mappings**: Official Minecraft Mappings (Mojang/Unobfuscated)
- **External Libs**: None (Uses Fabric API natively).
