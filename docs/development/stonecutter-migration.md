# Sagittary Stonecutter Migration

This is the working migration guide for converting Sagittary from its current single-version Architectury build into a Stonecutter version matrix.

This file records architecture and migration rules. It should not become a log of every compiler error.

## Goal

Maintain one branch that can build Sagittary for these initial targets:

- 1.21 Fabric / NeoForge
- 1.21.1 Fabric / NeoForge
- 26.1 Fabric / NeoForge
- 26.1.1 Fabric / NeoForge
- 26.1.2 Fabric / NeoForge
- 26.2 Fabric / NeoForge

The exact target set is defined by the repository, not by agent memory. Once present, `gradle/matrix/*.properties` is the dependency/version source of truth.

## Current Baseline

Before the matrix conversion, Sagittary is a working 26.1.2 Architectury-style project with `common/`, `fabric/`, and `neoforge/`, Java 25, Loom no-remap, Architectury API, JEI, optional compatibility hooks, and Shadow packaging.

The current source should remain the canonical maintained implementation unless a later migration result gives a strong reason to change that rule.

## Target Architecture

Use the same high-level matrix shape across the mod suite:

```text
settings.gradle
stonecutter.gradle
build.matrix.gradle
gradle/matrix/
  <minecraft-version>-fabric.properties
  <minecraft-version>-neoforge.properties

common/
fabric/
neoforge/

docs/development/
```

### `settings.gradle`

- registers Stonecutter;
- defines version/loader nodes;
- sets any platform property needed before project evaluation.

### `stonecutter.gradle`

- selects the IDE-active target;
- defines aggregate compile/package/launch-setup tasks.

### `build.matrix.gradle`

Keep this mostly generic.

It should load target properties, choose Fabric vs NeoForge, choose legacy vs current Loom generation, configure Java/toolchains, dependencies, source/resource roots, metadata expansion, isolated run directories, and release artifact locations.

Do not turn this file into a large library of Java source rewrites.

### `gradle/matrix/*.properties`

Each target file pins only data that differs by target, such as Minecraft version, loader, Java version when needed, Loom generation, Architectury API, Fabric Loader/API, NeoForge, JEI, and other version-bound dependencies.

Do not keep a second active set of dependency pins in root `gradle.properties`.

### Canonical source trees

`common/`, `fabric/`, and `neoforge/` remain the main source/resource trees.

Small source differences should stay close to their source using the compatibility policy.

## Compatibility Strategy

Read `compatibility-policy.md` before adding a compatibility mechanism.

Default order:

1. unchanged shared source;
2. native Stonecutter conditional for a small local API difference;
3. small deterministic replacement for a mechanical rename;
4. resource/data transform for a schema difference;
5. separate compatibility implementation for a materially different class/system.

Do not begin by creating dozens of legacy copies.

Do not begin by writing broad regex rules that rewrite Java.

## Migration Phases

### Phase 0 — Baseline

The pre-Stonecutter 26.1.2 Fabric and NeoForge baseline is already known to work.
Check `git status --short` and preserve unrelated edits. Rebuild the old layout
only when a migration regression requires comparison. Validate both canonical
targets through the new matrix in Phase 1.

The migration should not silently repair unrelated gameplay bugs.

### Phase 1 — Matrix skeleton

Create only the build structure:

- Stonecutter settings;
- aggregate tasks;
- target properties;
- central matrix build script;
- version-aware metadata expansion;
- isolated run directories.

Do not port gameplay APIs yet.

The first required result is that the unchanged canonical 26.1.2 Fabric and NeoForge targets still compile/package under the matrix.

### Phase 2 — Nearby modern boundary

Use 26.2 as the first source-compatibility test.

For each 26.2 failure:

1. inspect the exact target API;
2. classify the difference using `compatibility-policy.md`;
3. use the smallest mechanism;
4. compile again after batching related fixes.

Do not add a general replacement because one class changed.

### Phase 3 — Legacy pilot

Use 1.21.1 as the first legacy source pilot, then verify the same area on 1.21.

Start with Fabric, then NeoForge, while keeping loader-only differences separate.

The purpose is to learn which Sagittary systems fit inline Stonecutter conditions, resource transforms, or separate compatibility implementations.

### Phase 4 — Fill adjacent targets

Once the main compatibility groups are proven:

- expand 26.x-compatible rules to 26.1 / 26.1.1 as applicable;
- expand legacy rules from 1.21.1 to 1.21 only when the API is actually shared;
- keep version-specific exceptions explicit.

Do not assume adjacent target behavior solely from compilation.

