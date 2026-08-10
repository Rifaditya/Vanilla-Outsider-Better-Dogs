# API & Addon Integration Reference

*[[Home]] / API & Addon Integration*

---

## 🧩 DasikLibrary Integration

**Vanilla Outsider: Better Dogs** relies on **DasikLibrary** for standardized runtime guard protection and dynamic GameRule management:

### 1. Zero-Dependency Version Guard (`ModVersionGuard`)
During initialization (`onInitialize`), **Better Dogs** invokes `ModVersionGuard.checkClass()` to verify that the running environment matches minimum classloader requirements without introducing rigid launcher version locks:

```java
public class BetterDogsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModVersionGuard.checkClass("net.vanillaoutsider.betterdogs.BetterDogsFabric");
        BetterDogs.init();
    }
}
```

### 2. Dynamic GameRule Registration (`DynamicGameRuleManager`)
GameRules are registered dynamically using DasikLibrary's `DynamicGameRuleManager`, providing dynamic category grouping in the vanilla GameRules GUI (`gamerule.category.vanilla-outsider-better-dogs.better_dogs`):

```java
DynamicGameRuleManager.registerInt(
    "betterdogs",
    "bd_wolf_spawn_multiplier_percent",
    150,
    GameRuleCategory.SPAWNING
);
```

---

## 💾 Wolf Persistent State (`WolfExtensions`)

Persistent wolf metadata (personality, physical scale, guard anchors, inbred status, favorite treat) is stored directly on the entity via the `WolfExtensions` interface:

### Interfacing with Wolf Metadata
Third-party addon developers can cast any `Wolf` instance to `WolfExtensions` to access or modify custom properties:

```java
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.minecraft.world.entity.animal.Wolf;

public class AddonHelper {
    public static void checkWolf(Wolf wolf) {
        WolfExtensions ext = (WolfExtensions) wolf;
        
        // Retrieve personality enum (NORMAL, AGGRESSIVE, PACIFIST)
        WolfPersonality personality = ext.betterdogs$getPersonality();
        
        // Query scale factor (0.70 to 1.45)
        float scale = ext.betterdogs$getScale();
        
        // Query inbred status
        boolean isInbred = ext.betterdogs$isInbred();
        
        // Query guard mode state
        boolean isGuarding = ext.betterdogs$isGuarding();
    }
}
```

---

## 🔍 Jade WTHIT Tooltip Plugin (`BetterDogsJadePlugin`)

**Better Dogs** embeds a native plugin for **Jade (WTHIT)** (`BetterDogsJadePlugin`) that displays live entity diagnostics when hovering over a wolf:

### Tooltip Display Elements
* **Wolf Personality Tag**: Displays personality trait (Normal, Aggressive, Pacifist).
* **Inbred Runt Warning**: Shows `Inbred` badge (`betterdogs.jade.inbred`) if the wolf suffers from linebreeding penalties.
* **Favorite Treat Status**: Displays discovered favorite treat item (`betterdogs.jade.treat`).
* **Dynamic Health Overlay**: Displays exact health points taking into account personality HP bonuses.

Developers can enable or disable Jade tooltip components in client configuration via Jade's plugin settings menu.

---

*Back to [[Home]]*
