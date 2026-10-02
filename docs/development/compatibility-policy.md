# Sagittary Multiversion Compatibility Policy

Use this file when deciding how a Minecraft-version difference should be represented in Sagittary.

The goal is low duplication without hiding important behavior inside build-script source rewriting.

## Decision Order

### 1. No compatibility code

If the same source works on all affected targets, keep it unchanged.

Do not add a conditional for a difference that has not been observed.

### 2. Native Stonecutter source conditional

Use for a small local difference where both variants are easy to understand beside each other.

Good cases:

- import/package move;
- method rename;
- one changed argument;
- a short version-specific call;
- one annotation or interface difference;
- a small loader/version branch.

Prefer this when the alternate block is short and the main behavior stays the same.

Do not let one Java class become dominated by version comments.

### 3. Small deterministic replacement

Use only for truly mechanical drift.

Requirements:

- narrow scope;
- deterministic input/output;
- no behavior change;
- easy to assert that the replacement matched;
- not dependent on fragile formatting where possible.

Do not use broad regex as a substitute for understanding the target API.

### 4. Resource/data transform

Use build-time transforms when behavior is the same but the serialized format changed.

Possible examples include recipe ingredient representation, loader metadata fields, item definition shape, and model/resource schema changes.

Rules:

- operate on parsed data when practical;
- scope the transform to the affected target generation;
- preserve unaffected fields;
- verify processed resources;
- package and run a visual/gameplay check where relevant.

### 5. Separate compatibility implementation

Use a real Java/resource file when old and new versions need materially different implementations.

Good reasons:

- renderer architecture changed;
- menu/screen API is structurally different;
- entity model or render-state design changed;
- a whole subsystem has different lifecycle rules;
- an inline conditional would cover a large part of the class.

Keep this area small.

Possible shape:

```text
gradle/compat/
  legacy/
    common/
    fabric/
    neoforge/
```

Do not copy the whole canonical source tree.

Only override files that actually need a separate implementation.

## Avoid: Generated Java Hidden in Gradle

Do not make the normal solution:

```text
build.matrix.gradle
  -> read Java as text
  -> many regex replacements
  -> inject multiline Java strings
  -> compile generated Java
```

This can be acceptable for one tiny verified mechanical change, but it should not become Sagittary's source architecture.

A future agent should be able to find version behavior by reading Java source or a clearly named compatibility source file.

## When to Split a Class

Split rather than add more inline conditions when:

- version blocks are repeated throughout the file;
- old and new implementations use different lifecycle models;
- a large part of the class has alternate behavior;
- conditional code becomes harder to read than two explicit implementations;
- mixin targets/descriptors are completely different;
- implementations need different imports/types across most of the file.

There is no fixed line-count rule.

## When Not to Split

Do not create a second file just because one import moved, one method was renamed, one constructor added an argument, one constant changed name, or one registration call has a short old/new form.

Those are main use cases for Stonecutter conditions.

## Loader Differences

Minecraft-version and loader differences are separate axes.

If only NeoForge differs, keep that behavior in NeoForge source where possible.

If only Fabric differs, keep it in Fabric source.

Do not put Fabric-vs-NeoForge branches into common source when loader source can own the difference cleanly.

## Mixins

For mixins, correctness matters more than avoiding one extra file.

A mixin may need a version-specific target, descriptor, implementation, or omission on a target where the hooked behavior does not exist.

Small target/method differences can use Stonecutter conditions.

A fully different injection should usually be a separate compatibility mixin rather than a long set of inline changes.

## Compatibility Rule Log

When a reusable difference is proven, record it in this form:

```text
Boundary:
Affected targets:
Affected files/system:
Mechanism:
Reason:
Validation:
```

Do not record speculative rules.

## Current Pilot Rule

For the Sagittary pilot:

- test native Stonecutter conditions first for small Java drift;
- use parsed resource transforms for format drift;
- create legacy compatibility files only when a real class is too different for a readable inline form;
- do not create a large overlay tree in advance;
- do not build large Java mutation logic into `build.matrix.gradle`.

Update this policy if the pilot gives strong evidence that another split is simpler.

## Proven Sagittary Boundaries

- **26.2:** entity constants moved to `EntityTypes`, knockback gained damage
  arguments, and the hidden-HUD check moved. Short native conditions preserve
  the existing behavior; no modern overlay is needed. Fabric's hand-render
  redirect targets `submitArmWithItem`, not the earlier `renderArmWithItem`.
- **1.21/1.21.1:** item use/tooltip signatures, arrow persistence, registry keys,
  and registration APIs use local conditions. Bundle mouse
  actions do not exist here: omit that client mixin, not just its import.