### Phase 5 — Full matrix

Only after the main source and resource groups work:

```bash
python build-smart.py matrix:compile
python build-smart.py matrix:package
python build-smart.py matrix:server
```

Then run runtime smoke tests according to the acceptance checklist.

## Initial Sagittary Risk Inventory

Likely compatibility boundaries include:

- `FletchingTableMenu`, `FletchingTableMenuProvider`, `FletchingTableScreen`
- JEI fletching/component categories
- `QuiverItem`, bows, crossbows, component arrows
- projectile weapon mixins
- quiver cycling/networking
- custom arrow entities
- topaz pulse rendering
- tooltip rendering
- item-in-hand rendering mixin
- arrow recovery, bundle contents, bundle mouse actions, fletching table, and player projectile mixins
- JEI, Trinkets, Spelunkery, and Fabric data generation

This list tells the agent where to inspect when an error points there. It is not permission to open or rewrite all of these files at migration start.

## Metadata

Loader metadata must become target-aware.

Do not leave one hard-coded Minecraft version, Java requirement, Architectury requirement, or NeoForge requirement.

Process metadata from the active matrix target and inspect processed output when debugging loader resolution.

## Java

Treat the Gradle JVM separately from the game/toolchain JVM.

Expected generation rule:

- current 26.x targets: Java 25;
- legacy 1.21/1.21.1 targets: Java 21 game/toolchain where required.

Do not globally force Gradle to Java 21 because a legacy target needs Java 21.

Use the adaptive `build-smart.py` only after its print-plan selects the intended Gradle Java.

## Runtime Directories

Every generated target must use its own run directory.

Do not share run directories across versions or loaders.

## Release Artifacts

The build must distinguish development artifacts from installable release artifacts.

Legacy remap-era targets may require a remapped release jar. Current targets use the appropriate current artifact path.

The aggregate package task must choose the correct artifact task per generation.

## Context-Efficient Work Order

For each failure group:

1. read the concise compiler/runtime error;
2. search Sagittary source for the affected symbol;
3. open only that source and its immediate dependencies;
4. inspect the exact target vanilla/loader API when needed;
5. choose the compatibility mechanism;
6. patch related errors together;
7. run the narrowest useful check;
8. record a reusable compatibility rule only if it will matter again.

Avoid repeated full-matrix builds while one sentinel is still broken.

## What Belongs in This Doc

Record matrix architecture changes, compatibility categories, target-specific dependency requirements, non-obvious loader/version boundaries, and decisions a future agent would otherwise have to derive again.

Do not record raw compiler logs, one-off typo fixes, long copies of Java code, or every file edited during the port.

## Completion

Use `stonecutter-port-acceptance-checklist.md`.

A compile-only matrix is not a complete port.

## Implemented Build Architecture

- `settings.gradle` derives target registration from the matrix property files,
  validates their names, and selects NeoForge's Loom platform before evaluation.
- `stonecutter.gradle` selects `26.1.2-fabric` for the IDE and derives aggregate
  compile/package/launch-setup tasks from the same matrix.
- Each standalone node compiles common plus loader source, so Shadow and the
  old common-to-loader project dependency are no longer needed. Architectury API
  remains a dependency, but its common-project Gradle transformation plugin is
  unnecessary. Loader dependencies are external; optional integrations are not
  bundled.
- The native Stonecutter preparer emits only changed files. A small generic Sync
  adapter merges this cache over unchanged canonical sources before compilation.
  An empty source view is an error. No Java text mutations are performed by Gradle.
- Release jars live under `build/libs/<target>/`. Legacy targets use `remapJar`;
  unobfuscated targets use `jar`. Runtime directories are `runs/<target>/<run>`.
- Both Loom plugin IDs use 1.17.493; per-target remapping mode distinguishes the
  generations. Conflicting versions of the same plugin implementation in one
  Gradle classloader are not a reliable way to select an older implementation.
- Inactive IDE modules use distinct processed source roots. The active modern
  node uses canonical Java; legacy IDE views use processed roots to include the
  structural overrides. Edit canonical/compat sources, not generated views.
  Native merge paths are repository-relative. IDE switching is not yet tested.
- Checked-in Fabric datagen resources are used by both loaders, with canonical
  resources taking priority over identical generated duplicates.

On this WSL host use `python3`, a Java 25 Gradle JVM, and, for sentinel checks,
`--configure-on-demand`. The existing project cache encountered an I/O error;
`--project-cache-dir <native-temp-cache>` avoids it. Java 21 is
selected by legacy toolchains independently of the Gradle JVM.

