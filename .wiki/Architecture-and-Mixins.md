# Architecture & Mixins (Minecraft 1.20.1)

*[[Home]] / Architecture & Mixins*

---

## 🏛️ Package Organization

```
net.vanillaoutsider.betterdogs
├── ai/              # AI Goals (Eating, Guarding, Petting, Howling, Vehicles)
├── config/          # Client config & ModMenu integration
├── gamerules/       # Fabric API GameRule definitions & registration
├── mixin/           # Bytecode Mixin transformers
└── util/            # Dedicated 1-File 1-Purpose helper utilities
```

---

## 🧩 Mixin Injection Breakdown

| Mixin Class | Target Class | Injection Point | Purpose |
| :--- | :--- | :--- | :--- |
| `WolfEntityMixin` | `Wolf` | `initGoals`, `addAdditionalSaveData`, `readAdditionalSaveData`, `defineSynchedData` | Personality state, NBT persistence, scale sync data |
| `WolfInteractMixin` | `Wolf` | `mobInteract` (HEAD) | Shift+Paper adoption, Shift+Bone guard mode, Shift+Stick vehicle riding |
| `WolfAttackMixin` | `Wolf` | `doHurtTarget` | Aggressive bonus damage, Pacifist weakness penalty |
| `WolfDamageMixin` | `Wolf` | `hurt` | Defense calculations, combat panic, low-health fleeing |
| `WolfBreedMixin` | `Wolf` | `spawnChildFromBreeding` | Multi-puppy litters ($1\text{ to }4$), inheritance |
| `WolfSafetyMixin` | `Wolf` | `customServerAiStep` | Non-allocating cliff edge & thermal hazard detection |
| `WolfRendererMixin` | `WolfRenderer` | `scale` | Dynamic visual model matrix scaling ($0.70\times$ to $1.45\times$) |
| `GoatHornItemMixin` | `GoatHornItem` | `use` | Acoustic tactical command broadcast |
| `ServerPlayerMixin` | `ServerPlayer` | `disconnect` | Player session cleanup |
| `PlayerInteractEntityMixin` | `ServerGamePacketListenerImpl` | Interaction hooks | General debounce & interaction validation |

---

*Back to [[Home]]*
