# Sagittary Migration Notes

This file preserves durable facts learned during Sagittary's Stonecutter migration without forcing every future agent to load the migration diary.

Do not read this file by default. Use it when debugging a boundary that resembles one of the cases below.

## Resulting Matrix

The migration established one branch for:

```text
1.21      Fabric / NeoForge
1.21.1    Fabric / NeoForge
26.1      Fabric / NeoForge
26.1.1    Fabric / NeoForge
26.1.2    Fabric / NeoForge
26.2      Fabric / NeoForge
```

The repository, not this history file, remains authoritative for the current target set.

## Build Architecture That Worked

- target registration derived from matrix property files;
- aggregate matrix tasks derived from the same target list;
- standalone target nodes compile canonical common + loader source;
- Architectury API remains a runtime/API dependency;
- the old Architectury common-project transformation layer was unnecessary for this matrix shape;
- target-isolated run directories;
- deterministic release artifact location per target;
- legacy/current artifact generation selected per target;
- native Stonecutter processing used for source conditions;
- small compatibility source overrides only where implementations genuinely diverge;
- parsed resource transforms for serialized-format drift;
- no broad Gradle Java source-rewrite system.

One early false positive compiled zero Java because the source view was wired incorrectly. An empty prepared/merged source view must be treated as a hard error.

## Important Compatibility Discoveries

### Fletching Table dispatch

On the legacy generation, the relevant Fletching Table subclass overrides interaction and does not rely on the base `BlockBehaviour` path used by the current generation.

Lesson: when a mixin compiles but gameplay does not trigger, inspect the actual dispatch/override path rather than only a method descriptor with the same name.

### Mixin descriptors and remapping

Legacy and current descriptors can differ even when method intent is unchanged.

Release-JAR inspection verified remapped legacy annotations/classes, but runtime application still required a client check.

### Resource transforms

Legacy recipe/item-model/advancement formats were best handled as parsed data transforms.

A syntactically valid transform once produced a self-parenting model loop. Package validity is not enough; resource reload/visual behavior matters.

### Runtime dependency ABI

One modern Fabric loader combination bundled a MixinExtras version incompatible with the annotation ABI produced by the runtime. Advancing the loader to the fixed bundled combination solved the problem.

Lesson: inspect actual runtime library versions before rewriting application source around a loader ABI failure.

### Standalone Architectury nodes

The API dependency and the old Gradle transformation plugin are different concerns. Keeping Architectury API did not require keeping the old transformation layer. Removing the unnecessary layer also removed development-runtime side effects.

## Optional Integrations

Sagittary established this pattern:

- optional mods attach to development runtime only;
- release artifacts do not bundle them;
- release metadata remains optional;
- supported integrations are checked present and absent;
- a local sibling repository is convenient but not required for a fresh clone;
- matching sibling artifacts are selected by target metadata, not filename guesswork;
- missing optional sibling artifacts warn rather than fail ordinary compilation.

Trinkets split by generation:

- original Trinkets for legacy Fabric;
- Trinkets Updated for supported current targets;
- no Trinkets-family provider for legacy NeoForge under the current support policy.

The legacy Fabric Trinkets dependency needed a repository/artifact path that correctly brought Cardinal Components transitively. Cardinal Components itself is not a direct Sagittary public dependency.

## Quiver Compatibility Lesson

The stable cross-version design was to make Sagittary own the authoritative selected-arrow state rather than depending on a version-specific vanilla bundle selection field.

General lesson: when a vanilla internal state exists only on some versions, store gameplay authority in mod-owned synchronized state and use vanilla state only as an optional visual mirror.

## Validation Lessons

The efficient release confidence model became:

```text
all targets compile
all targets package
all artifacts structurally verified
representative modern/legacy × Fabric/NeoForge packaged-JAR smokes
manual/targeted gameplay for affected systems
platform dry-runs
```

Running every client permutation provided diminishing returns once compatibility shapes were covered.

A target with a unique mechanism should be added to the sentinel set.

## Production Release-JAR Smoke

Development clients were not considered sufficient.

### Fabric

The clean solution was Loom's production client task using the exact packaged release JAR.

### NeoForge

The clean solution was a pinned PortableMC Windows x64 binary invoked through WSL Windows interop.

The Linux PortableMC binary on the development WSL host required a newer glibc than available. The correct response was not a glibc workaround; use the Windows binary.

The smoke proved exact JAR origin and bounded startup/termination in fresh temporary runtime directories.

Lesson: prefer official/pinned launch mechanisms over writing custom installer parsing or launcher infrastructure.

## WSL / Filesystem Lessons

Mounted-drive build/runtime activity produced some permission/live-log problems.

Native Linux temporary project-cache/runtime locations were effective for high-churn intermediates while keeping final artifacts in deterministic repository paths.

Do not encode one machine's absolute paths into the build.

A WSL graphics crash observed during development was treated as environment evidence, not automatically attributed to Sagittary. Narrow software-renderer controls helped isolate it.

## Publishing Architecture

The successful publishing shape was:

```text
plan
→ platform dry-runs
→ one combined preflight
→ commit/push
→ sequential Modrinth + CurseForge publication
→ receipt journal
→ release tag
→ GitHub Actions release
```

Key safeguards:

- real uploads require explicit confirmation;
- tokens live outside Git;
- exact artifact/hash proof;
- duplicate/equivalent-version guards;
- sequential uploads;
- partial success is reported, not hidden;
- receipts make retries resumable;
- GitHub tag path does not need local `.env`;
- GitHub Release uses the built-in Actions token.

Sagittary 2.1.0 was the first release used to prove this end-to-end workflow.

## Things Not to Repeat

Avoid these rabbit holes unless narrower methods have failed:

- large Java mutation engines in Gradle;
- whole-tree legacy overlays before real divergence is known;
- manual `javap` archaeology before using `minecraft-dev`;
- repeated full-matrix builds while one sentinel is broken;
- repeated green production smokes/dry-runs for reassurance;
- custom NeoForge launcher/installer parsing when the pinned production backend works;
- host glibc surgery for a tool that has a working Windows binary;
- interactive GUI automation becoming its own subproject during a port;
- treating optional sibling repositories as fresh-clone requirements.

The general rules distilled from these lessons live in the other development docs.