### Current Verified State

All twelve registered targets pass aggregate compilation, release packaging,
and launcher/toolchain verification. Java 21/25 class versions, loader metadata,
optional dependency status, packaged mixin classes, remapped Fabric references,
and legacy resource formats are checked with:

```bash
python3 scripts/verify-matrix-artifacts.py
```

| Targets | Compile/release package/launch setup | Runtime evidence |
| --- | --- | --- |
| 1.21 Fabric + NeoForge | Pass | Not launched |
| 1.21.1 Fabric | Pass | Title/reload, integrated-world Fletching Table crafting, networked quiver selection, Iron Bow firing/consumption, and JEI recipe viewer. |
| 1.21.1 NeoForge | Pass | Title/reload, integrated-world gameplay, JEI initialization, and clean saved-world/client shutdown; details below. |
| 26.1 Fabric + NeoForge | Pass | Not launched |
| 26.1.1 Fabric + NeoForge | Pass | Not launched |
| 26.1.2 Fabric + NeoForge | Pass | Fabric datagen passes with temporary output. Fabric server exits for EULA before readiness; **not a server pass**. NeoForge not launched. |
| 26.2 Fabric | Pass | Rendered title screen and resource reload with JEI before final removal of the unnecessary Gradle transformation plugin; that final launch configuration is not rechecked. |
| 26.2 NeoForge | Pass | Title/reload, integrated-world crafting/quiver/weapon/networking gameplay, and both JEI categories; details below. |

No dedicated server reached a wrapper-confirmed ready-and-clean-stop result.
The isolated server has `eula=false`; acceptance was requested but not authorized.
Client runs were scoped development checks, not external release-jar tests, and
clean client shutdown was not established for every run. WSL emitted missing
Vulkan/cursor warnings; 26.2 used OpenGL successfully. Dev-account authentication
warnings are unrelated to Sagittary. Architectury 21.0.7 produces a deprecated
`logoFile` warning on NeoForge; Sagittary itself uses `iconFile` on 26.2.

An unnecessary Architectury Gradle transformation layer initially left non-daemon
executor pools alive after 26.2 NeoForge Quit, followed by a shutdown-watchdog
classloading failure. Removing that plugin removes its runtime/agent jars and
those pools; native Loom still loads the saved world, JEI, and Fletching Table.
The subsequent hardware-rendered WSL run saves all dimensions but exits with
native `SIGSEGV` in `libc` (process status 134; `hs_err_pid1732969.log` in its
ignored run directory). This is **not a clean client/Gradle shutdown pass**.
No Sagittary Java exception is reported. The exact native root cause and an
external-release reproduction remain unresolved; do not attribute it to a
specific dependency without further isolation.

A scoped software-renderer control (`LIBGL_ALWAYS_SOFTWARE=1
GALLIUM_DRIVER=llvmpipe`) reaches the 26.2 NeoForge title screen and quits with
Gradle success. This supports a WSL hardware graphics-path issue; it does not
establish the exact failing library. No graphics flags were added to the matrix.
The software control did not repeat the integrated-world gameplay checks.

Representative **1.21.1 NeoForge** gameplay checked:

- The Fletching Table opens, renders, consumes its inputs, and crafts six
  Amethyst Arrows with the expected model and component tooltip.
- Quiver inventory insertion stores two arrow types; the tooltip shows the
  contents/capacity/equipped arrow. Holding V and scrolling changes the selected
  type through the client-to-server payload and synchronized contents.
- Iron Bow firing spawns a custom arrow. Survival-mode quiver ammunition falls
  from four to three. Iron Crossbow loading consumes another arrow, stores its
  charged projectile, then fires a new custom arrow and clears the charge.
- JEI registers Sagittary categories/recipes without errors with Spelunkery
  absent. Full recipe-viewer interaction and installed optional mods remain
  untested. Resource reload confirms the upgrade-model parent loops are gone.

The same representative checks also pass on **26.2 NeoForge**: Fletching Table
crafting and component tooltips/models, two-type quiver insertion and networked
selection, Iron Bow firing/consumption, and Iron Crossbow loading/firing a crafted
Amethyst Arrow. Both JEI Fletching Table and Arrow Components recipe viewers open
without errors with Spelunkery absent. This is limited feature coverage, not a
test of every arrow effect or weapon/enchantment combination.

**1.21.1 Fabric** also passes opening/crafting in the legacy Fletching Table,
inserting two arrow types into a quiver and cycling them through Fabric's raw
input/network bridge, Iron Bow custom-arrow spawning and survival consumption,
and opening the JEI Fletching Table recipe viewer. The world saves and the client
exits with Gradle success. This checks the updated legacy mixin path on both
loaders; it is not a release-jar launch.

