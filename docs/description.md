# Sagittary (Or Fletcher's Workshop)

Sagittary expands Minecraft archery with a modular arrow system built around the fletching table.
(Still in beta, I'm not good at textures sorry guys)

Instead of crafting only one standard arrow, you can combine different parts to create arrows with different flight profiles, utility, and combat effects. Tips, shafts, and fletchings each matter, and their properties stack together.

![Component Based Arrows](https://cdn.modrinth.com/data/kh5dbjUL/images/3e995544e942bbbd0554ff75d309e27b8abc74a4.png)

## What's New in 2.0

Version 2.0 is a major expansion:

- **10 arrow tips** (up from 3) with new effects like bouncing, piercing, teleportation, and sonic booms
- **5 arrow shafts** (up from 3) including homing breeze rod and smite-dealing bone
- **Quiver** - stores 256 arrows, auto-feeds to bows, scroll to select arrow type
- **Iron Bow & Iron Crossbow** - craftable upgraded weapons with increased durability
- **Compound Bow** - shoots 3 arrows in a spread (found in Trial Chambers)
- **Repeater Crossbow** - rapid-fire magazine crossbow (found in Ancient Cities)
- **JEI Integration** - browse all component effects and recipes

> **Note:** Version 2.0 is currently only available for Minecraft 26.1.2. For 1.21.1, use version 1.x.

## Features

- Turns the fletching table into a functional arrow assembly station
- Lets you combine a tip, shaft, and fletching into a custom arrow
- Produces 6 arrows per craft
- Works with bows, crossbows, and dispensers
- Supports stacked behaviors from multiple arrow parts
- Quiver system for arrow storage and quick-switching
- New bows and crossbows with unique behaviors
- Available on Fabric and NeoForge

## How It Works

![Crafting Arrows](https://cdn.modrinth.com/data/kh5dbjUL/images/c64a7db1efe6b81e1832775c1db9f5568b7d2fdb.png)

Each arrow is built from three components:

- Tip: controls damage and special hit effects
- Shaft: controls flight behavior and secondary traits
- Fletching: controls handling, glide, or recovery behavior

The current system is designed so component effects work together instead of replacing each other.

Examples:

- A copper blaze arrow can both ignite targets and roll for lightning
- A diamond breeze arrow homes in on enemies and pierces through multiple targets
- A slime arrow bounces off surfaces instead of sticking
- An ender pearl arrow teleports you or your target on impact

## Current Components

### Tips

| Tip | Effect |
|-----|--------|
| Flint | Standard arrow behavior |
| Amethyst | Applies brief stun (slowness + reduced knockback) |
| Copper | 10% chance to call lightning on hit |
| Slime | Bounces off blocks instead of sticking |
| Glowstone | Applies glowing effect (like spectral arrows) |
| Echo Shard | Sonic boom AOE damage on impact |
| Ender Pearl | Teleports target; block hits teleport the shooter |
| Iron | Heavy arrow with 30% armor piercing |
| Gold | 35% chance for critical damage (1.5x) |
| Diamond | Pierces through up to 4 enemies |

### Shafts

| Shaft | Effect |
|-------|--------|
| Stick | Standard shaft behavior |
| Bamboo | Faster flight with reduced accuracy |
| Blaze Rod | Sets targets on fire |
| Breeze Rod | Homing arrow with reduced gravity and strong knockback |
| Bone | Smite effect (2.5x damage to undead) |

### Fletchings

| Fletching | Effect |
|-----------|--------|
| Feather | Standard fletching behavior |
| Paper | Faster flight, breaks on block impact |
| Phantom Membrane | Reduced gravity for gliding flight |

## New Weapons

### Quiver
Craft with leather and string. Stores up to 256 arrows. Bows automatically pull from quivers in your inventory. Scroll while holding to select arrow type. Right-click in inventory to add/remove arrows.

### Iron Bow & Iron Crossbow
Craftable upgrades with 1.5x durability and slightly increased range.

### Compound Bow
Found in Trial Chamber reward chests. Shoots 3 arrows in a spread pattern, consuming 3 arrows per shot.

### Repeater Crossbow
Found in Ancient City chests. Magazine-fed rapid-fire crossbow. Load 5 arrows, then fire 3 per shot.

## Why This Mod

Vanilla gives you one arrow and one decorative workstation.

Sagittary makes the fletching table matter and turns arrows into something you can actually build around. It is meant to make ranged combat feel more tactile, more experimental, and more expressive without turning the system into a giant tech tree.

## Loader / Version

**Version 2.0:**
- Minecraft 26.1.2
- Fabric
- NeoForge

**Version 1.x:**
- Minecraft 1.21.1
- Fabric
- NeoForge

## Notes

- Existing arrows created before major internal changes may not automatically pick up newer visual metadata
- The mod is currently centered on archery and the fletching table rather than broader content expansion
- JEI integration shows all component effects when you hover over items in the Arrow Components category
