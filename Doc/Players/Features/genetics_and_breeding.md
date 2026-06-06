# Genetics & Breeding Guide

This mod introduces a deep, genetics-based breeding and inheritance system for wolves. Every wolf possesses unique genetic markers that dictate their attributes, sizes, and social compatibility.

---

## 1. Individual DNA & Stat Rolls
Every wolf spawned in the world gets assigned a unique genetic signature derived from their UUID. This profile determines:
*   **Base Attribute Allocations**: Base maximum health, attack damage, and movement speed.
*   **Social Preference Rolls**: Determines their likelihood to initiate or participate in pack behaviors, zoomies, and play fights.

---

## 2. Dynamic Size Scaling
A wolf's physical scale is directly correlated with their maximum health attribute bonus. 
*   **Scale Formula**: `scale = 1.0 + (healthBonus * 0.012)`
*   **Range**: Scale dynamically varies from **0.808x** (worst-case runt) to **1.312x** (perfectly healthy max-health sentinel).

---

## 3. Genetic Inheritance & Mutation
When two wolves breed, their offspring inherit traits from both parents:
*   **Inheritance Calculation**: The puppy's base attributes are calculated as a weighted average of the parents' stats.
*   **Triangular Mutation**: A minor random mutation factor is applied using a triangular probability curve, meaning most puppies stay near parent averages, but rare instances may yield significantly stronger or weaker stats.

---

## 4. Litter System
Instead of always yielding a single puppy, mating wolves can now produce multiple offspring (Litters) in a single breed event. Each puppy in the litter receives independent attribute rolls and trait inheritance.

---

## 5. Inbreeding & Runts
To promote healthy breeding practices and prevent genetic stagnation, breeding closely related wolves (parent-child or siblings) results in **Inbreeding Depression**.

### The Runt Penalty
Inbred puppies are born as **Runts** and suffer the following permanent penalties:
*   **Severe Stat Reduction**: Drastically reduced health, movement speed, and attack power.
*   **Miniature Stature**: Forced to a miniature visual scale (0.808x).
*   **Slowness & Weakness**: Noticeably sluggish movement and combat performance.

### Curing Inbreeding
An inbred runt can be restored to health:
1.  **Golden Apple Treatment**: Feed a Golden Apple to the runt.
2.  **Recovery**: This clears the NBT inbreeding status, restoring the wolf's full health potential, standard attribute calculations, and normal visual scaling.
3.  **Outcrossing**: Alternatively, breeding an inbred wolf with a completely unrelated partner recovers genetic health in the next generation.

---

## 6. Mate Selection Prioritization
When wolves enter breeding mode (Love Mode), they use a smart partner selection algorithm to avoid accidental inbreeding:
*   **Priority 1: Unrelated Partners**: The wolf scans the surrounding area (8 blocks radial inflation) and prioritizes mating with the nearest unrelated wolf.
*   **Priority 2: Fallback Partner (Incest)**: If there are absolutely **no** unrelated wolves in love mode within range, the wolf will fall back to mating with a related partner. This prevents the breeding process from softlocking or failing when no other options exist.
