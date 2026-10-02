# AGENTS.md

This file defines the default agent workflow for Sagittary.

Keep this file short. Detailed multiversion rules live under `docs/development/`.

## Priorities

1. Preserve working behavior.
2. Keep Fabric and NeoForge aligned where practical.
3. Minimize context use.
4. Prefer small, verified changes over broad rewrites.
5. Do not overwrite unrelated user work.
6. Keep one clear source of truth for each multiversion fact.

## Context Rules

- Search first, read second, edit last.
- Open only files related to the current error or feature.
- Do not read all Java source files up front.
- Do not dump full Gradle logs into the conversation.
- Batch related compile fixes before rebuilding.
- Reuse known build commands instead of deriving them again.
- When a vanilla or loader API is uncertain, inspect the exact target version instead of guessing.

## Repository Baseline

Sagittary currently uses:

- Architectury-style `common/`, `fabric/`, and `neoforge/` source trees.
- Minecraft 26.1.2 as the current canonical source baseline.
- Java 25 for the current 26.x build.
- Fabric and NeoForge.
- JEI integration.
- Shadow packaging in the current single-version build.

The Stonecutter migration will replace the single-version project layout with explicit version/loader targets while keeping the canonical source trees as the main source inputs.

## Build Wrapper

Use `build-smart.py` instead of raw Gradle for normal compile and matrix checks.

Before relying on the adaptive wrapper in a newly migrated repo:

```bash
python build-smart.py compile --print-plan
```

Check that the selected Gradle Java and task list are correct.

Useful commands after the Stonecutter matrix exists:

```bash
python build-smart.py compile
python build-smart.py compile:fabric
python build-smart.py compile:neoforge
python build-smart.py compile:modern
python build-smart.py matrix:compile
python build-smart.py matrix:package
python build-smart.py matrix:server
python build-smart.py matrix:servers-runtime
```

Use `smoke-server:<target>` for one dedicated-server runtime check.

Do not call a server smoke test successful unless the wrapper confirms the Minecraft server reached its ready `Done (...)!` milestone and stopped cleanly.

## Multiversion Docs

For Stonecutter work, read only the docs needed for the task:

- `docs/development/stonecutter-migration.md`
- `docs/development/compatibility-policy.md`
- `docs/development/stonecutter-port-acceptance-checklist.md`

Historical port notes are reference material only. Do not load them by default.

## Source Compatibility Rule

Do not use large Gradle/Groovy string-rewrite systems to synthesize Java as the default approach.

For source differences:

1. Use unchanged shared source when possible.
2. Use native Stonecutter source conditions for small, local differences.
3. Use a mechanical replacement only when the replacement is narrow and deterministic.
4. Use a separate compatibility implementation only when the old and new code are materially different.
5. Do not create a large legacy overlay tree before a real compile/runtime difference proves that it is needed.

See `docs/development/compatibility-policy.md`.

## Build Configuration Rule

After migration:

- `settings.gradle` owns the registered Stonecutter targets.
- `stonecutter.gradle` owns aggregate matrix tasks.
- `build.matrix.gradle` owns generic per-target Gradle configuration.
- `gradle/matrix/*.properties` owns target dependency/version pins.
- Java compatibility differences should live with Java source or in a small compatibility source area, not as large Java strings inside `build.matrix.gradle`.
- Resource/data compatibility transforms may live in build logic when they are deterministic and scoped.

Do not duplicate dependency pins in root `gradle.properties` once matrix property files become authoritative.

## Loader Rule

Keep loader-specific behavior in loader source when the APIs differ.

Check Fabric and NeoForge separately for bootstrap, client registration, render hooks, event registration, networking, metadata, and optional mod integration.

Do not force a shared Architectury abstraction when loader-native behavior is meaningfully different.

## Mixins

Treat mixins as version-sensitive runtime code.

For every affected target:

- verify the target class exists;
- verify the target method name and descriptor;
- verify the injection point;
- keep loader-only mixins in loader-specific configs;
- remove or disable a mixin on versions where its target does not exist.

Compilation alone is not enough evidence for a mixin.

## Sagittary High-Risk Areas

During this migration, pay extra attention to:

- `FletchingTableMenu` / `FletchingTableScreen`
- projectile and arrow entity APIs
- bow/crossbow/quiver item APIs
- tooltip and client rendering
- bundle accessors and bundle-related mixins
- networking payloads
- JEI integration
- Fabric data generation
- optional Trinkets/Spelunkery hooks
- loader-specific render/client registration

These are inspection priorities, not instructions to rewrite them preemptively.

## Dirty Worktree

Assume the repository may contain user-owned edits.

Before editing or committing:

```bash
git status --short
```

Never revert unrelated changes.

Do not stage generated output, run directories, caches, or unrelated asset edits.

## Validation Rule

Use the smallest validation set that matches the change.

- common Java change -> affected version generation on both loaders
- Fabric-only change -> Fabric targets only
- NeoForge-only change -> NeoForge targets only
- resource transform -> package + resource/gameplay check
- mixin change -> runtime check on affected target(s)
- build-matrix change -> matrix compile/package/launch-setup checks

Do not rerun the full runtime matrix after every small edit.

## Commit Rule

Prefer one coherent change per commit.

Examples:

- `build: add stonecutter target matrix`
- `build: parameterize loader metadata`
- `fix: support legacy quiver api`
- `fix: split legacy fletching screen behavior`
- `docs: record sagittary multiversion rules`

Do not make commits unless the user asked for them.

## Closeout

At the end of a work session, report:

- what changed;
- what target(s) were checked;
- what passed;
- what was not tested;
- any new compatibility rule that should be recorded.

Keep the report short. Do not paste long build logs.
