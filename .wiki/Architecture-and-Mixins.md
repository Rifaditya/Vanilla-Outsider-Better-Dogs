# Architecture & Mixins (Minecraft 1.21.11 / Winter Drop)

*[[Home]] / Architecture & Mixins*

---

## 🏛️ Package Organization

```
net.vanillaoutsider.betterdogs
├── ai/              # AI Goals (Eating, Guarding, Petting, Howling, Vehicles)
├── config/          # Client config & ModMenu integration
├── gamerules/       # Dynamic BuiltInRegistries.GAME_RULE registrations
├── mixin/           # Bytecode Mixin transformers
└── util/            # Dedicated 1-File 1-Purpose helper utilities
```

---

## 🧩 Mixin Injection Breakdown

| Mixin Class | Target Class | Injection Point | Purpose |
| :--- | :--- | :--- | :--- |
| `WolfEntityMixin` | `net.minecraft.world.entity.animal.wolf.Wolf` | `initGoals`, `addAdditionalSaveData`, `readAdditionalSaveData`, `createAttributes` | Personality state, NBT persistence, `Attributes.SCALE` registration |
| `WolfInteractMixin` | `Wolf` | `mobInteract` (HEAD) | Shift+Paper adoption, Shift+Bone guard mode, Shift+Stick vehicle riding |
| `WolfAttackMixin` | `Wolf` | `doHurtTarget` | Aggressive bonus damage, Pacifist weakness penalty |
| `WolfDamageMixin` | `Wolf` | `hurt` | Defense calculations, combat panic, low-health fleeing |
| `WolfBreedMixin` | `Wolf` | `spawnChildFromBreeding` | Multi-puppy litters ($1\text{ to }4$), inheritance |
| `WolfSafetyMixin` | `Wolf` | `customServerAiStep` | Non-allocating cliff edge & thermal hazard detection |
| `GoatHornItemMixin` | `GoatHornItem` | `use` | Acoustic tactical command broadcast |
| `ServerPlayerMixin` | `ServerPlayer` | `disconnect` | Player session cleanup |
| `PlayerInteractEntityMixin` | `ServerGamePacketListenerImpl` | Interaction hooks | General debounce & interaction validation |

---

*Back to [[Home]]*
