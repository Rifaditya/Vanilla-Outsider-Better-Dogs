# Minecraft 1.21.1 Guide & Release Notes

*[[Home]] / [[Version Compatibility|Version-Compatibility]] / Minecraft 1.21.1 Guide*

---

## 📦 Version Information

| Property | Value |
| :--- | :--- |
| **Minecraft Target** | `1.21.1` (Transitional Anchor Early Era) |
| **Mod Version** | `1.0.70+1.21.1` |
| **Fabric Loader** | `>=0.16.0` |
| **Java JDK Requirement** | Java 21 (`release = 21`) |
| **Build Tooling** | Fabric Loom 1.10.2 / Mojang Official Mappings |
| **Jar Naming Convention** | `better-dogs-1.0.70+1.21.1.jar` |

---

## 🌟 Overview & Feature Parity

The Minecraft 1.21.1 release of **Vanilla Outsider: Better Dogs** provides complete 39-feature parity with the modern release line, utilizing Minecraft 1.21.1's native attributes and vanilla wolf coat variant mechanics.

### Key Architectural & Engine Highlights
* **Native Attribute Scaling**: Uses native `Attributes.SCALE` to seamlessly adjust wolf scale ($0.70\times$ to $1.45\times$) with full hitbox adjustment and client sync.
* **Vanilla Coat Variants & Biome Spawning**: Seamlessly integrates personality and genetics with vanilla 1.21's 9 wolf variants across biomes.
* **Custom Chorus Howl Audio Event**: Registers `betterdogs:entity.wolf.howl` with pitch-modulated harmonic response across packs.
* **Paper Adoption System**: Shift + Right-Click with Paper to list/cancel adoption; empty-handed players can adopt listed dogs.
* **Sentinel Guard Mode**: Shift + Right-Click with Bone to anchor dogs to a sentinel guard post.
* **Autonomous Ground Feeding**: Dogs seek out and consume dropped edible meat to heal, incrementing `feedCount` and triggering favorite treat zoomies.
* **Command Suite & Advancements**: Full Brigadier `/betterdogs` command suite and 13 custom Husbandry advancements.

---

## 🔧 Installation & Requirements

1. Install **Fabric Loader** `0.16.0` or higher for Minecraft `1.21.1`.
2. Install **Fabric API** for 1.21.1 into your `mods/` folder.
3. Place `better-dogs-1.0.70+1.21.1.jar` into your `mods/` directory.
4. Launch using **Java 21**.

---

*Back to [[Home]] | View [[Version Compatibility|Version-Compatibility]]*
