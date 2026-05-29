# Territorial Probability Matrix

Technical overview of the weighted outcome system for wild wolf pack territorial disputes.

## Matrix Values (v4.5.13)

Each encounter between two wild pack leaders results in a roll (0-99) against the following matrix, seeded by their UUIDs for synchronization:

| Encounter Pair | War (Fight) | Merge (Yield) | Run (Retreat) | Dominant Leader (If Merge) |
| :--- | :--- | :--- | :--- | :--- |
| **Aggro vs Aggro** | 80% | 10% | 10% | Health/ID based |
| **Aggro vs Normal** | 50% | 40% | 10% | Aggressive |
| **Aggro vs Pacifist**| 10% | 50% | 40% | Aggressive |
| **Normal vs Normal** | 20% | 50% | 30% | Health/ID based |
| **Normal vs Pacifist** | 5% | 45% | 50% | Normal |
| **Pacifist vs Pacifist**| 0% | 50% | 50% | Health/ID based |

---

## Technical Details

### 1. Synchronization
To prevent desync where one leader decides to fight and the other retreats, outcomes are synchronized using a shared seeded random generator:
```java
long seed = (long) Math.min(wolfA.getId(), wolfB.getId()) << 32 | (long) Math.max(wolfA.getId(), wolfB.getId());
Random seededRandom = new Random(seed);
```

### 2. Leader Duels & Member Brawls
- **duels**: Leaders duel 1v1 while followers stand by or participate in minor skirmishes.
- **Yielding**: Combat continues until one leader drops below 40% HP (or dies if fatal chance `bd_territorial_fatal_chance` rolls true).
- **Merge**: The defeated pack merges, pointing all followers to the victorious leader.

### 3. Performance Optimization
- **Throttling**: The 96-block rival pack scan (`serverLevel.getNearestEntity`) is throttled to run once every **40 to 80 ticks** (2-4 seconds) when no rival is nearby, protecting server TPS.
