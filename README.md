# Elemental Creepers Reloaded

A rewrite of Lomeli12's Elemental Creepers (1.9.4) for Minecraft 26.2 on Forge. It adds 27 creepers, each with its
own explosion, the Creepapedia, and Creeperfish eggs hidden in the mountains.

Requires [LomLib Reloaded](../LomLibReloaded) 8.x.

## Creepers

Fire, Water, Electric, Cookie, Dark, Light, Earth, Magma, Reverse, Ice, Friendly (tameable with gunpowder), Ghost
(comes back when a player kills a creeper), Illusion, Psychic, Spider, Wind, Hydrogen, Ender, Stone, Solar, Cake,
Firework, Big Bad Creep, Spring, Creeperfish, Furnace and Warp.

Spawn weights and explosion sizes are in `config/elementalcreepersreloaded-common.toml`. Spawn weights apply the next
time a world loads.

## Error reporting (Nitea)

The mod reports its errors and crashes to [Nitea](https://nitea.cc), once the player allows it (Nitea asks on the title
screen, once for every mod that uses it). Players can also send feedback:

```
/elementalcreepers report bug <what happened>
/elementalcreepers report suggestion <idea>
```

The Creepapedia has a "Report a bug" button that fills in the first command.

What the integration does:

- `Nitea.init` runs first in the mod constructor, with the mod's class as `owner` (package
  `com.pyro.elementalcreepersreloaded`, which the SDK key is locked to).
- Each explosion leaves a breadcrumb (which creeper, charged or not, dimension and position), and runs inside
  `NiteaSupport.guard`: if one explosion throws, the error is reported with the creeper's name as a tag and the world
  keeps going.
- The config values that change explosions (`dome_explosions`, `special_events`) and the LomLib Reloaded version are
  tags on every report.

## Building

The SDK key is not in the sources. Copy `.env.example` to `.env` and put the key in it; the build bundles it as
`nitea/elementalcreepersreloaded.properties`.

```sh
./gradlew build       # build/libs/elementalcreepersreloaded-26.2-6.1.0.jar (Nitea nested inside)
./gradlew runClient   # the game with this mod and LomLib Reloaded
```

`settings.gradle` includes `../LomLibReloaded`, so both folders must sit side by side.
