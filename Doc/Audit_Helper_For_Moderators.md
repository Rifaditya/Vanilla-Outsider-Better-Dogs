# Moderator Audit Helper: Better Dogs

**Mod Name:** Vanilla Outsider: Better Dogs
**Mod ID:** `vanilla-outsider-better-dogs` (Fabric)
**Version:** 4.5.13
**Creator:** DasikIgaijin

## 🛡️ Safety & Compliance Statement

To assist Platform Moderators (Modrinth/CurseForge) in auditing this project, I certify the following:

1. **No External Network Connections**: This mod runs entirely offline within the Minecraft game loop. It does **NOT** make any HTTP/Web requests.
2. **No Data Collection**: No telemetry, analytics, or user-data tracking.
3. **No Binary Execution**: No OS-level commands or external binary execution.
4. **Filesystem Hygiene**: Only writes to standard config directory `config/vanilla-outsider-better-dogs.json` (delegated via **DasikLibrary ConfigHelper**) and uses native Fabric Attachments for world data.

## 📂 Codebase Overview for Reviewers

| Feature | Source File | Description |
| :--- | :--- | :--- |
| **Data Persistence** | `WolfPersistentData` | Records personality, parentage markers, and social state via **Fabric Attachment API**. |
| **Core Logic** | `mixin.WolfMixin` | Handles taming adjustments, dynamic size scaling, and combat hooks. |
| **Scheduler** | `mixin.WolfSocialMixin` | Ticks events (Zoomies, Howls) and registers to Dasik Social registry. |
| **Optimized AI** | `ai.PersonalityFollowOwnerGoal` / `ai.WildWolfTerritorialGoal` | Follow and pack interaction goals utilizing **FollowerSpacingCache** and staggered throttles. |

## 🔍 Advanced Logic & Safety

- **Line-of-Sight Filtering**: Normal and Aggressive guarding wolves strictly require line-of-sight to target hostiles, preventing them from targeting cave monsters through solid walls.
- **Inbreeding Logic**: Sibling or parent-child pairings are verified by matching UUID parent markers. Applies attribute stunting and size reduction to runts.
- **Optimized Raycasting**: Pacifist sentinel warning scans check vertical distance (`dy <= 4.0`) first. If they are close, the goal bypasses the expensive `hasLineOfSight` raycast check, preserving server TPS.

## 🛠️ Build & Dependencies

- **Loader**: Fabric Loader (>=0.16.10)
- **Toolchain**: JDK 25, Gradle 9.3+
- **External Libs**: `dasik-library` (Required, >= 1.7.4). Optionally supports `cloth-config` and `modmenu` for configuration GUI.