**26.1.2 Fabric datagen** runs all four registered providers and exits with Gradle
success. The existing minimal provider emits two quiver JSON files; output was
redirected to a new temporary directory, leaving checked-in assets unchanged.
Apply run-directory isolation after `configureDataGeneration`, because its setup
otherwise replaces the datagen run's directory. Other targets' datagen execution
is unverified; legacy datagen Java compiles but no legacy datagen run is configured.

Release acceptance still requires dedicated-server smoke tests, broader arrow
effects/weapon/quiver/menu/tooltip/networking gameplay across generations, Topaz
rendering, fuller JEI behavior, optional integration checks, other-target datagen,
IDE switching, and external-launcher testing of release jars. No external
launcher was used in this session.

### Runtime discoveries

- 26.2 Fabric's hand-render redirect moved to `submitArmWithItem`.
- Fabric Loader 0.19.4's annotation ABI conflicts with bundled MixinExtras 0.5.4;
  26.2 Fabric now uses 0.19.5, which bundles the fix. NeoForge retains its older
  compile annotation pin. See the compatibility policy's runtime dependency rule.
- JEI 30.32.0.209 requires NeoForge 26.2.0.67 or later. Compilation and launch
  preparation do not detect this runtime mod-dependency constraint.
- Legacy `EntityType.Builder.build(String)` merely checks for a DFU schema and
  discards the returned type. Use `minecraft:arrow` for this check to avoid an
  unregistered-schema error; the registry supplier still registers the actual
  entity as `sagittary:component_arrow`, with saving enabled.
- Legacy Fletching Table interaction must inject into its overriding subclass,
  not the base `BlockBehaviour`. This was found by actually opening the menu.
- JEI rejects air catalysts from absent optional materials; filter unavailable
  components in shared Java rather than requiring Spelunkery.
- Flat legacy item definitions referencing their own model retain its existing
  parent/textures. A self-parenting transform passed compilation but produced
  runtime resource warnings; release inspection now rejects it.

## Focused Regression Pass

Quiver selection now belongs to Sagittary's persistent, synchronized
`quiver_selection` data component. It identifies an arrow type independently of
bundle order. Both hover-scroll and the registered, rebindable cycle key request
server-side selection through the same payload. Modern tooltips receive a
visual bundle-selection mirror; legacy tooltips highlight the corresponding grid
cell. Gameplay lookup and ammunition removal read only Sagittary's selection.

### Optional development runtimes

`gradle/dev-integrations.gradle` attaches optional mods only to Loom's local
runtime configurations. They are not required compile dependencies, published
runtime dependencies, or bundled release jars. Release metadata remains optional
and is target-aware; the artifact verifier rejects embedded optional-mod jars
and classes.

- 26.1 / 26.1.1 / 26.1.2, both loaders: Trinkets Updated **4.0.1+26.1**. The
  published version and both loader metadata files explicitly cover 26.1.x.
- 26.2, both loaders: Trinkets Updated **4.1.1+26.2**.
- 1.21 / 1.21.1 Fabric: original Trinkets **3.10.0**, with its bundled Cardinal
  Components dependencies and a small reflection/data-driven back-slot bridge.
- 1.21 / 1.21.1 NeoForge: no Trinkets-family development dependency or support
  declaration. Adding a different accessory API would be separate work.

