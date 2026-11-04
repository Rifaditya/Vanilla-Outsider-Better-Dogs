# Minecraft 1.21.11 Guide & Release Notes

*[[Home]] / [[Version Compatibility|Version-Compatibility]] / Minecraft 1.21.11 Guide*

---

## 📦 Version Information

| Property | Value |
| :--- | :--- |
| **Minecraft Target** | `1.21.11` (Transitional Late Anchor / Winter Drop / 1.21.4) |
| **Mod Version** | `1.0.73+1.21.11` |
| **Loaders Supported** | **Fabric** (`>=0.16.0`) & **NeoForge** (`>=21.4.0`) |
| **Java JDK Requirement** | Java 21 (`release = 21`) |
| **Build Tooling** | Fabric Loom Remap 1.15-SNAPSHOT / NeoForge Loom |
| **Jar Naming Convention** | `better-dogs-1.0.73+1.21.11-fabric.jar`<br>`better-dogs-1.0.73+1.21.11-neoforge.jar` |

---

## 🌟 Overview & Feature Parity

The Minecraft 1.21.11 release of **Vanilla Outsider: Better Dogs** provides complete 39-feature parity with the modern release line, fully adapted to the Winter Drop API relocations.

### Key Architectural & Engine Highlights
* **Native Multi-Loader Engine**: Built from a shared `:common` module with dedicated standalone `:fabric` and `:neoforge` loaders.
* **Relocated Entity Package Architecture**: Built for `net.minecraft.world.entity.animal.wolf.Wolf` with modern `Identifier.fromNamespaceAndPath()` and `CompoundTag.getOptional()`.
* **Native Attribute Scaling**: Uses native `Attributes.SCALE` to adjust wolf physical scale ($0.70\times$ to $1.45\times$).
* **Custom Chorus Howl Audio Event**: Registers `betterdogs:entity.wolf.howl` with pitch-modulated harmonic responses.
* **Paper Adoption System**: Shift + Right-Click with Paper to list/cancel adoption; empty-handed players can adopt listed dogs.
* **Sentinel Guard Mode**: Shift + Right-Click with Bone to anchor dogs to a sentinel guard post.
* **Autonomous Ground Feeding**: Dogs seek out and consume dropped edible meat to heal, incrementing `feedCount` and triggering favorite treat zoomies.
* **Command Suite & Advancements**: Full Brigadier `/betterdogs` command suite and 13 custom Husbandry advancements.

---

## 🔧 Installation & Requirements

### Option A: Fabric
1. Install **Fabric Loader** `0.16.0` or higher for Minecraft `1.21.11` (or 1.21.4).
2. Install **Fabric API** for 1.21.11 into your `mods/` folder.
3. Place `better-dogs-1.0.73+1.21.11-fabric.jar` into your `mods/` directory.
4. Launch using **Java 21**.

### Option B: NeoForge
1. Install **NeoForge** `21.4.0` or higher for Minecraft `1.21.11` (or 1.21.4).
2. Place `better-dogs-1.0.73+1.21.11-neoforge.jar` into your `mods/` directory. *(No Fabric API or external bridge libraries needed!)*
3. Launch using **Java 21**.

> ℹ️ **NeoForge Console Note**: On startup, NeoForge will log a one-time advisory notice: `Identified an attempt to add synced data to a foreign entity: this is highly discouraged`. This is **100% normal, safe, and non-fatal**. See [[Version Compatibility & Lifecycle Policy|Version-Compatibility]] for why Better Dogs uses vanilla data synchronization for multi-loader parity.

---

*Back to [[Home]] | View [[Version Compatibility|Version-Compatibility]]*
