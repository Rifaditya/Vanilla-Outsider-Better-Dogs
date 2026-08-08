# Ground Feeding & Favorite Treats

*[[Home]] / [[Items & Interactions|Items-and-Interactions]] / Ground Feeding*

---

## 🥩 Infobox: Ground Feeding & Treats Summary

| Parameter | Specification |
| :--- | :--- |
| **Ground Food Eating** | Automatic raw/cooked meat eating (`bd_dogs_eat_raw_food`, `bd_dogs_eat_cooked_food`) |
| **Ground Food Refusal Trait** | 30% chance born tamed dog refuses ground food (`bd_refuse_ground_food_chance`) |
| **Favorite Treat Seeding** | Deterministically seeded per wolf UUID (`bd_favorite_treats`) |
| **Gift Feed Threshold** | **10 positive interactions** (`bd_gift_feed_threshold`) |
| **Gift Interaction Cooldown** | **100 ticks** (5 seconds) (`bd_gift_interaction_cooldown`) |

---

## 🍖 1. Ground Food Pathing & Self-Healing

Injured tamed dogs automatically scan for dropped raw or cooked meat items on the ground:

* **Healing**: Eating ground meat restores health and triggers heart particles (`self_service` advancement).
* **Refusal Trait**: Tamed-from-birth dogs with ground food refusal trait ignore dropped ground meat, requiring direct hand feeding.

---

## 🎁 2. Favorite Treats & Morning Gifts

### Favorite Treats (`bd_favorite_treats`)
Each wolf has a unique hidden favorite treat item:
* **Effect**: Hand-feeding a wolf its favorite treat triggers an instant full heal, heart particles, and happy **Zoomies**!

### Morning Gifts
Wolves with high positive interaction counts (`bd_gift_feed_threshold`) bring morning gifts upon sunrise:
* **Aggressive Gifts**: Bones (40%), Rotten Flesh (35%), Arrows (15%).
* **Pacifist Gifts**: Sweet Berries (30%), Seeds (25%), Flowers (20%), Mushrooms (15%).

---

*Back to [[Home]] | View [[Items-and-Interactions]]*
