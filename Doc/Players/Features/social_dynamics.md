# Social Dynamics & QoL Features

This mod implements advanced social behaviors, group AI, and quality-of-life additions that make wolves behave like a natural pack.

---

## 1. Pack Structure & Dynamic Spacing
Wild wolves form persistent packs (up to 8 members) anchored around a pack leader:
*   **Cooperative Movement**: Packs use high-performance flocking pathing to move together efficiently.
*   **Spread Scaling**: To prevent wolves from walking in single-file lines or clipping into each other, packs dynamically scale their follow spacing based on the pack size ($N$):
    $$f(N) = \text{multiplier} \times \sqrt{N - 1}$$
    Larger packs naturally spread out wider, creating a realistic pack presence.

---

## 2. Immersive Social Events
Wolves participate in periodic pack behaviors based on their genetic preferences and time of day:
*   **Zoomies**: High-energy runs performed during early mornings or rainfall.
*   **Group Howl**: Vocalizations performed by wild and tamed packs during full moons or when prompted by their owner.
*   **Play Fighting**: Safe, non-lethal spars between packmates to burn off energy.

---

## 3. Quality-of-Life Tools

### adopotion Papers
If you wish to transfer ownership of your tamed wolf to another player:
1.  **Action**: **Shift + Right-Click** the tamed wolf while holding a piece of **Paper**.
2.  **State**: The wolf enters adoption mode and emits rose-pink trail particles.
3.  **Transfer**: Any other player can right-click the wolf to instantly adopt it.

### Calm Down
If your wolf is mistakenly attacking a target or stuck in an aggressive loop:
1.  **Action**: **Shift + Right-Click** the wolf with an **empty hand**.
2.  **Effect**: The wolf sits down immediately, and its current attack target and anger state are completely cleared.

---

## 4. Smart Survival AI (Anti-Jank)
Tamed wolves have undergone pathfinding training to eliminate frustrating deaths:
*   **Cliff Safety**: Wolves detect dangerous drops and will refuse to jump off high cliffs or ledge boundaries.
*   **Hazard Avoidance**: Actively pathfinds around fire, campfires, and lava pools to avoid accidental incineration.
*   **Friendly Fire Protection**: Tamed wolves are immune to the sweep attacks of their owners, preventing accidental strikes during combat.
