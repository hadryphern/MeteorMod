# Falling Meteors — Minecraft 1.20.1 (Forge)

A port of **Falling Meteors** by **AlexDGr8r** ([original source](https://github.com/AlexDGr8r/MeteorsMod), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/falling-meteors-mod)) from Minecraft 1.7.10 (v2.14.3) to Minecraft **1.20.1 / Forge 47.4+**, made with the original author's permission.

The goal is the original mod, unchanged: same meteors, crash behaviour, items, numbers, textures, sounds and translations.

> **Credits.** Falling Meteors and all its art, sounds and design belong to AlexDGr8r (textures also by Firecat1311 and SLuRsniper24). The original mod is *All Rights Reserved*.

## What's in it

- **Meteors** enter orbit at night near a random player and crash after a while. There are five types: Meteorite, Frezarite, Kreknorite, Unknown (a loot chest wrapped in meteorite) and the rare Kitty meteor shower.
  - A size 1–3 explosion leaves a crater lined with hot ore that cools over time; touching hot ore sets you on fire.
  - Alien Creepers come with Meteorite crashes, snow with Frezarite crashes, and Blazes with Kreknorite crashes. Big Kreknorite meteors build a Nether portal shrine.
  - Comets fall as well, and leave a few blocks of their material.
- **Meteor Shield**: charge it with a Meteor Chip and upgrade it with up to four Red Meteor Gems (64 blocks of range per level). It destroys incoming meteors and collects their materials, can block comets, and reports comets entering orbit.
- **Protected Land Tester**: lights up inside a protected area and tells you who owns it.
- **Detectors** (Proximity, Time, Crash): needles that point to incoming meteors and to the last crash zone.
- **Meteor Timer**: a redstone signal that grows as the next meteor gets closer (Power Mode / Quick Mode).
- **Freezer**: a machine with a 10-bucket fluid tank and four recipe modes. It makes ice, packed ice, obsidian, Frozen Iron and **Slippery Blocks** (four levels, made from almost any block or stairs).
- **Gear**:
  - Meteorite gear has Magnetization, which pulls items to you.
  - Frezarite gear: the tools work faster underwater, the sword slows enemies, the hoe makes pre-watered farmland, and the armor's Cold Touch freezes water as you walk.
  - Kreknorite: the full armor set makes you fire-immune, and the Flame Sword has Fire Aspect II.
- **Mobs**: the Alien Creeper (charged and fireproof) and the Comet Kitty (a tameable, fireproof cat in a space helmet).
- Meteor Summoners, ice cream, storage blocks, ores in world generation, 17 advancements (the old achievements) and 8 languages.

## Commands (op level 2)

| Command | What it does |
|---|---|
| `/meteor <x y z> <size> [delay] [type] [summoned]` | Spawns a meteor. Types: 0 Meteorite, 1 Frezarite, 2 Kreknorite, 3 Unknown, 4 Kitty |
| `/comet <x y z> [type] [delay]` | Spawns a comet |
| `/listMeteors [clear]` | Lists the meteors in orbit in this dimension |
| `/listShields` | Lists the Meteor Shields in this dimension |
| `/kittyattack` | Starts a Comet Kitty shower |

Game rules: `meteorsFall`, `summonMeteors`.

## Configuration

`<world>/serverconfig/meteors-server.toml` holds every option of the original config, including fall radius, meteor types, sizes, shield radius, explosion size, deterrence and whitelisted dimensions (`minecraft:overworld`, `minecraft:the_end`, `twilightforest:twilight_forest` by default). The server syncs it to clients.

## Differences from 1.7.10

These come from Minecraft itself changing, not from design choices:

- **Metadata became block states and separate items.** Hot or cool meteors use a `heat` state. Each Meteor Summoner and each decoration block is its own item. The Protected Land Tester has a wall variant like every torch.
- **Ore Dictionary names became Forge tags**, for example `forge:ingots/meteorite` and `forge:ores/frezarite`.
- **Achievements became advancements.**
- **Ore generation is data driven.** It lives in `data/meteors/worldgen`, with the same vein count, size and heights. Frezarite ore only spawns in cold biomes.
- **The Comet Kitty is based on the Cat**, because ocelots can't be tamed anymore.
- **Fire immunity**: Kreknorite armor cancels fire damage, since entities can no longer be made fire immune at runtime.
- **Cold Touch** freezes the block under your feet on the server. The 1.7.10 version froze client-side ghost blocks.
- **Not ported**: the Baubles, Waila and Thaumcraft integrations, since those mods don't exist on 1.20.1. The Baubles-only Magnetization Controller item goes with them.
- **Small fixes**:
  - The Freezer drops its contents when broken.
  - Meteors that fall into the void are removed.
  - The Meteor Timer keeps its signal correct after a reload.
  - A Frezarite crash no longer turns lava into packed ice.

## Building

Requires JDK 17.

```
./gradlew build          # jar in build/libs
./gradlew runClient      # test client
./gradlew runServer      # test server
./gradlew runClient -PquickPlay=<world>   # open a singleplayer world directly
```

---

### Português

Port do **Falling Meteors** (AlexDGr8r) do Minecraft 1.7.10 para o **1.20.1 Forge**, feito com a permissão do autor original, mantendo tudo igual ao original: meteoros, crateras, minérios, Meteor Shield, Freezer, detectores, Meteor Timer, armaduras e ferramentas, Alien Creeper, Comet Kitty, comandos, conquistas (agora *advancements*) e as 8 traduções.
