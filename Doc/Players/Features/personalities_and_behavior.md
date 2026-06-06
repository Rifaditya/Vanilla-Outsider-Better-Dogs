# Wolf Personalities & Behaviors

Every wolf in the world is born with one of three distinct personality profiles: **Normal**, **Aggressive**, or **Pacifist**. This profile permanently governs their combat engagement, guard behaviors, pathfinding decisions, and emotional responses.

---

## 1. Personality Profiles

### Normal (Balanced)
The baseline wolf. Balanced, dependable, and cautious when necessary.
*   **Combat Style**: Attacks targets designated by their owner, defending themselves and their pack.
*   **Flee Threshold**: Has a **50% chance** to run away and seek safety if their health drops below **30%**.
*   **Storm Anxiety**: Shows a moderate level of fear during severe weather.

### Aggressive (Scouts & Guardians)
Fierce, alert, and pack-oriented. They make excellent forward scouts but require careful supervision.
*   **Combat Style**: Highly proactive. They aggressively pursue threats and protect their owner from farther distances.
*   **Flee Threshold**: Extremely resilient. Only has a **10% chance** to flee when health falls below **30%** (they will almost always fight to the death).
*   **Storm Anxiety**: **Immune**. Aggressive wolves are completely unfazed by thunder and lightning.

### Pacifist (Silent Alarms & Sentinels)
Gentle, hyper-aware, and emotionally sensitive. While they do not participate in direct physical combat, they provide invaluable defensive utility.
*   **Combat Style**: Non-combatant. They will not attack hostile entities. Instead, they act as an early warning detection system.
*   **Flee Threshold**: **100% chance** to immediately flee from danger and return to their owner if their health drops below **30%**.
*   **Storm Anxiety**: Highly sensitive. Has a **3x higher risk** than Normal wolves to suffer panic and distress during thunder.

---

## 2. Storm Anxiety
During active thunderstorms, sensitive wolves (Normal and Pacifist) can experience anxiety:
*   **Symptoms**: The wolf will start whimpering, shaking visibly, and seeking shelter or staying close to their owner.
*   **Mechanic**: Aggressive wolves are entirely immune to this effect. Pacifist wolves are the most susceptible.

---

## 3. Tactical Fleeing
Wolves are no longer suicidal in combat. When their health drops below **30%**, they will evaluate whether to retreat based on their personality flee threshold (10% for Aggressive, 50% for Normal, 100% for Pacifist).
*   **Behavior**: When fleeing, they run away from the active threat, ignore combat commands, and attempt to navigate back to their owner or seek shelter until healed.
