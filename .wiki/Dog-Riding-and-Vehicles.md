# Dog Riding & Vehicle Transport

*[[Home]] / Dog Riding & Vehicles*

---

## 🚣 Infobox: Vehicle Boarding Summary

| Parameter | Specification |
| :--- | :--- |
| **Command Tools** | Stick (`minecraft:stick`), Blaze Rod (`minecraft:blaze_rod`), Breeze Rod (`minecraft:breeze_rod`), or `#vanilla-outsider-better-dogs:command_items` |
| **Selection Gesture** | Shift + Right-Click tamed dog with a Command Tool |
| **Boarding Gesture** | Shift + Right-Click target vehicle (Boat, Minecart, Horse, Camel) or seat block (Stairs, Chairs, Benches) with a Command Tool |
| **Dismount Gesture** | Shift + Right-Click boarded dog with a Command Tool |
| **Reserved Guard Item** | Bone (`minecraft:bone`) — *Reserved strictly for Guard Mode toggle (`bd_guard_mode`)* |
| **Unrestricted Riding Rule** | `betterdogs:bd_allow_unrestricted_dog_riding` (Default: `false`) |

---

## 🚗 1. Transport Boarding System

Commanding dogs to board or dismount vehicles is a 2-step process using any **Command Tool** (Stick, Blaze Rod, Breeze Rod, or `#vanilla-outsider-better-dogs:command_items`):

1. **Selection**: Hold a Command Tool and Shift + Right-Click a tamed dog (`"Selected [Name]"`). Emits note particles and plays ambient sound.
2. **Boarding**: With the Command Tool still in hand, Shift + Right-Click a target vehicle or seat block within 12 blocks (`"[Name] commanded to board [Vehicle]"`). The dog pathfinds to the vehicle and occupies a passenger seat.
3. **Dismounting**: Shift + Right-Click a boarded dog with a Command Tool to order it to dismount (`"[Name] dismounted"`). The dog leaves the seat and stands up.
4. **Seat Occupancy Check**: If all passenger seats are filled, the chat displays `"That seat is already occupied."`

> [!IMPORTANT]
> **Bone Reservation Safeguard**: Bone (`minecraft:bone`) is strictly used for toggling **Guard Mode** (`bd_guard_mode`). To avoid command conflicts, Bone is explicitly excluded from vehicle selection, boarding, and dismounting.

---

## ⚡ 2. Litematica Compatibility Notice

If you have **Litematica** installed:
* Litematica uses `minecraft:stick` as its default selection tool item on the client side, which intercepts stick right-clicks before entity interaction packets are sent.
* **Solution**: Use a **Blaze Rod** or **Breeze Rod** as your command tool (or change Litematica's tool item setting in its configuration menu). Blaze Rods and Breeze Rods bypass Litematica's stick tool interceptor completely while providing full dog commanding functionality!

---

## ⚙️ 3. Unrestricted Riding GameRule (`bd_allow_unrestricted_dog_riding`)

By default, dogs can board standard passenger vehicles (Boats, Chest Boats, Minecarts, Horses, Camels) and seat blocks (Stairs, Chairs). Enabling `/gamerule betterdogs:bd_allow_unrestricted_dog_riding true` permits commanding dogs to mount any entity, including custom mob mounts!

---

*Back to [[Home]] | View [[Adoption System|Adoption-System]]*
