# Version Compatibility & Lifecycle Policy

*[[Home]] / Version Compatibility*

---

## 🗺️ Minecraft Version Compatibility Matrix

**Vanilla Outsider: Better Dogs** follows the **1 Jar 1 Version** architecture law. Dedicated binaries are compiled and tagged specifically per Minecraft release version:

| Minecraft Version | Mod Release Tag | DasikLibrary Bounds | JDK | Support Status | Target Guide |
| :--- | :--- | :--- | :---: | :---: | :--- |
| **MC 26.3** | `v5.0.16+26.3` | `>=1.8.8` | JDK 25 | 🟢 Active (Snapshot) | [[MC 26.3 Guide|Minecraft-26.3-Guide]] |
| **MC 26.2** | `v4.24.1+26.2` | `>=1.8.3` | JDK 25 | 🟢 Primary Target | [[MC 26.2 Guide|Minecraft-26.2-Guide]] |
| **MC 26.1.2** | `v4.24.1+26.1.2` | `>=1.8.2` | JDK 25 | 🟡 Maintenance | [[MC 26.1 Guide|Minecraft-26.1-Guide]] |
| **MC 1.21.11** | Legacy `1.x` Series | Legacy | Java 21 | 🔴 Deprecated | Legacy Archive Only |

---

## 📜 Version History & 26.x Migration

### Origins (MC 1.21.11)
**Better Dogs** originally launched during the Minecraft 1.21.x era. Initial builds established the core concept: giving vanilla wolves unique personality traits (Aggressive, Pacifist, Normal) and physical scale variation without introducing non-vanilla items or entity types.

### The 26.x Modernization Era
Following the transition to modern Minecraft 26.x:
1. **Zero-Dependency Guard**: `ModVersionGuard` was integrated via DasikLibrary to protect servers against classloader mismatch crashes.
2. **Modern Identifier Syntax**: All resource identifiers migrated from `Identifier.of()` to `Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", path)`.
3. **Open-Ended Dependency Bounds**: `fabric.mod.json` uses open-ended lower bounds (e.g. `"minecraft": ">=26.2-"`) to prevent strict launcher version locks while enforcing exact compatibility thresholds.

---

## ⏳ Version Support & Deprecation Lifecycle Policy

1. **Primary Target (MC 26.2)**: Receives all new gameplay features, AI optimizations, performance updates, and instant bug fixes.
2. **Snapshot Tracking (MC 26.3)**: Tracks upcoming Mojang snapshot API changes to ensure zero-day readiness upon major game updates.
3. **Maintenance Builds (MC 26.1.2)**: Receives critical bug fixes and security patches backported from the primary release line.
4. **Legacy Deprecation**: Pre-26.x releases (such as 1.21.11) are officially retired. Users on legacy versions are encouraged to upgrade to MC 26.2 for improved AI performance, non-blocking pathing safety, and bug fixes.

---

*Back to [[Home]]*
