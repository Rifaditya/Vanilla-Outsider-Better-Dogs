# Namespaced GameRules Reference

*[[Home]] / GameRules*

---

## ⚙️ Complete GameRules Specification

All server options in **Vanilla Outsider: Better Dogs** are controlled dynamically via namespaced GameRules under the prefix `betterdogs:`. Server operators can modify rules in real time via `/gamerule` or the in-game command suite.

### Category: §lVanilla Outsider: Better Dogs

| GameRule Key | Data Type | Default | Description |
| :--- | :---: | :---: | :--- |
| `betterdogs:bd_wolf_spawn_multiplier_percent` | Integer | `150` | Multiplier for wolf spawn weight in biome spawn settings (150 = 1.5x spawn weight). |
| `betterdogs:bd_wolf_min_scale_percent` | Integer | `70` | Minimum scale percentage for wolves (70%). Controls rendering and physical bounding box. |
| `betterdogs:bd_wolf_max_scale_percent` | Integer | `145` | Maximum scale percentage for wolves (145%). Controls rendering and physical bounding box. |
| `betterdogs:bd_storm_anxiety` | Boolean | `true` | If true, wolves whine and shake during thunderstorms based on personality. |
| `betterdogs:bd_cliff_safety` | Boolean | `true` | If true, prevents wolves from pathfinding or chasing targets off dangerous cliffs/void drops. |
| `betterdogs:bd_creeper_awareness` | Boolean | `true` | If true, tamed dogs detect swelling creepers and flee radially at 1.5x sprint speed. |
| `betterdogs:bd_creeper_evasion_enabled` | Boolean | `true` | If true, enables tactical blast evasion sprint away from active creeper fuses. |
| `betterdogs:bd_flee_low_health` | Boolean | `true` | If true, wolves low on health (<30%) attempt to flee from active combat. |
| `betterdogs:bd_friendly_fire_protection` | Boolean | `true` | If true, prevents owners from damaging their tamed dogs via sweeping attacks or direct hits. |
| `betterdogs:bd_dogs_eat_raw_food` | Boolean | `true` | If true, tamed dogs automatically consume dropped raw meat from the ground to heal. |
| `betterdogs:bd_dogs_eat_cooked_food` | Boolean | `true` | If true, tamed dogs automatically consume dropped cooked meat from the ground to heal. |
| `betterdogs:bd_enable_refuse_ground_food` | Boolean | `true` | If true, some dogs born tamed refuse to eat food dropped on the ground. |
| `betterdogs:bd_refuse_ground_food_chance` | Integer | `30` | Percentage chance (0-100) that a dog born tamed will refuse ground food. |
| `betterdogs:bd_favorite_treats` | Boolean | `true` | If true, wolves have a hidden favorite treat (seeded by UUID) triggering zoomies & full heal. |
| `betterdogs:bd_aggressive_health` | Integer | `-10` | Bonus health points (half-hearts) for Aggressive personality wolves (-5 hearts). |
| `betterdogs:bd_aggro_speed_percent` | Integer | `15` | Sprinting speed modifier percent for Aggressive wolves (+15% speed). |
| `betterdogs:bd_aggro_dmg_percent` | Integer | `-15` | Attack damage modifier percent for Aggressive wolves (-15% damage). |
| `betterdogs:bd_aggro_follow_start` | Integer | `10` | Follow start distance from owner for Aggressive wolves (10 blocks). |
| `betterdogs:bd_aggro_chase_dist` | Integer | `50` | Maximum chase distance limit for Aggressive wolves (50 blocks). |
| `betterdogs:bd_aggro_detect_range` | Integer | `20` | Aggressive wolf hostile mob detection range (20 blocks). |
| `betterdogs:bd_aggro_flee_chance` | Integer | `10` | Percentage chance for Aggressive wolves to flee when low on health (10%). |
| `betterdogs:bd_paci_health` | Integer | `20` | Bonus health points (half-hearts) for Pacifist personality wolves (+10 hearts). |
| `betterdogs:bd_paci_speed_percent` | Integer | `-10` | Sprinting speed modifier percent for Pacifist wolves (-10% speed). |
| `betterdogs:bd_paci_dmg_percent` | Integer | `15` | Attack damage modifier percent for Pacifist wolves (+15% damage). |
| `betterdogs:bd_paci_knockback_percent` | Integer | `50` | Knockback modifier percent applied to Pacifist wolf attacks (+50%). |
| `betterdogs:bd_paci_follow_start` | Integer | `6` | Follow start distance from owner for Pacifist wolves (6 blocks). |
| `betterdogs:bd_paci_flee_chance` | Integer | `100` | Percentage chance for Pacifist wolves to flee when low on health (100%). |
| `betterdogs:bd_pacifist_guard_buffs` | Boolean | `false` | If true, Pacifist guard dogs apply Regeneration and Resistance to nearby owners. |
| `betterdogs:bd_normal_follow_start` | Integer | `10` | Follow start distance from owner for Normal wolves (10 blocks). |
| `betterdogs:bd_normal_flee_chance` | Integer | `50` | Percentage chance for Normal wolves to flee when low on health (50%). |
| `betterdogs:bd_spawn_normal_percent` | Integer | `60` | Spawn weight percentage for Normal wild wolves (60%). |
| `betterdogs:bd_spawn_aggro_percent` | Integer | `20` | Spawn weight percentage for Aggressive wild wolves (20%). |
| `betterdogs:bd_spawn_paci_percent` | Integer | `20` | Spawn weight percentage for Pacifist wild wolves (20%). |
| `betterdogs:bd_breed_same_chance` | Integer | `80` | Percentage chance for offspring to inherit exact parent personality if both match (80%). |
| `betterdogs:bd_breed_same_other_chance` | Integer | `10` | Percentage chance for offspring to mutate to a variant personality (10%). |
| `betterdogs:bd_breed_mixed_dominant_chance` | Integer | `40` | Percentage chance for dominant Normal inheritance when breeding Normal + Variant (40%). |
| `betterdogs:bd_breed_mixed_recessive_chance` | Integer | `40` | Percentage chance for recessive variant inheritance when breeding Normal + Variant (40%). |
| `betterdogs:bd_enable_inbred_curing` | Boolean | `true` | If true, feeding a Golden Apple cures genetic inbreeding penalties. |
| `betterdogs:bd_show_runt_particles` | Boolean | `false` | If true, tamed runt (inbred) wolves emit subtle rotten flesh particles. |
| `betterdogs:bd_guard_patrol_range_aggressive` | Integer | `12` | Aggressive guard patrol radius in blocks (12 blocks). |
| `betterdogs:bd_guard_patrol_range_normal` | Integer | `0` | Normal guard patrol radius in blocks (0 = stationary post). |
| `betterdogs:bd_guard_patrol_range_pacifist` | Integer | `3` | Pacifist guard patrol radius in blocks (3 blocks). |
| `betterdogs:bd_horn_command_range` | Integer | `64` | Maximum distance in blocks for goat horn commands to reach owned wolves (64 blocks). |
| `betterdogs:bd_horn_override_duration` | Integer | `600` | Duration in ticks for tactical horn state overrides (600 ticks = 30 seconds). |
| `betterdogs:bd_allow_unrestricted_dog_riding` | Boolean | `false` | If true, permits commanding dogs to ride any vehicle entity. |
| `betterdogs:bd_gift_feed_threshold` | Integer | `10` | Interactions required before a tamed wolf brings morning gifts (10). |
| `betterdogs:bd_pack_flanking_tactics` | Boolean | `true` | If true, wolves dynamically surround targets instead of attacking in a straight line. |
| `betterdogs:bd_flanking_raycast_check` | Boolean | `true` | If true, performs raycasts to verify flanking paths are clear of walls/deep drops. |
| `betterdogs:bd_nemesis_system` | Boolean | `true` | If true, wolves hold a temporary pack grudge against mob types that kill pack mates. |
| `betterdogs:bd_nemesis_duration_days` | Integer | `3` | Number of in-game days a pack holds a grudge against a mob type (3 days). |
| `betterdogs:bd_debugging` | Boolean | `false` | Enables detailed AI system logs and unlocks `/betterdogs debug` subcommands. |

---

*Back to [[Home]] | View [[Commands]]*
