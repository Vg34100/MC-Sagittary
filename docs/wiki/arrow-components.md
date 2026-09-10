# Arrow Components

Sagittary is a modular arrow mod built around a custom fletching table workflow.

Each arrow is assembled from three components:

- Tip: controls damage and on-hit effects
- Shaft: controls flight behavior and some secondary effects
- Fletching: controls handling, recovery, or special flight traits

Crafting shape:

- 1 tip material
- 1 shaft material
- 1 fletching material
- Output: 6 arrows

The resulting arrow preserves its parts when fired from bows, crossbows, and dispensers.

## Current Parts

### Tips

- Flint: baseline arrow behavior
- Amethyst: heavier hit with a brief stun-like slowness effect
- Copper: chance to call down lightning on hit
- Optional Spelunkery tips: Ruby fire burst, Sapphire ice burst, Topaz prospecting pulse, Bronze knockback pulse, Electrum chain damage, and Invar armor-piercing heavy shots

### Shafts

- Stick: baseline shaft behavior
- Bamboo: much faster flight with slightly worse accuracy
- Blaze Rod: sets targets on fire

### Fletchings

- Feather: baseline fletching behavior
- Paper: faster flight, but the arrow breaks on block or ground impact instead of remaining stuck
- Phantom Membrane: gliding flight with reduced gravity and slightly lower speed

## Stacking Behavior

Component effects are meant to stack.

Examples:

- Copper + Blaze Rod + Feather: can inflict fire and also has a lightning chance
- Flint + Bamboo + Paper: behaves like a faster disposable arrow
- Amethyst + Stick + Phantom Membrane: keeps the brief stun-like hit effect while gliding farther

## Notes

- Phantom membrane flight was recently fixed so the client now matches the server trajectory instead of snapping near the end of flight.
- Crossbow support works through the same component-preserving arrow item/entity path as bow firing.
