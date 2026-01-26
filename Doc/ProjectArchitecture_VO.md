# Project Architecture: Better Dogs (v3.1.25)

## Philosophy

**"Immersion via Systems (Native Integration)."**
Better Dogs enhances wolf behavior through hidden complexity. We use the **Highlander Unified Pulse (v6.1)** and **Social DNA** to create behaviors that feel organic and individual. The mod relies exclusively on **Native Minecraft Game Rules**, ensuring a "Mod-less" feel.

## Structure

- **Core**: Single-module Java 25 project.
- **Engine (net.vanillaoutsider.social.core)**:
  - `GlobalSocialSystem`: The "Highlander" pulse guard. Central brain for the world.
  - `SocialRegistry`: WeakReference tracking for O(1) global entity sampling.
  - `EntitySocialScheduler`: Local event manager (Mood vs Ambient tracks).
  - `SocialEvent`: Contract for modular, DNA-driven behaviors.
- **Implementation (net.vanillaoutsider.betterdogs)**:
  - `ai/`: Content-specific AI goals (Fetch, Begging, Wanderlust).
  - `mixin/`: Direct injections (Wolf, ServerLevel, attributes).
  - `WolfPersistentData`: Fabric Attachment logic for DNA, scale, and traits.

## Key Components

- **The Highlander Pulse**: A single-threaded, O(1) master tick providing global behavior updates without per-entity cost.
- **Social DNA**: Permanent 64-bit seed derived from UUID, driving all character-specific participation odds.
- **Genetic Scaling**: DNA-driven size variation (0.9x to 1.1x) with vanilla-compatible hitboxes.
- **Postural Integrity**: Hard-gated safety hooks ensuring all AI respects the "Stay" (sitting) command.

---
*Last Updated: January 2026 | Highlander Engine v6.1*
