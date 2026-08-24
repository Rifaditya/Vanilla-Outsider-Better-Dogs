# Minecraft 1.20.1 Guide & Release Notes

*[[Home]] / [[Version Compatibility|Version-Compatibility]] / Minecraft 1.20.1 Guide*

---

## 📦 Version Information

| Property | Value |
| :--- | :--- |
| **Minecraft Target** | `1.20.1` (Legacy Anchor Era) |
| **Mod Version** | `1.0.70+1.20.1` |
| **Fabric Loader** | `>=0.15.0` |
| **Java JDK Requirement** | Java 17 (`release = 17`) |
| **Build Tooling** | Fabric Loom 1.10.2 / Mojang Official Mappings |
| **Jar Naming Convention** | `better-dogs-1.0.70+1.20.1.jar` |

---

## 🌟 Overview & Feature Parity

The Minecraft 1.20.1 release of **Vanilla Outsider: Better Dogs** provides complete 39-feature parity with the modern release line while strictly respecting Minecraft 1.20.1's native runtime APIs.

### Key Architectural & Engine Highlights
* **Visual Entity Scaling**: Uses `WolfRendererMixin` to dynamically scale the wolf model rendering matrix ($0.70\times$ to $1.45\times$) backed by synchronized `DataTracker` keys (`betterdogs$synchedScale`).
* **Custom Chorus Howl Audio Event**: Registers `betterdogs:entity.wolf.howl` in `BuiltInRegistries.SOUND_EVENT` with pitch-modulated vanilla canine fallbacks in `sounds.json`.
* **Canine Personalities & Genetics**: Complete support for Aggressive, Pacifist, and Normal personalities, multi-puppy litters ($1\text{ to }4$ puppies), and inbreeding lineage tracking.
* **Paper Adoption System**: Shift + Right-Click with Paper to list/cancel adoption; empty-handed players can adopt listed dogs.
* **Sentinel Guard Mode**: Shift + Right-Click with Bone to anchor dogs to a sentinel guard post.
* **Autonomous Ground Feeding**: Dogs seek out and consume dropped edible meat to heal, incrementing `feedCount` and triggering favorite treat zoomies.
* **Command Suite & Advancements**: Full Brigadier `/betterdogs` command suite and 13 custom Husbandry advancements.

---

## 🔧 Installation & Requirements

1. Install **Fabric Loader** `0.15.0` or higher for Minecraft `1.20.1`.
2. Install **Fabric API** for 1.20.1 into your `mods/` folder.
3. Place `better-dogs-1.0.70+1.20.1.jar` into your `mods/` directory.
4. Launch using **Java 17**.

---

*Back to [[Home]] | View [[Version Compatibility|Version-Compatibility]]*
