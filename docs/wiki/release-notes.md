# Release State

Current target baseline:

- Minecraft 26.1.2
- Architectury
- Fabric and NeoForge
- Java 25

## 2.1.0 Development

- Sagittary 2.x is now available on Fabric and NeoForge for Minecraft 1.21, 1.21.1, 26.1, 26.1.1, 26.1.2, and 26.2.
- Quivers keep one persistent selected arrow across inventory scrolling and the rebindable quiver-cycle key. Selection, tooltips, and projectile lookup use the same state across supported versions.
- Optional accessory support uses original Trinkets on legacy Fabric and Trinkets Updated on 26.x; Spelunkery and JEI integrations remain optional.
- Installable release JARs now receive matrix artifact verification and representative production-client startup checks before publication.

## 2.0.1 Development

- Quiver progression now includes the base Quiver, Hunter's Quiver, and Ranger's Quiver.
- Hunter and Ranger upgrades use smithing templates; the Ranger template appears in Trial Chamber rewards.
- Sagittary recognizes optional Trinkets Updated chest/back quivers and optional Spelunkery materials.
- Special thanks to **ODY** for requesting the expanded quiver features and examples that shaped this update.

## What Is Working

- Component-based arrow item and entity flow
- Fletching table arrow assembly UI
- Six-arrow output crafting flow
- Bows preserve arrow components when firing
- Crossbows work with component arrows
- Dispensers fire component arrows
- Generated item models for all current component combinations

## Known Issues

- Copper lightning can cause a noticeable hitch when the lightning entity is spawned
- The repo does not yet have polished user-facing release docs outside this wiki directory

## Release Readiness

The mod looks close to a first public release if the goal is a focused arrow-system release.

Before releasing, verify:

1. The fletching table flow is stable in normal survival play.
2. All current component combinations render and fire correctly.
3. Copper lightning behavior is acceptable or intentionally deferred as a known issue.
4. Loader-specific smoke tests still pass on both Fabric and NeoForge.
