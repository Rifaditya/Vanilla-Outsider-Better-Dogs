# Items & Interaction Mechanics Guide

*[[Home]] / Items & Interactions*

---

## 🎒 Overview

**Vanilla Outsider: Better Dogs** expands vanilla item interactions to provide intuitive control over wolf companion behaviors without adding clutter items or breaking vanilla survival mechanics.

---

## 🦴 1. Guard Mode Toggle (`minecraft:bone`)

Holding a **Bone** and **Shift + Right-Clicking** an owned tamed wolf toggles Guard Mode at the wolf's current position:

* **Activation Text**: `"Guard Mode Activated for [Name] at [X, Y, Z]"`
* **Deactivation Text**: `"Guard Mode Deactivated for [Name]. Following owner."`
* **Behavior**:
  * **Aggressive Guards**: Patrol a **12-block radius** (`bd_guard_patrol_range_aggressive`) around post.
  * **Normal Guards**: Hold a stationary post (`bd_guard_patrol_range_normal`).
  * **Pacifist Guards**: Patrol a **3-block radius** (`bd_guard_patrol_range_pacifist`) while radiating **Regeneration** and **Resistance** status effects to nearby owners (`bd_pacifist_guard_buffs`).

---

## 📄 2. Adoption System (`minecraft:paper`)

Holding a piece of **Paper** and **Shift + Right-Clicking** an owned tamed wolf puts the dog up for adoption:

1. **Pending Status**: The wolf enters an adoption state (`" [Name] is now ready for adoption! Another player can right-click them to claim."`).
2. **Claiming**: Any other player can right-click the wolf to immediately assume ownership (`"You have adopted [Name]!"`).
3. **Cancellation**: Shift+Right-Clicking paper again cancels adoption (`"Adoption cancelled for [Name]."`). Adoption is also automatically cancelled if the dog takes damage (`"Adoption cancelled for [Name] because they took damage!"`).

---

## 🍏 3. Inbred Curing (`minecraft:golden_apple`)

Right-clicking an **Inbred Runt** wolf with a **Golden Apple** cures its genetic linebreeding penalties (`bd_enable_inbred_curing`):

* **Effect**: Instantly clears runt scale caps, removes rotten flesh particles (`bd_show_runt_particles`), and restores normal max health.
* **Notification**: `"[Name] has been cured of genetic inbreeding penalties!"`
* **Advancement**: Grants the **A Fresh Start** challenge advancement (+100 XP).

---

## 📯 4. Tactical Goat Horn Commands (`minecraft:goat_horn`)

Sounding a goat horn within **64 blocks** (`bd_horn_command_range`) issues tactical pack orders to all owned wolves:

| Goat Horn Type | Tactical Order | Effect & Duration |
| :--- | :--- | :--- |
| **Ponder Horn** | **Set Guard Posts** | Orders all nearby following wolves to enter Guard Mode at their current positions. |
| **Sing Horn** | **Tactical Override** | Commands Aggressive wolves to adopt Pacifist defensive rules for 30 seconds (`bd_horn_override_duration`). |
| **Seek / Call Horn** | **Recall Pack** | Instantly recalls distant following wolves back to the player's position. |

---

## 🥩 5. Ground Feeding & Favorite Treats

### Automatic Ground Feeding
Injured tamed dogs detect dropped food entities on the ground and pathfind to eat them automatically (`bd_dogs_eat_raw_food`, `bd_dogs_eat_cooked_food`):

* **Raw Meats & Rotten Flesh**: Restores health (`bd_dogs_eat_raw_food`).
* **Cooked Meats**: Restores health (`bd_dogs_eat_cooked_food`).
* **Refusal Trait**: Some dogs born tamed have a 30% chance (`bd_refuse_ground_food_chance`) to refuse ground food, preferring hand feeding.

### Favorite Treats (`bd_favorite_treats`)
Each wolf has a unique hidden favorite treat item deterministically seeded by its entity UUID:

* **Effect**: Feeding a wolf its favorite treat triggers an instant full heal, heart particles, and happy **Zoomies**!
* **Jade Integration**: Discovered favorite treats are revealed in the Jade/WTHIT tooltip overlay (`betterdogs.jade.treat`).

---

## 🚣 6. Vehicle Transport Boarding

1. **Selection**: **Shift + Right-Click** a tamed dog with an empty hand to select it (`"Selected [Name] for command. Shift+Right-click a vehicle to board."`).
2. **Boarding**: **Shift + Right-Click** a vehicle (Boat, Minecart, Horse) within reach to command the dog to board as a passenger (`"Commanded [Name] to board [Vehicle]."`).
3. **Dismounting**: Shift+Right-Clicking the dog again dismounts it (`"[Name] hopped out of the vehicle."`).

---

*Back to [[Home]]*
