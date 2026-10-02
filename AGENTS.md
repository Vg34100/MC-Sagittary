# AGENTS.md

This file defines the default agent workflow for Sagittary and for maintenance of its multiversion build. Keep it short. Detailed procedures live under `docs/development/`.

## Priorities

1. Preserve working behavior.
2. Keep one clear source of truth for each version, loader, dependency, artifact, and publishing fact.
3. Keep Fabric and NeoForge aligned where practical without hiding meaningful loader differences.
4. Minimize context and validation cost.
5. Prefer small, verified compatibility changes over broad rewrites.
6. Never overwrite unrelated user work.
7. Stop once the requested acceptance evidence exists.

## Repository Model

Sagittary is a Stonecutter multiversion project with canonical `common/`, `fabric/`, and `neoforge/` source trees.

The exact target matrix is discovered from:

```text
gradle/matrix/*.properties
```

Do not hard-code the target count or supported versions into build logic when they can be derived from those files.

Authoritative responsibilities:

- `settings.gradle` — target registration and early platform selection.
- `stonecutter.gradle` — active target and aggregate matrix tasks.
- `build.matrix.gradle` — generic per-target Gradle configuration.
- `gradle/matrix/*.properties` — target-specific version/dependency facts.
- `gradle/publishing.properties` — public publishing identifiers and naming templates.
- `common/`, `fabric/`, `neoforge/` — canonical maintained source.
- small compatibility source/resource areas — only where a real version boundary requires them.

Do not duplicate active dependency pins in root `gradle.properties` once a matrix property owns them.

## Context Discipline

Search first, read second, edit last.

- Open only the files involved in the current failure or feature.
- Do not read the entire Java tree or large build scripts without a reason.
- Do not paste full Gradle logs when the useful error is a few lines.
- Batch related fixes before rebuilding.
- Prefer existing wrapper commands over re-deriving raw Gradle commands.
- Treat historical migration notes as reference material, not default context.
- Do not inspect the full `build-smart.py` unless it fails, selects the wrong plan, or must be changed for the requested task.

For a new session, read only the document that matches the task:

- migration / adding versions → `docs/development/multiversion-playbook.md`
- deciding how to represent API drift → `docs/development/compatibility-policy.md`
- deciding what to test → `docs/development/validation-and-release.md`
- publishing → `docs/development/publishing.md`
- new computer / missing tools → `docs/development/fresh-machine-setup.md`

## Minecraft Source Investigation

For Minecraft API changes, mappings, class or method availability, mixin targets, and version comparisons, prefer the `minecraft-dev` MCP.

Escalation order:

1. `minecraft-dev` MCP for Minecraft/version questions.
2. project source and resolved dependency metadata.
3. Gradle-cache/JAR inspection when the MCP cannot answer or when the exact resolved artifact must be verified.
4. `javap`/manual bytecode archaeology only when narrower methods are insufficient.

Do not spend a migration session rediscovering APIs manually when the static MCP can answer the question.

## Build Wrapper

Use `build-smart.py` for normal build, matrix, runtime, smoke, and publication workflows.

Before trusting a newly migrated repository or unfamiliar machine:

```bash
python build-smart.py compile --print-plan
```

Useful commands include:

```bash
python build-smart.py compile
python build-smart.py compile:fabric
python build-smart.py compile:neoforge
python build-smart.py compile:modern
python build-smart.py matrix:compile
python build-smart.py matrix:package
python build-smart.py matrix:server
python build-smart.py matrix:servers-runtime
python build-smart.py release-smoke
python build-smart.py publish:plan
python build-smart.py publish:preflight
```

When `doctor`/`bootstrap` support is present, use it before debugging environment failures:

```bash
python build-smart.py doctor
python build-smart.py bootstrap
```

See `fresh-machine-setup.md` for the contract these commands should satisfy.

## Compatibility Rule

Represent version drift using the smallest mechanism that keeps behavior visible:

1. unchanged shared source;
2. native Stonecutter condition for a small local difference;
3. narrow deterministic replacement for a truly mechanical rename;
4. parsed resource/data transform for serialized-format drift;
5. separate compatibility implementation when behavior or lifecycle materially differs.

Do not create a large overlay tree in advance.

Do not turn `build.matrix.gradle` into a Java source generator or broad regex-rewrite engine.

See `compatibility-policy.md`.

## Loader Rule

Minecraft-version differences and loader differences are separate axes.

Keep loader-native behavior in loader source when practical. Check Fabric and NeoForge independently for bootstrap, client registration, rendering, networking, metadata, events, mixins, and optional integrations.

Do not force shared abstraction when the loaders genuinely have different lifecycle or registration APIs.

## Mixins

Treat mixins as runtime-sensitive compatibility code.

For an affected target, verify:

- target class;
- method name and descriptor;
- injection point;
- loader/version ownership;
- omission when the target behavior does not exist.

Compilation is not proof that a mixin applies correctly.

## Optional Integrations

Optional integrations must remain optional for end users.

Development runtimes may attach supported optional mods, but release artifacts must not accidentally bundle them or turn them into required dependencies.

When compatibility with an optional mod is changed, validate both:

- integration present;
- integration absent.

A local sibling mod may be auto-detected, but its absence must not make a fresh clone fail unless that repository is explicitly required for the requested task.

## Validation and Stopping Rule

Use the smallest validation set that matches the change.

- common Java change → affected generation on both loaders;
- Fabric-only change → affected Fabric target(s);
- NeoForge-only change → affected NeoForge target(s);
- resource transform → package + processed-resource inspection + relevant runtime/visual check;
- mixin change → runtime on every distinct target shape touched;
- build-matrix change → matrix compile/package and artifact verification;
- publishing change → publication tests and platform dry-runs, not extra gameplay runs.

Full release acceptance uses `validation-and-release.md`.

**Stopping rule:** once the requested acceptance evidence is green, stop. Do not repeat full-matrix operations, production clients, or dry-runs solely for reassurance. Continue only when a required criterion is unresolved or a new deterministic failure appears.

## Feature Development After Migration

For new features, do not develop twelve targets in parallel.

Default workflow:

1. implement and deeply test on one current canonical target, normally the current/highest Fabric target unless the task requires another target;
2. prove the feature there;
3. port the proven behavior across the matrix using the compatibility policy;
4. run narrow sentinel checks while porting;
5. run full release gates only when preparing a release.

## Dirty Worktree and Commits

Before editing or committing:

```bash
git status --short
```

Never revert unrelated user changes. Never stage generated builds, runtime directories, caches, local `.env`, downloaded tools, or unrelated assets.

Do not commit, push, tag, or publish unless the user explicitly authorized those actions.

## Closeout

Report only:

- what changed;
- which targets/gates were checked;
- what passed;
- what remains untested or manual;
- any new reusable compatibility rule.

Keep raw logs out of the closeout.
