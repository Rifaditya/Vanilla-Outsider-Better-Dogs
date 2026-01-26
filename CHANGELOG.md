# Changelog

## v3.1.19

- **Localization Fix:** Prepended `minecraft.` namespace to all Game Rule translation keys (e.g., `gamerule.minecraft.bd_storm_anxiety`) to match vanilla Game Rule registry behavior. This ensures Game Rules are properly named in the "Edit Game Rules" screen.

## v3.1.18

- **Fix:** Resolved startup crash caused by `IdentifierException`.
- **Technical:** Renamed all Game Rule IDs to lower_snake_case (e.g., `bdStormAnxiety` -> `bd_storm_anxiety`) to comply with Minecraft 1.21+ registry standards.
- **Localization:** Updated `en_us.json` to match new Game Rule keys.

## v3.1.17

- **Technical:** Internal build for localization fixes.

## v3.1.13

- **Feature:** Added native Game Rules for all configuration options.
- **Feature:** Removed Cloth Config dependency for 26.1+ versions.
