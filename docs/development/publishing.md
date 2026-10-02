# Publishing Sagittary

No command publishes just because credentials exist. Start with:

```bash
python build-smart.py publish:plan
python build-smart.py publish:modrinth-dry-run
python build-smart.py publish:curseforge-dry-run
python build-smart.py publish:all-dry-run
python build-smart.py publish:preflight
```

Preflight packages the matrix, verifies all installable JARs, runs the existing
four-target `release-smoke`, then validates all Minotaur and CurseForgeGradle debug payloads. It must
print `PUBLISH PREFLIGHT PASS`. GUI prerequisites apply only to local preflight;
CI packages/verifies without client launches.

## Release facts and credentials

- Matrix nodes/pins remain authoritative in `gradle/matrix/*.properties`.
- `gradle/publishing.properties` owns public project IDs, names, release type,
  and the version-specific human-edited section of `docs/wiki/release-notes.md`.
  A missing/empty section fails clearly. Only that section feeds all three release bodies.
- Minotaur is pinned to **2.10.0**. Each target uploads exactly its verified
  installable JAR (`remapJar` output on legacy), no additional/source/dev files.
- Visible names: `[Fabric] Sagittary 2.1.0 (26.2)` and
  `[NeoForge] Sagittary 2.1.0 (26.2)`. Internal Modrinth numbers:
  `2.1.0-26.2-fabric` / `2.1.0-26.2-neoforge`.
- Modrinth: [sagittary](https://modrinth.com/mod/sagittary), ID `kh5dbjUL`.
  Its optional Spelunkery integration is
  [The Spelunker Update](https://modrinth.com/mod/spelunker-update), ID `RLyGc4q3`.
- CurseForgeGradle is pinned to **1.3.33**; project
  [sagittary](https://www.curseforge.com/minecraft/mc-mods/sagittary), ID `1587941`.
  Each independent file explicitly declares the exact Minecraft version,
  Fabric/NeoForge, Java 21/25, and both Client and Server. Automatic version
  detection is disabled. Optional Spelunkery points to `the-spelunker-update`
  (project `1597427`), not the unrelated original Spelunkery project.
- Required dependencies come from packaged loader metadata; JEI is the optional
  matrix/compile-only integration. Supported Trinkets providers stay optional;
  legacy NeoForge has none. Trinkets' own Cardinal Components dependencies are
  **not** direct Sagittary dependencies.
- Create ignored root `.env` from `.env.example`, or explicitly set
  `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` in your environment. Only real publishing opens `.env`, only
  these two keys are accepted, and explicit environment values win. Values are
  passed only through the child environment, never CLI/manifests/log output.
  Debug runs remove the credential. A tracked `.env` blocks credential loading.
- Generated public manifest/dry-run log: `build/publishing/`. GitHub asset
  collection contains exactly the verified matrix JARs plus workflow-only
  manifest/notes; only the JARs are attached to the GitHub Release.

## Explicit real publication (not part of validation)

```bash
python build-smart.py publish:modrinth --confirm
python build-smart.py publish:curseforge --confirm
python build-smart.py publish:all --confirm
```

Without `--confirm`, the wrapper refuses. The Gradle task also defaults to debug
mode; all real tasks depend on an all-artifact and public-version duplicate gate.
Uploads are sequential (Modrinth, then CurseForge) and stop on failure. The unified
command validates both credentials, duplicate guards, and official CurseForge
game tags before the first upload. Successfully created remote IDs/URLs are
reported immediately and saved to the public `build/publishing/<version>-uploads.jsonl`
receipt journal, even on partial failure; there is no destructive rollback.
Retrying with identical verified artifacts skips journaled successes. Changed
bytes or unjournaled remote duplicates are refused. Keep the journal until release
completion, including while CurseForge files are still scanning. CurseForge's
upload API has no file-list endpoint, so indexed duplicates are checked using
public CFWidget data; API receipts cover not-yet-indexed uploads.
Do not use `--continue`, parallel uploads, debug logging, or build scans.

**Existing 2.0.1 versions already cover 26.1.2 Fabric and NeoForge under the old
bare internal number.** Real publication catches those equivalents as well as
new target-qualified numbers. Choose a new mod version and author its matching
release-notes section before a new complete release; do not duplicate 2.0.1.

## GitHub Actions

`.github/workflows/release.yml` supports manual `workflow_dispatch` and pushed
`v*` release tags. Manual defaults build/verify all targets and upload a workflow artifact; they create
no GitHub Release and needs no Modrinth secret. It installs Java 21 and 25 on a
clean Ubuntu runner and uses the existing wrapper/verification.

After local preflight passes on the intended committed revision, dispatch:

- `create_draft=true`, `local_preflight_passed=true`: one draft `v<mod_version>`
  release, titled `Sagittary <mod_version>`, with all installable matrix JARs.
- Additionally `publish_modrinth=true`: real, sequential target uploads after
  the draft exists. Configure the repository Actions secret `MODRINTH_TOKEN`
  (Modrinth token needs `CREATE_VERSION` scope). GitHub-only paths need no token.
- Additionally `publish_github=true`: make the draft public **only after** all
  requested stages succeed. All mutating inputs default to false.

GitHub release creation uses the automatic `GITHUB_TOKEN` (`contents: write`
only in the release job), not a PAT. Public releases/different-commit tags and
unexpected draft assets are not overwritten. A pushed tag must match the
authoritative mod version. The tag path builds/verifies the tagged commit,
stages exactly the matrix JARs in one draft, then makes that GitHub Release public.
It does **not** republish Modrinth/CurseForge and needs no external platform secret.
Publish those locally first, then push the release tag. No project-description
synchronization or machine-specific paths are configured.
