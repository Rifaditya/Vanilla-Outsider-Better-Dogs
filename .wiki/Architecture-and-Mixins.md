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

## 🌐 Multi-Loader Entity Synchronization (:common)

In multi-loader anchors (**Minecraft 1.20.1, 1.21.1, and 1.21.11**), Better Dogs maintains an architectural commitment to **Zero 3rd-Party Runtime Bloat**:

### Why Vanilla SynchedEntityData is Used
* Rather than binding to loader-specific data attachments (such as NeoForge Data Attachments or Fabric Data Attachments) or introducing third-party API dependencies (like Architectury), Better Dogs registers two entity data accessors directly on vanilla `Wolf` in `:common`:
  ```java
  @Unique
  private static final EntityDataAccessor<Boolean> BETTERDOGS$DATA_SUBDUED =
      SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

  @Unique
  private static final EntityDataAccessor<Boolean> BETTERDOGS$DATA_SPECTATOR_WAGGING =
      SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
  ```
* **NeoForge Advisory Notice**: NeoForge’s sync worker emits an informational `[modloading-sync-worker/WARN]` when registering synched data on foreign entities. This is safe, expected, and non-fatal.
* **Fail-Safe Try-Catch Gating**: Reads and writes to these accessors are enclosed in guarded exception handlers with local fallback variables, guaranteeing that entity ticking never crashes even during abnormal classloading transitions.

---

*Back to [[Home]]*
