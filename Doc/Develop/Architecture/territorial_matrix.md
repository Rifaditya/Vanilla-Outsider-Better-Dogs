# Territorial Probability Matrix

Technical overview of the weighted outcome system for wild wolf territorial disputes.

## Matrix Values (v3.4.16)

Each encounter between two pack leaders results in a roll (0-99) against the following matrix:

| Encounter Pair | War (Fight) | Merge (Yield) | Run (Retreat) | Dominant Leader (If Merge) |
| :--- | :--- | :--- | :--- | :--- |
| **Aggro vs Aggro** | 80% | 10% | 10% | Health/ID based |
| **Aggro vs Normal** | 50% | 40% | 10% | Aggressive |
| **Aggro vs Pacifist**| 10% | 50% | 40% | Aggressive |
| **Normal vs Normal** | 20% | 50% | 30% | Health/ID based |
| **Normal vs Pacifist** | 5% | 45% | 50% | Normal |
| **Pacifist vs Pacifist**| 0% | 50% | 50% | Health/ID based |

## Deterministic Synchronization
To prevent desync where one wolf thinks it's a war and the other thinks it's a retreat, the decision is synchronized using a shared seed:
```java
long seed = (long) Math.min(wolfA.getId(), wolfB.getId()) << 32 | (long) Math.max(wolfA.getId(), wolfB.getId());
Random seededRandom = new Random(seed);
```

## Configuration
All values are exposed as GameRules under the `bd_territorial_matrix_` prefix:
- `bd_territorial_matrix_aa_war`
- `bd_territorial_matrix_aa_merge`
- ...and so on for all 6 pairings.
