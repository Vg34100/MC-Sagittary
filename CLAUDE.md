his # Claude Development Notes

## Project Overview: Amethyst Mod

**Technical Stack:**
- Minecraft 1.21.5
- Architectury API (multi-platform)
- Fabric + NeoForge support
- Java 21

**Core Concept: Component-Based Arrow System**
This mod implements a modular arrow crafting system where arrows are built from three components:
- **Tip**: Affects damage/effects (e.g., flint = default, amethyst = stun effect)
- **Shaft**: Affects flight properties (e.g., stick = default)
- **Fletching**: Affects accuracy/speed (e.g., feather = default)

**Current Implementation Status:**
- ✅ Component system (`ArrowComponent` enum with materials/types)
- ✅ `ComponentArrowItem` with DataComponentType storage (refactored from NBT)
- ✅ `ComponentArrowEntity` with component-based behavior and stacking effects
- ✅ Fletching table GUI for crafting component arrows
- ✅ Fletching table improvements: bulk crafting (64 stacks), 6 arrow output, better UI layout
- ✅ Dispenser support (`ComponentArrowDispenseBehavior`)
- ✅ Bow shooting works correctly with component preservation
- ✅ Crossbow compatibility (works automatically)
- ✅ Dynamic texture system for component visualization with JSON model selection
- ✅ Complete JSON generation via datagen for all component combinations

**Key Classes:**
- `ArrowComponent`: Enum defining all tip/shaft/fletching materials with effects/modifiers
- `ArrowParts`: DataComponentType record storing tip/shaft/fletching as strings  
- `ComponentArrowItem`: Item class handling ArrowParts component data
- `ComponentArrowEntity`: Entity with component-based effects/rendering and stacking behaviors
- `FletchingTableMenu/Screen`: Crafting interface with bulk support
- `ComponentArrowDispenseBehavior`: Dispenser compatibility
- `ModModelProvider`: Datagen for models and complete JSON generation

**Component Data Storage:**
Components stored as `ArrowParts` DataComponentType with CODEC serialization. Registered as `ARROW_PARTS` in ObjectRegistry.

**Current Components:**
- **Tips**: Flint (default), Amethyst (stun effect), Copper (10% lightning chance)
- **Shafts**: Stick (default), Bamboo (speed+/accuracy-), Blaze Rod (fire effect)  
- **Fletchings**: Feather (default), Paper (fast, destroyed on ground hit), Phantom Membrane (gliding)

## Finding Vanilla Minecraft Classes

When you need to examine vanilla Minecraft classes (like `ProjectileDispenseBehavior`, `AbstractArrow`, etc.):

1. **In Architectury/Loom projects**, vanilla MC classes are accessible through the remapped Minecraft jar
2. **Search locations:**
   - `.gradle/caches/minecraft/de-obfuscated/` directory
   - IDE navigation to `net.minecraft.*` packages
   - Use IDE "Go to Declaration" on MC class references in the code
3. **Alternative:** Ask the user to provide the decompiled source if searching fails

This avoids wasting time guessing at method signatures and ensures accurate implementation.

## Current Issues / Needed Fixes

**Copper Tip Lightning Bug:**
- Lightning strike causes brief game lag/freeze
- Need alternative lightning implementation method
- Current method: `EntityType.LIGHTNING_BOLT.create()` + `addFreshEntity()`
- Consider: Server-side only lightning, different lightning effect, or async spawning

## Future Component Ideas

**Potential New Tips:**
- **Diamond Tip**: Armor piercing, ignores some protection
- **Netherite Tip**: Fire immunity, can pierce shields  
- **Quartz Tip**: Perfect accuracy, increased range
- **Prismarine Tip**: Seeks nearest mob underwater
- **Obsidian Tip**: Can break blocks, pierces multiple enemies
- **End Crystal/TNT Tip**: Explosive on impact
- **Slime Ball Tip**: Bounces off surfaces instead of sticking

**Potential New Shafts:**
- **Iron Shaft**: Heavier, more damage but slower
- **Chorus Shaft**: Teleports target randomly on hit
- **End Rod Shaft**: Glows, infinite range in darkness
- **Bone Shaft**: Lightweight, chance to summon skeleton ally

**Potential New Fletchings:**
- **Dragon Breath**: Leaves poison cloud area (questionable thematic fit)
- **Shulker Shell**: Levitates target briefly

**Future Systems:**
- **Quiver System**: Crafted storage for different arrow types
- **Arrow Retrieval**: Some components make arrows return like boomerang
- **Component Combinations**: Special effects for certain material pairings

## Architectural Improvements Needed (Work In Progress)

**Problem**: Current component effects are hardcoded in ComponentArrowEntity with switch statements and if blocks. This doesn't scale well and clutters the entity class.

**Proposed Solution**: Refactor to component-based architecture where each material has its own effect class:

```java
// Instead of switch statements in entity class:
public interface ComponentEffect {
    void onEntityHit(LivingEntity target, ComponentArrowEntity arrow);
    void onBlockHit(BlockPos pos, ComponentArrowEntity arrow);
    void onTick(ComponentArrowEntity arrow);
    void addParticles(ComponentArrowEntity arrow);
}

// Each material gets its own class:
public class AmethystTipEffect implements ComponentEffect { ... }
public class BlazeRodShaftEffect implements ComponentEffect { ... }

// ArrowComponent enum would reference its effect class
// ComponentArrowEntity would dynamically call effects
```

**Benefits:**
- Cleaner, more maintainable entity class
- Easy to add new components without modifying entity
- Better separation of concerns
- More modular and extensible design
- Effects can be more complex without cluttering main class