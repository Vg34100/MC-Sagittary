# Sagittary — 16-target port handoff

## State

The original twelve targets are preserved. Added:

| Target | Java | Architectury | Loader | Fabric API | JEI |
| --- | --- | --- | --- | --- | --- |
| 1.20.1 Fabric | 17 | 9.2.14 | 0.19.5 | 0.92.12+1.20.1 | 15.71.0.224 |
| 1.20.1 Forge | 17 | 9.2.14 | 47.4.28 | — | 15.71.0.224 |
| 26.3 Fabric | 25 | 22.0.5 | 0.19.5 | 0.162.0+26.3 | 31.16.0.84 |
| 26.3 NeoForge | 25 | 22.0.5 | 26.3.0.76 / FML 12.0.12 | — | 31.16.0.84 |

`gradle/matrix/*.properties` remains authoritative. New optional accessory pins:
original Trinkets 3.7.2 on 1.20.1 Fabric; Trinkets Updated 4.2.1+26.3 on both
26.3 loaders. There is no Trinkets-family provider on 1.20.1 Forge.

Version remains **2.1.0**. These are development outputs, not replacements for
published releases. No commit, push, tag, merge or publication was performed.

## Implementation

- Existing ObjectRegistry, source trees and shared UI retained; native Forge
  bootstrap/client hooks added. Identifiers use the existing ModIds factory;
  existing property factories remain. A small shared bow API helper avoids
  repeated constructor/durability/ammo branches.
- Small native conditions handle ordinary drift. Separate implementations are
  limited to the NBT quiver/crossbows/enchantments/dispenser, legacy consumption
  mixins, and the split 26.3 Fabric hand-render lifecycle. Existing legacy
  screen/render/trade implementations are reused.
- Parsed resource transforms retain recipes, all arrow model variants and
  advancement predicates. The compatibility policy records the deliberate
  Ancient City reward fallback and unavailable Breeze Rod ingredient.
- Forge participates in discovery, artifact checks, public publication planning,
  and the existing Windows PortableMC packaged-client backend. CI provisions
  Java 17/21/25. Optional integration jars are not bundled. Existing Fabric
  production launch behavior is unchanged; no new launcher framework was added.

## Validation performed

| Target | Compile/package/artifacts | Packaged client | Packaged dedicated server |
| --- | --- | --- | --- |
| 1.20.1 Fabric | PASS | Not run | Not run |
| 1.20.1 Forge | PASS | PASS | PASS |
| 26.3 Fabric | PASS | PASS (see resource-only correction below) | PASS |
| 26.3 NeoForge | PASS | Not run | Not run |

After the narrow target checks, one complete `compileMatrix` + `packageMatrix`
invocation passed, followed by **16/16 artifact verification**. No full matrix
repeat was needed. Python tooling compiles; read-only publication discovery finds
the same sixteen targets and correct Forge relations. Workflow YAML parses.

Clients loaded the exact staged release JAR, proven by JVM class-origin logs,
without development source injection. 26.3 Fabric reached `startedCleanly:true`;
1.20.1 Forge completed initial reload/first-screen initialization and a stable
period. Each owned JVM was stopped. Forge used the existing pinned Windows
PortableMC 5.0.5 via WSL; no system-library changes were needed.

The 26.3 client startup preceded a resource-only Infinity predicate correction.
Dedicated-server registry loading exposed that schema mismatch; the corrected
Fabric/NeoForge artifacts passed verification and the corrected Fabric server
reached `Done`, saved, and exited 0. Client startup was not repeated for that
server-loaded data-only change. The Forge server also reached `Done`, saved, and
exited 0. Both server code origins point to their staged installable JARs.

Short console checks created component-arrow entities and a Fletching Table
block. The Forge arrow retained its slime tip and had nonzero flight motion.
Barrel serialization and a world save retained three component arrows plus the
selected prototype in both the old NBT and modern component formats. These were
command-authored fixtures, not proof of quiver insertion, menu crafting or
weapon firing. There was no GUI input automation or copied test world.

Client smoke included JEI and, on 26.3, Trinkets Updated. Matching local
Spelunkery artifacts for the tested new nodes were absent; the existing useful
warning/optional path worked. Servers ran without optional accessory/Spelunkery
integrations. JEI deprecation warnings were intentionally left alone.

## Manual verification still required

- Fletching Table menu/screen: normal, slime, potion and optional-material
  crafting; recipe/advancement unlocks and smithing tier/capacity preservation.
- Actually fire vanilla and Sagittary bows/crossbows, including magazine,
  damage/effects, recovery and enchantment behavior; inspect rendering.
- Quiver insertion/removal/capacity, save/reload, V and hover scrolling,
  tooltip/HUD agreement, survival consumption and creative selection.
- Supported equipped quivers, rebinding, and original Trinkets on 1.20.1 Fabric.
- Matching local Spelunkery present: JEI tips, material crafting/effects and
  Topaz targets. Absence was tested; presence on the new nodes was not.
- New Fabric/NeoForge loader-specific runtime paths not represented above.
  Back up worlds before testing 1.20.1 NBT → component-era save transfer;
  automatic Sagittary-specific conversion was not implemented/tested, and
  Minecraft world downgrades are not supported.
