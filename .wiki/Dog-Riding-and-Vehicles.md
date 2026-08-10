# Dog Riding & Vehicle Transport

*[[Home]] / Dog Riding & Vehicles*

---

## 🚣 Infobox: Vehicle Boarding Summary

| Parameter | Specification |
| :--- | :--- |
| **Selection Gesture** | Shift + Right-Click tamed dog with empty hand |
| **Boarding Gesture** | Shift + Right-Click vehicle entity (Boat, Minecart, Horse) |
| **Dismount Gesture** | Shift + Right-Click dog while boarded |
| **Unrestricted Riding Rule** | `betterdogs:bd_allow_unrestricted_dog_riding` (Default: `false`) |

---

## 🚗 1. Transport Boarding System

1. **Selection**: Shift + Right-Click a tamed dog with an empty hand (`"Selected [Name] for command. Shift+Right-click a vehicle to board."`).
2. **Boarding**: Shift + Right-Click a target vehicle within reach (`"Commanded [Name] to board [Vehicle]."`). The dog pathfinds to the vehicle and enters the passenger seat.
3. **Seat Occupancy Check**: If all passenger seats are filled, the chat displays `"That seat is already occupied."`

---

## ⚙️ 2. Unrestricted Riding GameRule (`bd_allow_unrestricted_dog_riding`)

By default, dogs can board standard passenger vehicles (Boats, Chest Boats, Minecarts, Horses). Enabling `/gamerule betterdogs:bd_allow_unrestricted_dog_riding true` permits commanding dogs to mount any entity, including custom mob mounts!

---

*Back to [[Home]] | View [[Adoption System|Adoption-System]]*
