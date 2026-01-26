# Better Dogs: Technical Manual (v3.1.25)

**Philosophy**: "Vanilla Outsider" - Mechanics should be invisible, intuitive, and seamlessly integrated into the base game. **Native Game Rules** and **Social DNA** provide deep immersion without the need for external configuration menus.

---

## 1. Personality & Genetic DNA System

Every wolf possesses permanent **Social DNA** (64-bit seed) and a calculated **Social Scale** (Size variation) stored via the **Fabric Data Attachment API**.

### A. Individual DNA

- Derived from the wolf's UUID.
- Determines participation odds for all social events (Zoomies, Begging, etc.).
- Result: Each dog feels like a unique individual with its own likes/dislikes.

### B. Genetic Scaling

- Visual size varies between **0.9x and 1.1x**.
- Hitboxes remain vanilla-sized for 100% pathfinding compatibility.

---

## 2. Hive Mind Social Engine (v6.1)

A centralized, world-pulsed scheduler handles all social behaviors through a **Highlander Unified Pulse**.

### A. Posture Integrity (Vanilla Spirit)

Social AI strictly respects the dog's sitting state. If a dog is ordered to sit via the "Stay" command, all voluntary social goals are suppressed to prevent posture-flickering and protect player agency.

### B. Global Cooldowns

To ensure natural pacing, each dog has a **2-10 minute Global Cooldown** after finishing a social event. Interactions are designed to be "special encounters" rather than constant spam.

---

## 3. Social Events (Behaviors)

- **Begging**: Passive interest when players hold food.
- **Fetch**: Interactive play when items are tossed nearby.
- **Zoomies**: Morning hyper-activity (random high-speed running).
- **Wanderlust**: Occasional curious exploration of nearby surroundings.
- **Idle Curiosity**: Soft interest in nearby entities or blocks.

---

## 4. Performance & Threading Model

### A. The Single-Thread Rule

Better Dogs operates strictly on the **Minecraft Server Thread**.

- **Reasoning**: Minecraft's entity components (Goal Selectors, Attributes, Data) are not thread-safe. Modifying them from a parallel thread would cause immediate crashes (`ConcurrentModificationException`).
- **Safety**: Main-thread execution guarantees 100% stability.

### B. O(1) Highlander Efficiency

Despite running on one thread, the cost is practically zero:

- **Constant Load**: The engine does NOT iterate over all dogs. It picks **one random dog** from the registry per tick.
- **Scaling**: Performance is identical whether you have 10 dogs or 10,000 in the world.

### C. Swarm Optimization

Large packs elect a "Leader" to perform environmental scanning. "Followers" mirror the leader's navigation, saving 70% CPU time in massive wolf populations.

---

## 5. Safety & Persistence

- **Fabric Attachments**: State-safe data preservation across version jumps.
- **DNA Migration**: Deterministic seed generation for old-world entities.
- **Native Game Rules**: All chances and ranges are configurable in the "Edit Game Rules" screen.

---
*Documentation for v3.1.25 | Highlander Pulse Engine v6.1*
