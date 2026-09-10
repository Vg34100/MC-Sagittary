# Just Variants Campfires - Development Documentation

## Project Overview
Creating chest variants for every wood type in Minecraft 1.21.5 using ArchitecturyAPI (Fabric/NeoForge cross-platform).

## Current Progress Summary

### ✅ Completed Features
- Functional spruce chest with custom textures
- Functional spruce trapped chest with redstone functionality  
- Proper block entity registration using reflection (due to 1.21.5 BlockEntityType.Builder removal)
- Custom block entity renderers for both variants
- Working double chest mechanics with proper naming
- Recipe system integration
- Language file setup
- Fixed item model rendering (critical: models must be in `models/item/` folder)
- Fixed blockstate issues (needed waterlogged variants)

### 🎯 Current Architecture
- One block class per wood type (`SpruceChestBlock`, `SpruceTrappedChestBlock`)
- One block entity per wood type (`SpruceChestBlockEntity`, `SpruceTrappedChestBlockEntity`)  
- One renderer per wood type (`SpruceChestRenderer`, `SpruceTrappedChestRenderer`)
- Manual registration in `ObjectRegistry`

## Technical Requirements & Lessons Learned

### 🔧 Critical Technical Details
- **Item models MUST be in `models/item/` folder** - this was the key issue with missing textures
- **Blockstate files need waterlogged variants** - fixed double chest rendering issues
- **Block entity registration requires reflection in 1.21.5** - BlockEntityType.Builder was removed
- **Texture atlas registration needed** for custom chest textures (though we removed this successfully)
- **Proper parent class hierarchy required** for chest functionality

### 📁 Required File Structure
```
assets/modid/
├── blockstates/           # Block rendering states (needs waterlogged variants)
├── models/block/          # 3D block models  
├── models/item/           # Item inventory models (CRITICAL LOCATION!)
├── lang/                  # Translation keys
└── textures/entity/chest/ # Block entity textures

data/modid/
├── recipe/               # Crafting recipes
├── loot_table/blocks/    # Block drops
└── tags/                 # Block/item groupings
```

### 🐛 Key Debugging Discoveries
1. **Double chest texture issues** = blockstate file differences (not texture atlas problems)
2. **Missing item textures** = wrong file location (`models/item/` not `models/items/`)
3. **Overlapping missing texture models** = block model files had unnecessary texture definitions
4. **Item showing as `item.` instead of `block.`** = language file structure, but functionally works

## Goals & Scalability Analysis

### 🎯 Main Goal
Create chest variants for every wood type: oak, birch, spruce, jungle, acacia, dark_oak, mangrove, cherry, bamboo, crimson, warped + future wood types.

### 🚨 Current Architecture Issues
- **Code Duplication**: Each wood type requires 4+ classes
- **Manual Registration**: Each wood type needs manual ObjectRegistry entries  
- **Asset File Explosion**: Each wood type needs 6+ JSON files, textures, language entries
- **Maintenance Overhead**: Adding new wood types requires touching many files

## Recommended Improved Architecture

### 1. Generic Block System
Replace wood-specific classes with generic ones that take WoodType parameters:
```java
public class ModChestBlock extends ChestBlock {
    private final WoodType woodType;
    private final boolean isTrapped;
}
```

### 2. Enum-Driven Registration  
```java
public enum WoodType {
    OAK, BIRCH, SPRUCE, JUNGLE, ACACIA, DARK_OAK, 
    MANGROVE, CHERRY, BAMBOO, CRIMSON, WARPED;
}

static {
    for (WoodType wood : WoodType.values()) {
        registerChestPair(wood);
    }
}
```

### 3. Programmatic Asset Generation
Generate JSON files during build instead of manual creation.

## Datagen Possibilities

### ✅ Can be generated via Fabric Datagen
- Recipes (`RecipeProvider`)
- Loot tables (`LootTableProvider`)
- Language files (`LanguageProvider`)  
- Block/item tags

### ❓ Possible but Complex
- Blockstate JSON files (custom provider)
- Block/item models (custom provider)

### ❌ Cannot be generated via Datagen
- Block/BlockEntity/Renderer Java classes (compile-time requirement)
- Textures (need actual image files)

### 💡 Hybrid Approach
Custom build script generates JSON files programmatically during build, before datagen runs.

