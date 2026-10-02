# Sagittary Stonecutter Port Acceptance Checklist

Use this as the completion gate for each claimed Minecraft/loader target.

Compilation is necessary but not sufficient.

## Baseline

- [ ] Record the pre-migration 26.1.2 Fabric result.
- [ ] Record the pre-migration 26.1.2 NeoForge result.
- [ ] Record known pre-existing failures.
- [ ] Check `git status --short` before migration edits.

## Matrix Structure

- [ ] Every claimed Minecraft/loader pair has an explicit target.
- [ ] Every target has a pinned `gradle/matrix/<target>.properties`.
- [ ] Matrix property files are the active dependency/version source of truth.
- [ ] Every target has an isolated run directory.
- [ ] Fabric metadata is expanded for the active target.
- [ ] NeoForge metadata is expanded for the active target.
- [ ] Java/toolchain selection matches the target generation.
- [ ] Legacy/current packaging tasks produce the intended artifact type.

## Compatibility

For each compatibility rule:

- [ ] The difference was observed on a real target.
- [ ] The exact target API/resource format was checked.
- [ ] The selected mechanism follows `compatibility-policy.md`.
- [ ] Small source differences use a readable local form.
- [ ] Large behavior differences use a real compatibility implementation instead of broad source regex.
- [ ] Resource transforms are target-scoped and their processed output was inspected.
- [ ] Mixins were checked against exact target method/descriptor behavior.

## Sentinel Checks

Before expanding a compatibility rule:

- [ ] Canonical 26.1.2 Fabric compiles/packages.
- [ ] Canonical 26.1.2 NeoForge compiles/packages.
- [ ] 26.2 Fabric passes the relevant check.
- [ ] 26.2 NeoForge passes when loader behavior is involved.
- [ ] 1.21.1 Fabric passes the relevant legacy check.
- [ ] 1.21.1 NeoForge passes when loader behavior is involved.
- [ ] 1.21 is checked separately before assuming 1.21.1 compatibility.

## Matrix Build

- [ ] `python build-smart.py matrix:compile` passes.
- [ ] `python build-smart.py matrix:package` passes.
- [ ] `python build-smart.py matrix:server` passes.
- [ ] `--print-plan` confirms intended Gradle Java/task selection when environment setup is in doubt.

## Dedicated Server

For each target required by the release gate:

- [ ] `runServer` starts.
- [ ] Minecraft reaches `Done (...)!`.
- [ ] The wrapper sends `stop`.
- [ ] The server begins clean shutdown.
- [ ] Gradle exits successfully.
- [ ] No unexpected runtime error was reported.
- [ ] Shared initialization did not load client-only classes.

Do not count an exit-before-`Done` as a pass.

## Client

For each target required by the release gate:

- [ ] `runClient` reaches a stable title screen.
- [ ] Resource reload completes.
- [ ] No required mixin fails.
- [ ] Loader metadata accepts active Minecraft/loader/dependency versions.

## Sagittary Gameplay Checks

Run checks relevant to compatibility work, including:

- [ ] items register and render;
- [ ] component arrows can be created/used;
- [ ] custom arrow entities behave normally;
- [ ] bow/crossbow behavior works;
- [ ] quiver storage/use/cycling works;
- [ ] quiver tooltip/client rendering works;
- [ ] Fletching Table menu opens and functions;
- [ ] Fletching Table screen renders and updates;
- [ ] networking payloads used by tested features work;
- [ ] affected mixins execute without runtime injection failures;
- [ ] JEI integration works when installed for that target;
- [ ] optional integrations do not become hard dependencies.

## Real Release Artifact

For each release target:

- [ ] Build the installable artifact, not a raw/dev jar.
- [ ] Confirm the artifact is in the intended matrix output location.
- [ ] Inspect loader metadata inside the produced jar.
- [ ] Test the produced jar in an external launcher instance with matching dependencies.
- [ ] Reach the title screen.
- [ ] Exercise representative Sagittary gameplay.

Development `runClient` alone does not prove the release jar is correct.

## Closeout

- [ ] Record new version boundaries future work needs to know.
- [ ] Remove accidental generated/cached files from the change set.
- [ ] Do not stage unrelated user files.
- [ ] State exactly which targets received runtime checks.
- [ ] State which targets received only compile/package checks.
- [ ] Do not call the migration complete while required runtime or external-jar checks remain.