Pins remain in the matrix. Their source metadata is available from the official
[Updated releases](https://modrinth.com/mod/trinkets-updated/versions) and
[original releases](https://modrinth.com/mod/trinkets/versions).

Spelunkery resolution checks `build/libs/<target>/` under, in precedence order,
`spelunkery_dev_root` (Gradle property or ignored `gradle.local.properties`),
`SPELUNKERY_DEV_ROOT`, or the auto-detected `../spelunkery` sibling. It excludes
sources/development archives and validates target loader metadata. Exactly one
matching release is attached; missing or ambiguous artifacts produce one visible
warning when the affected game run starts, not a configuration/compile failure.
This machine has matching sibling jars for every matrix node. No sibling files
are modified by Sagittary.

Use `-Pdev_integrations=false` to test without both optional integrations,
`-Pspelunkery_dev=false` or `-Ptrinkets_dev=false` to exclude only one. An absent
Spelunkery path is also a useful resolver-warning test. Present AND absent runs
are required before claiming optional integration acceptance.

The per-target `verifyDevIntegrations` task resolves the optional development
jars and uses the same Spelunkery diagnostic as game launches, without starting
Minecraft. For example:

```bash
python build-smart.py :26.2-fabric:verifyDevIntegrations
python build-smart.py :26.2-fabric:verifyDevIntegrations -Pspelunkery_dev_root=../missing-spelunkery
python build-smart.py :26.2-fabric:verifyDevIntegrations -Pdev_integrations=false
```

This verifies dependency configuration and warnings, not absent-mod gameplay.

Regression gameplay acceptance follows the user-defined A–I checks: Fletching
(normal/slime), vanilla and Sagittary weapons, smithing upgrades, quiver contents
and both selection paths (survival/creative), Controls/rebinding, JEI, supported
accessories, and Spelunkery crafting/effects. Build and startup results alone do
not satisfy those checks.

### Regression verification (2026-10-01)

- Final four-sentinel compile, full twelve-target compile, full twelve-target
  package, and `scripts/verify-matrix-artifacts.py`: **pass**. The verifier checks
  installable legacy remapped jars/current jars, target metadata, mixin classes,
  transformed resources, and the absence of embedded optional-integration
  jars/classes.
- Full `matrix:server` launch-setup/toolchain verification: **pass**. This does
  not start Minecraft and is not a ready-and-clean-shutdown server smoke test.
- Default optional-runtime resolution: **pass on all twelve nodes**, including
  the matching local Spelunkery release jar and only the supported Trinkets
  provider. Missing-repository tests pass on both 26.2 loaders; missing-artifact
  tests pass on both 1.21.1 loaders, with one useful warning per target and
  successful launch preparation. No sibling Spelunkery files were changed.
- With `dev_integrations=false`, all four sentinels resolve no optional
  development jars and pass launch-setup verification. This is configuration
  evidence only, not an absent-mod Minecraft runtime pass.
- Accepted client evidence is limited to **26.2 Fabric** startup with JEI,
  Spelunkery and Trinkets Updated present; Spelunkery materials visible in
  Sagittary/JEI; the Sagittary Controls entry and persisted rebinding. These are
  partial **F/G/I** checks, not full feature-acceptance passes.
- Hover scrolling, actual arrow firing, creative selection, accessories,
  consumption, smithing, and Spelunkery effects are **MANUAL VERIFICATION
  REQUIRED**, as are absent-mod game startup and representative external
  release-jar launches. No new dedicated-server readiness pass is claimed.
- Existing JEI deprecation warnings are intentionally retained during this
  focused closeout; no JEI API rewrite was attempted just to remove warnings.
  The local Spelunkery artifact also emits its own resource/recipe warnings on
  26.2; material visibility does not establish that those upstream recipes work.

WSL validation used disposable Linux-side node intermediates/project caches to
avoid mounted-drive Gradle permission errors. Release jars still go to the
normal target-specific `build/libs/<target>/` directories; no host-specific
paths or interactive test hooks were added to the project configuration.

### Manual regression acceptance (A–I)

Use representative **26.2 Fabric / NeoForge** and **1.21.1 Fabric / NeoForge**
clients. Interactive automation is intentionally stopped; the following remain
manual checks, not inferred passes from compilation or tooltip screenshots.

- **A — Fletching:** craft normal and slime arrows in the Fletching Table.
- **B — Vanilla weapons:** actually fire selected component arrows using a
  vanilla bow and crossbow.
- **C — Sagittary weapons:** repeat with Sagittary bows and crossbows.
- **D — Upgrades:** smith quiver upgrades; check the resulting tier/capacity.
- **E — Quiver:** insert two visibly distinct arrow types. Select each with both
  hover-scroll and the cycle key plus scroll; fire after each selection. Check
  tooltip/HUD agreement in survival and creative, correct consumption versus
  no consumption, persistence after reopening/reloading, insertion/removal,
  selected-stack depletion, and capacity on each tier.
- **F — Controls:** rebind the registered key, confirm the new key actually
  cycles arrows, and restore the preferred binding. Saved rebinding alone is
  not evidence that input handling honors it.
- **G — JEI:** inspect normal and Spelunkery component recipes/categories with
  integrations installed; repeat without optional mods and reject AIR entries.
- **H — Accessories:** on supported targets equip a back-slot quiver, cycle its
  selection, fire from it, and check survival consumption. Legacy NeoForge has
  no Trinkets-family provider. Also launch supported targets without Trinkets.
- **I — Spelunkery:** craft/fire a representative material arrow and confirm its
  effect; check Topaz/prospector targets where practical. Also launch without
  Spelunkery and confirm Fletching/JEI remain valid.