## Implementation Phases

### Phase 1: Refactor to Generic System
- Create `ModChestBlock(WoodType, isTrapped)`
- Create `ModChestBlockEntity(WoodType, isTrapped)`  
- Create `ModChestRenderer(WoodType, isTrapped)`
- Enum-driven registration loop

### Phase 2: Asset Generation System
- Build script for JSON generation (`scripts/generate-assets.java`)
- Generate blockstates, models, recipes programmatically
- Run before gradle build

### Phase 3: Datagen Integration
- `ModLanguageProvider` - auto-generate chest names
- `ModRecipeProvider` - auto-generate chest recipes  
- `ModLootTableProvider` - auto-generate loot tables

## Development Priority
1. **First**: Refactor to generic system (prevents code duplication)
2. **Second**: Asset generation system (reduces manual JSON work)
3. **Third**: Datagen integration (automates data files)
4. **Fourth**: Texture system optimization

## Current Working Code Structure

### Key Classes
- `ObjectRegistry.java` - Central registration with reflection-based BlockEntityType creation
- `SpruceChestBlock.java` - Custom chest block extending ChestBlock
- `SpruceTrappedChestBlock.java` - Trapped variant with redstone functionality
- `SpruceChestBlockEntity.java` - Block entity with custom naming
- `SpruceTrappedChestBlockEntity.java` - Trapped block entity with redstone signals
- `SpruceChestRenderer.java` - Custom renderer with spruce textures
- `SpruceTrappedChestRenderer.java` - Trapped chest renderer
- `Justvariants_campfiresClient.java` - Client-side renderer registration

### Key Files Working Correctly
- `/assets/justvariants_campfires/models/item/spruce_chest.json` - 3D item model
- `/assets/justvariants_campfires/blockstates/spruce_chest.json` - Block variants with waterlogged
- `/assets/justvariants_campfires/lang/en_us.json` - Translation keys
- `/assets/justvariants_campfires/textures/entity/chest/` - Custom chest textures

## ⚠️ CRITICAL: Generic Refactoring Lessons Learned

### 🔥 Major Issues Encountered During Generic System Implementation

**ALWAYS READ THIS SECTION BEFORE MAKING REGISTRY CHANGES!**

#### 1. **Circular Reference Hell** - The #1 Recurring Issue
**Problem**: `variable X might not have been initialized`
```java
// BROKEN - tries to reference BLOCK_ENTITY before it's initialized
BLOCK = register("block", () -> new Block(() -> BLOCK_ENTITY.get()));
BLOCK_ENTITY = register("entity", () -> new BlockEntityType(..., Set.of(BLOCK.get())));
```

**Root Cause**: Java static initialization executes top-to-bottom, can't reference variables declared later.

**✅ SOLUTION**: Register BlockEntityTypes with empty Set first, fix later
```java
// Register BlockEntityType with empty Set
BLOCK_ENTITY = register("entity", () -> new BlockEntityType(..., Set.of()));

// Register Block referencing the BlockEntityType  
BLOCK = register("block", () -> new Block(() -> BLOCK_ENTITY.get()));

// Fix valid blocks AFTER both are registered
public static void init() {
    // ... other registrations
    updateBlockEntityValidBlocks(); // Fix the empty Set via reflection
}
```

#### 2. **NullPointerException on BlockEntity Creation**
**Problem**: `Cannot invoke "BlockEntityType.isValid" because "this.type" is null`

**Root Cause**: BlockEntity constructor called with `null` BlockEntityType

**✅ SOLUTION**: Always pass BlockEntityType in constructor
```java
// BROKEN
public ModChestBlockEntity(WoodType wood, BlockPos pos, BlockState state) {
    super(null, pos, state); // NULL = CRASH!
}

// FIXED  
public ModChestBlockEntity(WoodType wood, BlockPos pos, BlockState state) {
    super(ObjectRegistry.SPRUCE_CHEST_BLOCK_ENTITY.get(), pos, state);
}
```

#### 3. **Invalid Block Entity State Validation Error**
**Problem**: `Invalid block entity X state at BlockPos, got Block Y`

**Root Cause**: BlockEntityType's `validBlocks` Set is empty or wrong

