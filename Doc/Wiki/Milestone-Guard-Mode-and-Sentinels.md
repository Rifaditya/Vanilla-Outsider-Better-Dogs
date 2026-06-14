# Milestone: Guard Mode & Sentinels

Introduced in **`v4.8.0` (MC 26.2)** and backported to **`v3.10.0` (MC 26.1.2)**, this milestone implements the domestic guard system, watchdog alert stances, directional trigonometry particles, and rate-limited pack howling.

---

## 🛡️ Domestic Guard Mode

Tamed wolves can be configured to defend specific posts or patrol routes:
* **Activation**: Gated strictly to the owner. Sneak (Shift) + Right-Click the wolf while holding a bone to toggle **Guard Mode** (consuming exactly 1 bone).
* **Line-of-Sight Gating**: Guard dogs require a clear line-of-sight to target hostiles, preventing them from trying to attack mobs through solid walls (such as cave mobs beneath a house).

### Patrol Patterns
Each personality walks a distinct patrol route anchored to their guard post coordinates:
* **Aggressive**: Paces in an outer perimeter circle/polygon sweep at $80\%$ of their max guard range, pausing periodically to scan outward for threats.
* **Normal**: Sentry posture directly at the post coordinates (if range is 0) or walking radial paths outward and back (if range > 0).
* **Pacifist**: Close protective orbital circular pacing around the post coordinates.

---

## 🚨 Watchdog Alarms & Alert Stance

When a guarding Pacifist dog detects hostile mobs within 16 blocks (with vertical hearing up to 4 blocks through walls), it triggers the **Watchdog Alarm**:
1. **Alert Stance**: The wolf instantly stands up (clearing sitting poses), freezes its navigation pathfinding, and locks its head and body orientation directly toward the closest threat every game tick.
2. **Audio/Visual Warnings**: The wolf whines and shoots a directional spray of alarm particles.
3. **Owner Buffs**: Applies Regeneration and Resistance status buffs to the owner and nearby allies if the corresponding GameRule is enabled.

---

## 📐 Directional Trigonometric Particle Spray

During a Watchdog Alarm, red dust particles are projected outward in the exact direction the wolf is looking.

### 1. Direction Math
The mod projects a 3D forward-pointing cone spanning $60^\circ$ centered around the wolf's looking vector:
* Fetches the 3D look vector: $\vec{L} = (L_x, L_y, L_z)$.
* Calculates yaw angle: $\theta = \text{atan2}(L_z, L_x)$ and pitch: $\phi = L_y$.
* Computes horizontal length: $H = \sqrt{L_x^2 + L_z^2}$.
* For each particle, rotates the yaw by an offset $\Delta\theta$ within the $60^\circ$ cone:
  \[v_x = \cos(\theta + \Delta\theta) \times H\]
  \[v_z = \sin(\theta + \Delta\theta) \times H\]
* Spawns particles slightly in front of the face at mouth level ($y = \text{eyeHeight} - 0.1$).

### 2. Configurable Particle Density
A dedicated, client-side configuration setting `guardParticleDensity` inside the Cloth Config screen scales particle spawn counts to balance aesthetics and performance:
* **High**: Spawns 12 particles (spaced at $30^\circ$ steps within the $60^\circ$ cone).
* **Medium** (Default): Spawns 6 particles (spaced at $60^\circ$ steps).
* **Low**: Spawns 3 particles (spaced at $120^\circ$ steps).
* **Off**: Disables particles entirely.

---

## 🌕 Rare Lunar Howling & Cooldowns

To prevent large packs of wolves from howling constantly and creating auditory spam, howling is rate-limited:
1. **Rate-Limited Rolls**: Cooldown checks are throttled to run only once every 100 ticks ($5$ seconds) rather than every tick.
2. **Shared Pack Cooldowns**: When a wolf initiates a pack howl during a full moon, it searches for all nearby pack members and sets their `GroupHowlGoal` cooldown to a full 10 minutes ($12,000$ ticks). This ensures howling remains a rare, atmospheric group event.
