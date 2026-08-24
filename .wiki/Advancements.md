# Advancements & Achievements Guide

*[[Home]] / Advancements*

---

## 🏆 Husbandry Advancement Tree

**Vanilla Outsider: Better Dogs** adds a dedicated set of 13 custom Husbandry advancements integrated directly into Minecraft's native advancement screen under the Husbandry tab:

```
[Husbandry Root]
  └── On Guard!
        ├── A Pack of Guardians
        └── On Patrol
  └── A Pack of Personalities
  └── Keep it in the family
        ├── Fresh Blood
        └── A Fresh Start
  └── Double Trouble
        ├── Triple Threat
        │     └── Puppy Rain
        └── Litter Legend
  └── Looking for a Home
  └── Self-Service
```

---

## 📜 Full Advancement Reference Table

| Advancement Title | Frame | Display Icon | Requirements & Trigger Criteria | Parent Advancement |
| :--- | :---: | :---: | :--- | :--- |
| **On Guard!** | Task | `minecraft:bone` | Place a tamed wolf into Guard Mode for the first time using a Bone while sneaking (`betterdogs:guard_wolf_personality`). | Husbandry Root |
| **A Pack of Guardians** | Goal | `minecraft:iron_sword` | Place a wolf of **each personality type** (Normal, Aggressive, and Pacifist) into Guard Mode using a Bone while sneaking. | On Guard! |
| **On Patrol** | Goal | `minecraft:shield` | Have your guarding Aggressive dog defeat a hostile monster within its 12-block patrol radius (`betterdogs:on_patrol`). | On Guard! |
| **A Pack of Personalities** | Goal | `minecraft:wolf_spawn_egg` | Tame at least one wolf of **each personality type**: Normal, Aggressive, and Pacifist (`betterdogs:tame_wolf_personality`). | Husbandry Root |
| **Keep it in the family** | Task | `minecraft:rotten_flesh` | Linebreed two closely related tamed wolves (siblings or parent/child) and produce an Inbred Runt (`betterdogs:inbred_wolf`). | Husbandry Root |
| **Fresh Blood** | Goal | `minecraft:bone` | Breed an Inbred Runt parent with an unrelated wild or imported wolf to produce a healthy puppy and recover the lineage (`betterdogs:outcross_runt`). | Keep it in the family |
| **A Fresh Start** | Challenge | `minecraft:golden_apple` | Cure an Inbred Runt by right-clicking it with a Golden Apple (`betterdogs:cure_inbred`). Reward: 100 XP. | Keep it in the family |
| **Double Trouble** | Task | `minecraft:wolf_spawn_egg` | Breed two tamed wolves and yield a litter of **2 puppies** in a single breeding session (`betterdogs:wolf_litter`). | Husbandry Root |
| **Triple Threat** | Goal | `minecraft:wolf_spawn_egg` | Breed two tamed wolves and yield a litter of **3 puppies** in a single breeding session (`betterdogs:wolf_litter`). | Double Trouble |
| **Puppy Rain** | Challenge | `minecraft:wolf_spawn_egg` | Breed two tamed wolves and yield a maximum litter of **4 puppies** (`betterdogs:wolf_litter`). Reward: 100 XP. | Triple Threat |
| **Litter Legend** | Challenge | `minecraft:experience_bottle` | Experience all litter sizes: get a litter of 2, 3, and 4 puppies at least once. Reward: 250 XP. | Double Trouble |
| **Looking for a Home** | Task | `minecraft:paper` | Place one of your tamed dogs up for adoption using a piece of Paper while sneaking (`betterdogs:put_up_for_adoption`). | Husbandry Root |
| **Self-Service** | Task | `minecraft:cooked_beef` | Let an injured tamed dog heal itself by automatically consuming meat dropped on the ground (`betterdogs:self_service`). | Husbandry Root |

---

## 🛠️ Trigger Types & Technical Criteria

Mod developers and pack creators can reference custom criteria triggers in custom datapacks:

* `betterdogs:tame_wolf_personality` — Triggers when a player tames a wolf. Checks personality enum (`NORMAL`, `AGGRESSIVE`, `PACIFIST`).
* `betterdogs:guard_wolf_personality` — Triggers when a wolf enters Guard Mode.
* `betterdogs:inbred_wolf` — Triggers when breeding yields an inbred runt.
* `betterdogs:outcross_runt` — Triggers when an inbred runt breeds with an unrelated wolf.
* `betterdogs:cure_inbred` — Triggers upon curing an inbred wolf with a Golden Apple.
* `betterdogs:wolf_litter` — Triggers upon puppy spawn, passing litter count parameter.
* `betterdogs:put_up_for_adoption` — Triggers when paper adoption state is activated on a dog.
* `betterdogs:on_patrol` — Triggers when an Aggressive guard dog slays a mob inside its patrol zone.
* `betterdogs:self_service` — Triggers when a dog heals by eating ground food.

---

*Back to [[Home]]*