**✅ SOLUTION**: Update validBlocks field after registration
```java
private static void updateBlockEntityValidBlocks() {
    try {
        Field validBlocksField = BlockEntityType.class.getDeclaredField("validBlocks");
        validBlocksField.setAccessible(true);
        validBlocksField.set(CHEST_BLOCK_ENTITY.get(), Set.of(CHEST_BLOCK.get()));
    } catch (Exception e) {
        throw new RuntimeException("Failed to update BlockEntityType valid blocks", e);
    }
}
```

#### 4. **Generic Type Compatibility Issues**
**Problem**: `Supplier<BlockEntityType<ModChestBlockEntity>> cannot be converted to Supplier<BlockEntityType<? extends ChestBlockEntity>>`

**✅ SOLUTION**: Explicit cast to match expected bounds
```java
// BROKEN
return new ModChestBlock(wood, () -> BLOCK_ENTITY.get(), props);

// FIXED
return new ModChestBlock(wood, () -> (BlockEntityType<? extends ChestBlockEntity>) BLOCK_ENTITY.get(), props);
```

### 🚨 **NEVER DO THESE THINGS**

1. **❌ Reference registry entries in static initialization before they're declared**
2. **❌ Pass `null` as BlockEntityType to BlockEntity constructors**  
3. **❌ Create BlockEntityType with wrong/empty valid blocks Set**
4. **❌ Forget to cast generic types when dealing with bounded wildcards**
5. **❌ Try to `.get()` on RegistrySupplier during static initialization**

### ✅ **WORKING PATTERN FOR REGISTRY ORDER**

```java
static {
    // 1. Register BlockEntityTypes FIRST with empty valid blocks
    CHEST_BE = BLOCK_ENTITIES.register("chest", () -> {
        return new BlockEntityType(
            (pos, state) -> new ModChestBlockEntity(WOOD_TYPE, pos, state),
            Set.of() // EMPTY - will fix later
        );
    });
    
    // 2. Register Blocks referencing the BlockEntityTypes
    CHEST = BLOCKS.register("chest", () -> {
        return new ModChestBlock(WOOD_TYPE, () -> (BlockEntityType<? extends ChestBlockEntity>) CHEST_BE.get(), props);
    });
}

public static void init() {
    // 3. Fix valid blocks AFTER everything is registered
    updateBlockEntityValidBlocks();
}
```

### 🔧 **Debugging Checklist**

When you encounter crashes:

1. **Circular Reference**: Check initialization order, use empty Sets + reflection fix
2. **NullPointerException**: Verify BlockEntityType is passed to BlockEntity constructor  
3. **Invalid Block Entity**: Check BlockEntityType's validBlocks Set contains your block
4. **Generic Type Error**: Add explicit casts for bounded wildcards
5. **Startup Crash**: Check that no `.get()` calls happen during static initialization

## Current Working Generic Architecture (Updated)

### ✅ Successfully Implemented Generic System
- **`WoodType` enum** - Centralized wood type definitions with helper methods
- **`ModChestBlock`** - Generic chest block that works with any WoodType
- **`ModTrappedChestBlock`** - Generic trapped chest with redstone functionality  
- **`ModChestBlockEntity`** - Generic chest block entity with WoodType-based naming
- **`ModTrappedChestBlockEntity`** - Generic trapped chest entity with redstone signals
- **`ModChestRenderer`** & **`ModTrappedChestRenderer`** - Generic renderers that dynamically load textures based on WoodType
- **`ObjectRegistry`** - Updated to use generic classes with proper initialization order

### 🎯 Benefits Achieved
1. **Eliminated Code Duplication** - One set of classes now handles all wood types
2. **Simplified Future Expansion** - Adding new wood types only requires:
   - Adding enum entry to `WoodType`
   - Adding registration call to `ObjectRegistry` 
   - Creating texture files
3. **Maintained Compatibility** - Spruce chest still works exactly as before
4. **Cleaner Architecture** - Generic system is more maintainable

## Notes for Future Development
- Adding new wood types now only requires enum entry + registration call
- Generic system successfully eliminates class duplication
- Asset generation would eliminate manual JSON creation  
- Current generic system works and scales efficiently
- **CRITICAL**: Always follow the registry patterns documented above to avoid crashes
- Item model location was critical debugging discovery - remember for future blocks