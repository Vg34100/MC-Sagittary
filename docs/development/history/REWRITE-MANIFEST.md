# Rewrite Package

This package is a proposed replacement/cleanup of the Sagittary multiversion documentation.

## Replace

- `AGENTS.md`
- `docs/development/compatibility-policy.md`
- `docs/development/publishing.md`

## Add

- `docs/development/multiversion-playbook.md`
- `docs/development/validation-and-release.md`
- `docs/development/fresh-machine-setup.md`
- `docs/development/history/sagittary-migration-notes.md`

## Keep as compatibility stubs

- `docs/development/stonecutter-migration.md`
- `docs/development/stonecutter-port-acceptance-checklist.md`

## Important implementation note

`build-smart.py doctor` and `build-smart.py bootstrap` are specified as the desired fresh-machine interface. If those commands do not yet exist, implement them before treating those commands as existing functionality.
