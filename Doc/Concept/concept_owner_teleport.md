# Concept: Owner Teleport Synchronization

This document outlines the design and implementation specifications for the **Owner Teleport Synchronization** feature under the Vanilla Outsider (VO) philosophy. It ensures that active following wolves automatically teleport with their owner when the owner teleports over long distances or across dimensions.

---

## 🏛️ Core Design Philosophy (Vanilla Outsider)
* **Friction Reduction**: Solves the classic vanilla problem where wolves are left behind in unloaded chunks when a player teleports, eliminating the need to write custom commands to retrieve them.
* **Respect Posture & Tasks**: Only standing, active following wolves are teleported. Wolves that are ordered to sit, leashed, or in Guard Mode remain behind to guard their designated posts.
* **Server-Side Sovereignty**: Teleport synchronization is handled entirely server-side, ensuring full compatibility with vanilla clients.

---

## 📅 Implementation Stages

### 🎬 Stage 1: Distance & Commands Interception
This stage handles teleporting wolves when the owner teleports to a distant location in the same dimension (e.g., using `/tp`, waypoints, or home commands).
* **Teleport Event Hook**:
  * Hooks into player teleportation calls (e.g., Mixin on `ServerPlayer.teleportTo` or intercepting movement packets).
  * Evaluates if the distance between the player's old position and new position exceeds a threshold (e.g., 32 blocks).
* **Follower Search & Teleportation**:
  * Scans the old area (loaded chunks around the player's previous coordinates) for tamed wolves owned by the player.
  * Filters for wolves that:
    * Are owned by the player.
    * Are not manually sitting (`!wolf.isOrderedToSit()`).
    * Are not leashed (`!wolf.isLeashed()`).
    * Are not in Guard Mode (`!wolfExt.betterdogs$isGuardMode()`).
  * Automatically teleports the matching wolves to safe, valid ground coordinates around the player's new destination position.

---

### 🎬 Stage 2: Cross-Dimension Travel
This stage handles teleporting wolves when the player changes dimensions (e.g., using Netther/End portals or cross-dimensional commands).
* **Dimension Change Hook**:
  * Intercepts `ServerPlayer.changeDimension` or teleportations where `destinationLevel != oldLevel`.
* **Cross-Dimension Extraction**:
  * Before the player leaves the origin dimension, finds all eligible following wolves in the player's vicinity.
  * Schedules a deferred task to transition the wolves across levels to the destination level.
  * Places the wolves safely around the portal or spawn location in the new dimension.

---

## ⚙️ Configuration (GameRules & Config)

Exposed via dynamic GameRules:

| GameRule Key | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `vanilla-outsider-better-dogs:bd_sync_owner_teleport` | Boolean | `true` | When true, standing following wolves teleport alongside their owner. |

---

## 🧪 QA & Verification

### Manual Test Cases
1. **Command Teleport Test**:
   * Have 5 active following wolves.
   * Run `/tp @s ~ ~1000 ~` or teleport to another coordinate.
   * **Expected Result**: All 5 wolves teleport to the ground near you.
2. **Sitting / Guard Test**:
   * Order 1 wolf to sit, place 1 wolf in Guard Mode, and leave 3 wolves following you.
   * Teleport far away.
   * **Expected Result**: Only the 3 following wolves teleport to you. The sitting and guard wolves stay at their respective spots.
3. **Cross-Dimension Portal Test**:
   * Step into a Nether Portal with active following wolves.
   * **Expected Result**: Wolves appear in the Nether next to you.
