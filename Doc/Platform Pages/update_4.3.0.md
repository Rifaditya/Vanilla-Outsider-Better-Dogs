# 🐕 Better Dogs - Version 4.3.0 (The Paper Adoption Update)

Welcome to the **Paper Adoption Update**! Version 4.3.0 introduces a long-requested, vanilla-friendly way to transfer ownership of your tamed wolves to other players, complete with beautiful custom particle effects and built-in security features.

---

## 📄 Wolf Adoption & Ownership Transfer

You can now pass ownership of your beloved companions to friends! This system uses standard, vanilla-only **Paper** and requires no complicated commands.

### 1. Putting a Dog Up for Adoption
* **How to Trigger**: As the owner, **Shift + Right-Click** your tamed wolf while holding a sheet of **Paper** in your main hand.
* **The State**: 
  * The wolf will sit down, clear its aggressive targets, and halt all pathfinding.
  * Exactly **1 sheet of Paper** is consumed.
  * A page turn sound plays, and the owner gets an overlay notification: *"Buddy is now ready for adoption! Another player can right-click them to claim."*
* **Visual indicator**: The wolf will emit a beautiful, sparkling trail of **Rose Pink** particles floating upward from its body to indicate it is ready to be claimed.

### 2. Claiming the Wolf
* **How to Claim**: Any other player (non-owner) can walk up to the adoptable wolf and **Right-Click** it with an **empty main hand**.
* **The Transfer**:
  * The wolf's ownership is instantly transferred to the new player.
  * The wolf stands up and plays a happy ambient sound.
  * Standard heart taming particles explode around the wolf.
  * Both players receive overlay notifications confirming the adoption:
    * New Owner: *"You have adopted Buddy!"*
    * Old Owner (if online): *"Buddy has been adopted by Player2!"*

### 3. Safety & Cancellation Checks
To prevent accidental transfers or griefing, the adoption state is cancelled automatically under the following conditions:
* **Manual Cancel**: If the owner interacts normally (without paper) with their adoptable wolf, the state is cancelled, and the wolf returns to normal behavior.
* **Damage Interrupt**: If the adoptable wolf takes any damage, the adoption is cancelled immediately to protect the dog, and the owner is warned via an overlay message.

---

## 📡 Vanilla Client Compatibility

Like the rest of the mod, the **Paper Adoption** system is **100% server-side optional**! 
* Vanilla players can join your server or P2P multiplayer world and fully participate in adopting and claiming dogs without needing to install the mod on their clients.
* The custom Rose Pink particle trails are made using vanilla trail particles, so they render perfectly on unmodded clients.

---

## 📜 Full Changelog

* **Paper Adoption Mechanics**: added Paper-based ownership transfer system for tamed wolves.
* **Trail Particles**: configured custom Rose Pink (`0xFF99BB`) trail particles for the pending adoption state.
* **Security Checks**: implemented automatic cancellation on damage or manual owner interaction.
* **Compatibility**: optimized for vanilla clients and dedicated servers using high-performance AI scheduling.
