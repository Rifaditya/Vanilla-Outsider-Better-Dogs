# 🐕 Better Dogs - Version 3.11.1 (The Pack Tactics & Gifts Update)

Welcome to the **Pack Tactics & Gifts Update**! Version 3.11.1 backports advanced, intelligent hunting behavior for wolf packs and rewards players for nurturing strong bonds with their companions, bringing this branch into full parity with the 26.2 release.

---

## 🐺 True Pack Hunting (Flanking Tactics)
Wolves no longer charge mindlessly in a single-file line toward their target!
* **Dynamic Encirclement**: When hunting as a pack, followers will intelligently spread out, flank the target from the sides, and encircle them.
* **Combat Advantage**: This minimizes the chance of multiple wolves being hit by a single sweeping attack or splash potion, drastically increasing their combat effectiveness against crowds!
* **Configurable**: Fully controlled via the new `bd_pack_flanking_tactics` GameRule.

---

## 🎁 Morning Gifts & Interactions
Your dogs now express gratitude for your love and care!
* **Merit System**: Positive interactions (feeding, ordering to sit, or calming down an angry dog) build up hidden "merits."
* **Morning Surprises**: Once enough merits are accumulated, your dog has a chance to wake you up with a random gift in its mouth after you sleep in a bed!
* **Accidents Forgiven**: By default, accidentally hitting your dog removes its merits. You can change this using `bd_demerit_accidental_attacks` so that only sneaking while hitting them resets their progress.
* **GameRules**: Thresholds and cooldowns are fully customizable via `bd_gift_feed_threshold` and `bd_gift_interaction_cooldown`.

---

## 🛠️ Sanitary Refactors & Bug Fixes
* **Memory & Footprint**: Reduced `WolfMixin` size significantly by extracting heavy ambient sound ticking logic into an isolated `WolfTickHelper` class.
* **API Compliance**: Aligned with modern Fabric standards by removing legacy references and cleaning up NBT data schemas.

---

*As always, this update is 100% server-side compatible! Vanilla clients can join and experience the new flanking tactics and gifting mechanics without needing to install anything.*
