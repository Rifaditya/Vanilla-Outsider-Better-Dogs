# Config & GameRules Technical Reference

Better Dogs exposes all AI parameters, percentages, scaling factors, and mechanics directly to native Minecraft GameRules (grouped under the **"Better Dogs"** category in the UI) and visual client configuration settings.

---

## ⚙️ Native GameRules Reference

Here is a microscopic breakdown of the key GameRules supported by the mod, categorized by their milestone functions:

### 1. General & QoL Settings
| GameRule | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `bd_scavenge_feeding` | Boolean | `true` | Allows tamed wolves to find and eat dropped food from the ground to heal themselves. |
| `bd_calm_down` | Boolean | `true` | Enables clearing a wolf's anger and target states via Shift+Right-Click with an empty hand. |
| `bd_paper_adoption` | Boolean | `true` | Allows players to put tamed dogs into a pending adoption state using Paper. |
| `bd_friendly_fire_protection` | Boolean | `true` | Blocks sweeping edge attack damage from the owner to their tamed wolves. |
| `bd_gift_feed_threshold` | Integer | `10` | The number of interactions (feeding, sitting, calming down) required before a tamed wolf brings morning gifts. |
| `bd_gift_interaction_cooldown` | Integer | `100` | The cooldown in ticks (20 ticks = 1 second) required between free positive interactions to count towards the gift threshold. |
| `bd_demerit_accidental_attacks` | Boolean | `true` | If true, accidentally attacking your dog will reset its gifting merits. If false, only sneaking and attacking resets it. |

### 2. Genetics & Breeding Settings
| GameRule | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `bd_inbreeding_penalties` | Boolean | `true` | Enables runt scaling penalties, low speed, and low health when breeding closely related parent lines. |
| `bd_litter_max_size` | Integer | `4` | Caps the maximum number of puppies that can spawn from a single breeding event. |
| `bd_breeding_same_parent_chance` | Integer | `80` | Percentage probability ($80\%$) that offspring inherit the shared personality of identical parents. |
| `bd_breeding_same_parent_other_chance`| Integer | `10` | Percentage probability ($10\%$) for each of the alternative personalities if identical parents fail inheritance check. |

### 3. Pack Spread Scaling Settings
| GameRule | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `bd_tamed_pack_spread_multiplier` | Double | `0.5` | The scaling factor (multiplier) applied to tamed packs during boids spacing calculations. |
| `bd_tamed_pack_spread_max` | Double | `2.5` | The absolute maximum spacing bounds (in blocks) for tamed pack followers. |
| `bd_wild_pack_spread_multiplier` | Double | `0.75` | The scaling factor (multiplier) applied to wild packs during boids spacing calculations. |
| `bd_wild_pack_spread_max` | Double | `3.5` | The absolute maximum spacing bounds (in blocks) for wild pack followers. |
| `bd_pack_flanking_tactics` | Boolean | `true` | Enables true pack hunting AI, allowing followers to dynamically flank and surround enemies instead of attacking in a straight line. |

### 4. Domestic Guard Mode Settings
| GameRule | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `bd_guard_range` | Integer | `16` | The maximum distance (in blocks) a guarding wolf can chase targets from its post coordinates before pathing back. |
| `bd_guard_buffs` | Boolean | `true` | Allows guarding Pacifist watchdog alarms to apply Regeneration and Resistance status buffs to their owners. |
| `bd_guard_line_of_sight` | Boolean | `true` | Restricts guard auto-attacks to targets within a clear line-of-sight (prevents through-wall cave aggro). |

### 5. Wild Territoriality Settings
| GameRule | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `bd_wild_pack_dominance_matrix` | Boolean | `true` | Enables the Territorial Probability Matrix calculations when wild packs meet. |
| `bd_wild_pack_duel_chance` | Integer | `30` | Percentage probability ($30\%$) that a pack war resolves via a cinematic 1v1 leader duel instead of full pack combat. |

---

## 🎨 Visual Configuration (ModMenu & Cloth Config)

For client-side rendering settings that do not impact world logic, parameters are configured via **ModMenu** + **Cloth Config** screen:

### 1. `guardParticleDensity`
Determines the number of rotated directional red dust particles emitted during Pacifist watchdog alarms:
* **`high`**: Spawns 12 particles (spaced at $30^\circ$ steps within the $60^\circ$ cone).
* **`medium`** (Default): Spawns 6 particles (spaced at $60^\circ$ steps).
* **`low`**: Spawns 3 particles (spaced at $120^\circ$ steps).
* **`off`**: Completely disables sentinel alert particles.

### 2. Client Isolation Guarantee
These screen builder types are loaded lazily. If a server is running in a headless environment, these client configurations are ignored, preventing any classloading crashes due to missing client rendering APIs.
