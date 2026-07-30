# 🐕 Better Dogs - Version 4.8.2 (The Ride, Howl & Hazard Polish Update)

Welcome to the **Ride, Howl & Hazard Polish Update**! Version 4.8.2 introduces vehicle boarding commands, restores classic wolf audio, improves pathfinding around magma hazards, and delivers critical safety measures to prevent wolves from pushing each other into danger.

---

## 🚗 Select-and-Ride Command System
Wolf transportation and companion handling is now fully unified:
* **Stick Commands**: Sneak-right-click a tamed wolf with a standard vanilla Stick to command it to sit or board any nearby vehicle.
* **Vehicle Compatibility**: Fully supports boats, minecarts, vanilla mounts, and modded chairs.
* **Visual Parity**: Wolves maintain their visual sitting pose while riding vehicles to keep animations clean.

---

## 🐺 Pack Collision & Hazard Safety
Your wolves are much more aware of dangerous terrain and each other:
* **Magma block Avoidance**: Wolves will actively avoid pathfinding onto magma blocks, keeping them safe from heat/burning damage.
* **Collision Push Prevention**: Colliding wolves will immediately stop if they encounter a packmate that is sitting, guarding, or endangered (positioned next to hazards like lava, magma, fire, or cliffs). No more pushing companions off ledges!
* **Ground Food Refusal Trait**: Selectively bred or tamed-from-birth puppies can persistently refuse to eat food dropped on the ground. This ensures they only eat when fed directly by their owner (governed by the `bd_enable_refuse_ground_food` GameRule).

---

## 🔊 Restored Audio & Soundscapes
* **Classic Howling Sounds**: Restored and repackaged the atmospheric vanilla pack howl audio files and registered the `betterdogs:entity.wolf.howl` sound event.
* **Dynamic Whining Thresholds**: Replaced absolute HP checks (`< 20 HP`) with dynamic whimpering threshold scaling (`< 50% max HP`), preventing Runts and puppies from crying constantly when fully healthy.

---

*This update is fully server-side compatible! Vanilla clients can connect to servers running version 4.8.2 without needing to install the mod.*
