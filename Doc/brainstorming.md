# Brainstorming: New Dog Personalities

This document explores potential new personalities for the **Better Dogs** mod. The goal is to create unique, "Vanilla+" behaviors that distinguish wolves from each other beyond just damage or HP stats.

---

## 💡 Concept 1: The Guardian (The Bodyguard)

*Focus: Defensive Utility & Loyalty*

* **Behavior**: Stays within a much tighter radius of the player (3-5 blocks).
* **Trait**: Has high Knockback Resistance.
* **Unique Goal**: **Physical Buffer.** If the player is being targeted by a projectile (arrow/fireball), the Guardian tries to move into the path to "intercept" it.
* **Stats**: High HP, Slow Speed.

## 💡 Concept 2: The Forager (The Treasure Hunter)

*Focus: Instant Gratification & Exploration*

* **Behavior**: Occasionally wanders away from the player (within 15 blocks) to sniff the ground.
* **Unique Goal**: **Sniff Out Treasure.** Every few minutes, it can "dig up" a small item depending on the biome (Bones in Deserts, Berries in Taiga, Sticks/Apples in Forests).
* **Trait**: Occasionally brings an item to the player and drops it at their feet (like a gift).
* **Stats**: Normal stats.

## 💡 Concept 3: The Sentry (The Alarm System)

*Focus: Awareness & Scouting*

* **Behavior**: Lowers its head and growls when a hostile mob is within 24 blocks (even through walls).
* **Unique Goal**: **Early Warning.** Barks once loudly when it first detects a monster that the player hasn't seen yet.
* **Trait**: Increased detection range for AI goals.
* **Stats**: High Speed, Low HP.

## 💡 Concept 4: The Coward (The Distractor)

*Focus: Chaos & Unique Combat*

* **Behavior**: Runs *away* from combat, often hiding behind the player.
* **Unique Goal**: **Accidental Lure.** While fleeing, it leaves a "scent trail" (particles) that actually draws mob aggro for a few seconds, acting as a temporary decoy.
* **Trait**: Insane movement speed when "Scared".
* **Stats**: Very Low Damage, Maximum Speed.

## 💡 Concept 5: The Hyper (The Energizer)

*Focus: Animation & Personality*

* **Behavior**: Constantly moving, jumping over blocks, and spinning in circles when idle.
* **Unique Goal**: **Playful Pounce.** Occasionally pounces on passive mobs (Rabbits/Chickens) just to scare them, without actually doing damage (unless untamed).
* **Trait**: Does not take fall damage.
* **Stats**: High Speed, High Jump Strength.

---

## 🛠 Tech Considerations

* **Mixin Hooks**: Most of these can be implemented via new `Goal` classes injected in `WolfMixin`.
* **Persistence**: Added to the `WolfPersonality` enum and saved via Attachment API.
* **Config**: Each personality needs its own section in `betterdogs.json` for tweaking weights and chance values.
