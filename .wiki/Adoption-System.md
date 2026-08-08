# Adoption System

*[[Home]] / [[Items & Interactions|Items-and-Interactions]] / Adoption System*

---

## 📄 Infobox: Adoption System Overview

| Parameter | Specification |
| :--- | :--- |
| **Activation Item** | Paper (`minecraft:paper`) |
| **Activation Gesture** | Shift + Right-Click tamed wolf with Paper |
| **Claiming Gesture** | Right-Click pending dog with empty hand |
| **Associated Advancement** | **Looking for a Home** (Task) |
| **Cancellation Triggers** | Shift+Right-Click paper again OR dog takes damage |

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

*Back to [[Home]] | View [[Items-and-Interactions]]*