- **Legacy client architecture:** arrow render states, extracted GUI rendering,
  bundle tooltip rendering, and Topaz debug gizmos require four small alternate
  implementations. Two additional bridges provide legacy item predicates and
  the fletcher trade. Trade values still come from the canonical JSON.
- **1.21 JEI:** the pinned API requires a drawable background and the older
  tooltip callback. 1.21.1 does not share this exception.
- **Serialized formats:** derive legacy recipe ingredients, integer model data,
  arrow/bow/crossbow/quiver overrides, and advancement icons from parsed current
  JSON. Model predicates require the corresponding legacy client bridge. A flat
  item definition pointing at its own existing model must retain that model's
  textures/parent, not become a self-referencing parent. Check generated models
  during resource reload as well as in the release jar.
- **Mechanical aliases:** native string replacements are bidirectional. Match
  exact moved classes, not broad package prefixes: reversing a broad projectile
  or Fabric datagen prefix incorrectly moves classes that stayed put.
- **Mixin descriptors:** legacy `ItemStack.is(Item)` differs from modern
  `is(Object)`. The Fabric hand-render redirects use a versioned descriptor and
  a coerced argument. Release-jar inspection confirms legacy annotations are
  remapped; compilation alone still does not prove runtime application.
- **Fletching interaction dispatch:** legacy `FletchingTableBlock` overrides
  `useWithoutItem` and returns `PASS` without calling `BlockBehaviour`. Target
  that subclass on 1.21/1.21.1; target `BlockBehaviour` on 26.x, where the subclass
  no longer exists. A local target-class condition suffices. Checking only the
  base method's descriptor would miss this runtime failure.
- **Optional JEI ingredients:** absent Spelunkery materials resolve to air.
  Filter unavailable components from JEI catalysts, ingredient info, and recipe
  combinations. This uses unchanged shared Java rather than a version branch.
- **Standalone matrix nodes:** compiling common plus loader sources directly
  needs Architectury API, not the old common-project Gradle transformation layer.
  Removing that unnecessary plugin also removes its development runtime/agent
  and the 26.2 NeoForge shutdown executor leak. Keep this distinction explicit;
  retaining an API dependency does not justify retaining every old build plugin.

All twelve targets compile with these rules. Runtime/release acceptance is
tracked separately in `stonecutter-migration.md`; these boundaries are not a
claim that every gameplay path has been exercised.

### Runtime dependency ABI

Fabric Loader 0.19.4 supplies Fabric Mixin 0.17.4, whose newly compiled
`Redirect.at` annotations are arrays. Its bundled MixinExtras 0.5.4 crashes when
reading these annotations, on both common arrow recovery and client redirects.
The regression pass advances **26.2 Fabric** to Loader 0.19.5, which bundles
[MixinExtras 0.5.5](https://github.com/LlamaLad7/MixinExtras/releases/tag/0.5.5).
This also satisfies the local Spelunkery jar's >=0.19.4 constraint. NeoForge keeps
the older compile-only Fabric annotation pin; its runtime libraries are separate.

### Quiver selection and optional integrations

- **Selection is mod-owned:** `sagittary:quiver_selection` stores an immutable,
  structurally comparable count-one arrow prototype. Resolve it against current
  contents, not a persisted index. Insertion/reordering preserves the type;
  depletion repairs selection and an empty quiver clears it. Vanilla bundle
  selection is only a modern visual mirror, never the source of ammunition.
- **One request path:** key-scroll and inventory-hover scroll send
  `CycleQuiverPayload` to the server. Validate the current container and slot;
  never accept client-authored contents. Legacy container screens have no bundle
  scrolling and Creative overrides that method, so intercept MouseHandler's
  actual `Screen.mouseScrolled(DDDD)` call on both generations. Translate
  Creative's client-only slots by backing stack identity to the inventory menu;
  Creative also replaces `LocalPlayer.containerMenu`, so menu identity alone is
  not a sufficient test. Modern vanilla bundle mouse actions exclude quivers.
- **Trinkets boundary:** Updated (`trinkets_updated`, `eu.pb4`) is the 26.x API;
  original (`trinkets`, `dev.emi`) is the legacy Fabric API. Keep reflection on
  their public interfaces, then use Minecraft's typed `Container` methods so
  legacy remapping remains correct. Back-slot entity data/item tags keep slot
  setup optional. Legacy NeoForge intentionally has no supported provider.
  A detected but incompatible reflection API warns once, not silently.
- **Development versus release:** supported Trinkets-family mods and matching
  local Spelunkery artifacts use Loom's local runtime configurations, not
  implementation or jar-in-jar. Optional integrations must be tested both
  installed and absent; compilation without them is not runtime evidence.
