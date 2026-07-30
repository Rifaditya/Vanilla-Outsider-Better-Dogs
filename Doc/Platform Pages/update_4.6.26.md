# 🐕 Better Dogs - Version 4.6.26 (The Genetics & Breeding Polish Update)

Welcome to the **Genetics & Breeding Polish Update**! Version 4.6.26 improves the selective breeding process, integrates modern genetics library tools, and delivers critical runtime optimizations to ensure peak performance for your canine packs.

---

## 🧬 Unrelated Mate Prioritization
No more accidental inbreeding! When breeding tamed wolves in a pack:
* **Smart Partner Selection**: Wolves will actively search for and prioritize mating with completely unrelated wolves within their breeding range.
* **Fallback Safety**: If no unrelated mates are available, they will fallback to breeding with related members to ensure you can still breed them if desired.
* **Inbreeding Risk Avoidance**: This helps players naturally scale up large packs without accidentally introducing the genetic "Inbred Runt" trait and health debuffs.

---

## 📈 Centralized Genetics & Thin Architecture
All genetics and breeding attributes are now managed more efficiently:
* **Library Integration**: Migrated genetics calculations, inbreeding checks, and outcross recovery mechanisms to **DasikLibrary v1.8.1**.
* **Modpack Safety**: Required dependency constraints in `fabric.mod.json` now enforce `dasik-library >=1.8.0` to guarantee crash-safe startups.
* **Deterministic Size Scaling**: Added a seed-based UUID calculation to ensure size and rendering scales are determined dynamically and deterministically, preventing visual overrides.

---

## ⚡ Heavy Loop & GC Performance Polish
Enhanced server stability and ticked entities:
* **Allocation Zeroing**: Replaced heavy Java Stream API calls and closures with direct loop structures inside hot AI tasks like `EatGroundFoodGoal` and `PersonalityFollowOwnerGoal`, resolving heap garbage spikes.
* **Hazard Avoidance**: Switched to cached `BlockPos.MutableBlockPos` instances in `AvoidHazardsGoal` to completely eliminate coordinate allocations in entity ticks.
* **Clean Event Handlers**: Extracted player right-click actions and bone/paper mechanics into a modular `WolfInteractionHelper` class.

---

*This update is fully server-side optional! Vanilla clients can connect to servers running version 4.6.26 without needing to install the mod.*
