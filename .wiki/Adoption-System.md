# Adoption System

*[[Home]] / Adoption System*

---

## 📄 Infobox: Adoption System Overview

| Parameter | Specification |
| :--- | :--- |
| **Activation Item** | Paper (`minecraft:paper`) |
| **Activation Gesture** | Shift + Right-Click tamed wolf with Paper |
| **Claiming Gesture** | Right-Click pending dog with empty hand |
| **Associated Advancement** | **Looking for a Home** (Task) |
| **Cancellation Triggers** | Shift+Right-Click paper again OR dog takes damage |
| **Personality Pre-Assignment** | Auto-assigns random personality (`WolfPersonality.random`) if dog lacks one |

---

## 🔄 1. Adoption Lifecycle Workflow

```
       [Owner: Holds Paper] ──► Shift + Right-Click Dog
                                        │
                                        ▼
                           [Pending Adoption State]
                                        │
         ┌──────────────────────────────┼──────────────────────────────┐
         ▼                              ▼                              ▼
 [Other Player Right-Clicks]   [Owner Shift+Right-Clicks]     [Dog Takes Damage]
         │                              │                              │
         ▼                              ▼                              ▼
 [Ownership Transferred]       [Adoption Cancelled]           [Adoption Cancelled]
 "You have adopted [Name]!"    "Adoption cancelled for..."    "Adoption cancelled because..."
```

---

## 📜 2. Chat Notifications & Security

* **Pending Activation**: Broadcasts `"[Name] is now ready for adoption! Another player can right-click them to claim."`
* **Claim Transfer**: Updates entity owner UUID to the new player, clearing old sitting/guarding states.
* **Damage Protection**: If an adoption-pending dog takes environmental or combat damage, adoption state automatically cancels to prevent griefing during transfers.

---

## 🛡️ 3. Personality Safeguards & Stability (v4.24.4+ / v5.0.19+)

When adopting pre-existing vanilla dogs or dogs imported from older worlds:
* **Automatic Personality Assignment**: If the dog does not have an initialized personality NBT tag when claimed, the system automatically assigns a random personality (`WolfPersonality.random`) prior to calculating owner stats.
* **Stat Modifier Fallback**: `WolfStatManager` enforces a null guard (`WolfPersonality.NORMAL` fallback), guaranteeing that adoption claims never throw null pointer exceptions regardless of entity origin.

---

*Back to [[Home]] | View [[Goat Horns & Tactical Commands|Goat-Horns-and-Tactical-Commands]]*
