# Guard Mode & Patrols

Tamed wolves can be assigned static guard duties, allowing them to patrol an area, defend a base, or act as early warning systems while you are away.

---

## 1. Activating Guard Mode
To place a tamed wolf on guard duty:
1.  **Requirement**: Hold a standard **Bone** in your hand.
2.  **Action**: **Shift + Right-Click** the wolf.
3.  **Visual Indicator**: The wolf will sit briefly, bark, and begin emitting custom-colored dust particles matching their personality.
4.  **Deactivation**: **Shift + Right-Click** them again with a Bone or an empty hand to release them from duty.

---

## 2. Guard Patrol Patterns
Once anchored, wolves patrol their guard post using distinct pathfinding algorithms based on their personality:

*   **Aggressive (Perimeter Sweep)**: Performs wide, sweeping loops around the outer boundary of the anchor zone. Excellent for detecting and intercepting hostiles before they reach your base.
*   **Normal (Radial Patrol / Sentinel Post)**: Paces in radial star patterns outwards from the center and back. If tired, they will stand guard directly at the post.
*   **Pacifist (Orbital Ring)**: Walks in circular orbital loops close to the anchor point, keeping watch over a tight radius.

---

## 3. Targeting Restrictions (Anti-Jank)
To prevent wolves from constantly aggroing on hidden enemies (such as zombies in caves directly underneath your floor), targeting is strictly limited:
*   **Line-of-Sight Mandate**: Wolves require direct line-of-sight to target hostiles. They will not bark or attack through solid walls.
*   **Pacifist Hearing exception**: Pacifist wolves are the sole exception. They possess heightened senses and can "hear" hostiles through solid blocks up to **4 blocks vertically** or spot them up to **16 blocks horizontally** (regardless of walls), alerting their owner immediately.

---

## 4. Visual Guard Particles
Active guard wolves emit continuous, subtle particle effects to show they are on duty:
*   **Aggressive**: Red dust particles.
*   **Normal**: Gold dust particles.
*   **Pacifist**: Green/Teal dust particles.
