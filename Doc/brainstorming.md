# Brainstorming: Character Events (Moods)

Character Events are temporary behavioral states that give wolves individual personality and agency without breaking the "Vanilla Rule" (no forced state changes).

## 🧬 The DNA System (Individual Acceptance)

Every wolf has a unique, persistent **DNA** value derived from its UUID. This ensures that behavioral traits are deterministic and "born-in," rather than purely random.

* **Acceptance Probability**: When an event (like Fetch) triggers, the `WolfScheduler` calculates an **Acceptance Score** based on the wolf's DNA and the Event ID.
* **Thresholds**: Each event has a participation threshold (adjustable via Game Rules). A dog whose score is above the threshold will "reject" the mood.
* **Individual Consistency**: A dog that is "lazy" will consistently reject high-energy moods like Zoomies or Wanderlust.

## ⚖️ The "Vanilla Rule"

To maintain player control and immersion, these events **MUST NOT**:

* Force a sitting dog to stand.
* Force a standing dog to sit.
* Interrupt critical behaviors (like being hurt or commanded).

---

## 🎭 Proposed Character Events (Moods)

### 1. Wanderlust (Refined)

* **Mood**: The dog wants to explore.
* **Behavior**: Increases wander range and speed. The dog sniffs more frequently.
* **Constraint**: If sitting, the dog stays put but "sniffs" the air/ground more often (visual only).

### 2. Begging

* **Mood**: The dog wants treats.
* **Trigger**: Owner is holding food or eating nearby.
* **Behavior**:
  * **If Standing**: Follows owner very closely, performs "Head Tilts" and whines.
  * **If Sitting**: Remains sitting but looks up at owner with puppy eyes and head tilts.
* **Benefit**: Purely immersion and flavor.

### 3. Idle Curiosity (Sniffing)

* **Mood**: The dog is interested in its surroundings.
* **Behavior**: The dog looks at nearby blocks (flowers, grass, trees) and performs a sniffing animation/particles.
* **Constraint**: Works in both sitting and standing states.

### 4. Fetch

* **Mood**: Playful energy.
* **Trigger**: A "fetchable" item (Stick, Bone) is dropped near the dog.
* **Behavior**:
  * **If Standing**: Runs to pick up the item and brings it to the owner's feet.
  * **If Sitting**: Does NOT fetch (respects the "Stay" command), but may look at the item longingly.

---

## 📏 Concept 7: Size Variation (DNA-driven)

Using the new 26.1 data-driven scale system, wolves now have subtle natural size differences.

* **Deterministic Scale**: The wolf's `Attributes.SCALE` is modified by its DNA.
* **Range**: Subtle variation (e.g., 0.9x to 1.1x) to ensure they still look like wolves but feel individual.
* **Persistence**: The scale is derived from the DNA on first spawn/tame and remains constant.

---

## 🛠 Technical Notes

* **Animation**: Head Tilts can be implemented via a Mixin on `WolfModel` or by manipulating `headRotation`.

* **Scaling**: All chances and thresholds should be exposed via `BetterDogsGameRules`.
