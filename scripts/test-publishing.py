#!/usr/bin/env python3
"""Small stdlib-only regression tests for publication safety; no real secrets/API writes."""
import os
from pathlib import Path
import runpy
import subprocess
import tempfile
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
WRAPPER = runpy.run_path(str(ROOT / "build-smart.py"))
PUBLISH = runpy.run_path(str(ROOT / "scripts/publish-release.py"))


class PublishingSafety(unittest.TestCase):
    def test_dotenv_is_child_only_and_environment_wins(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / ".env").write_text("# comment\n\nMODRINTH_TOKEN='synthetic-test-value'\nCURSEFORGE_TOKEN=synthetic-cf-value\nJAVA_HOME=ignored\n")
            result = subprocess.CompletedProcess([], 0, stdout=b"", stderr=b"")
            with patch.dict(os.environ, {}, clear=True), patch('subprocess.run', return_value=result):
                child = WRAPPER["publishing_environment"](root)
                self.assertEqual(child, {"MODRINTH_TOKEN": "synthetic-test-value", "CURSEFORGE_TOKEN": "synthetic-cf-value"})
                self.assertNotIn("MODRINTH_TOKEN", os.environ)
            with patch.dict(os.environ, {"MODRINTH_TOKEN": "explicit-test-value", "CURSEFORGE_TOKEN": "explicit-cf-value"}, clear=True), patch('subprocess.run', return_value=result):
                with patch.object(Path, 'open', side_effect=AssertionError("must not open .env")):
                    self.assertEqual(WRAPPER["publishing_environment"](root)["MODRINTH_TOKEN"], "explicit-test-value")

    def test_tracked_env_stops_before_open(self):
        result = subprocess.CompletedProcess([], 0, stdout=b".env\n", stderr=b"")
        with patch('subprocess.run', return_value=result), patch.object(Path, 'open', side_effect=AssertionError("must not open .env")):
            with self.assertRaisesRegex(WRAPPER["PlanError"], "STOP: .env is tracked"):
                WRAPPER["publishing_environment"](ROOT)

    def test_confirmation_required_before_other_work(self):
        project = WRAPPER["Project"](ROOT)
        self.assertEqual(PUBLISH["run"](project, "publish:modrinth", [], False, {}), 2)
        self.assertEqual(PUBLISH["run"](project, "publish:curseforge", [], False, {}), 2)
        self.assertEqual(PUBLISH["run"](project, "publish:all", [], False, {}), 2)

    def test_plan_is_read_only_and_matrix_derived(self):
        with patch.object(Path, 'write_text', side_effect=AssertionError("read-only")), patch('subprocess.run', side_effect=AssertionError("no subprocess")):
            data = PUBLISH["publication_plan"]()
        self.assertEqual({r["target"] for r in data["entries"]}, {p.stem for p in (ROOT / 'gradle/matrix').glob('*.properties')})
        self.assertEqual(len({r['version_number'] for r in data['entries']}), len(data['entries']))
        for row in data['entries']:
            loader = {'fabric': 'Fabric', 'neoforge': 'NeoForge'}[row['loader']]
            self.assertEqual(row['version_name'], f"[{loader}] Sagittary {data['mod_version']} ({row['minecraft']})")
            mods = {d['mod_id'] for d in row['dependencies']}
            self.assertFalse(any('cardinal' in d for d in mods))
            if row['loader'] == 'neoforge' and row['minecraft'].startswith('1.'):
                self.assertFalse({'trinkets', 'trinkets_updated'} & mods)
            self.assertEqual(row['curseforge_versions'], [row['minecraft'], loader, f"Java {row['java']}", 'Client', 'Server'])
            self.assertEqual(next(d['curseforge_slug'] for d in row['dependencies'] if d['mod_id'] == 'spelunkery'), 'the-spelunker-update')

    def test_curseforge_exact_metadata_and_duplicate_guard(self):
        data = PUBLISH['publication_plan']()
        names = {name for row in data['entries'] for name in row['curseforge_versions']}
        versions = [dict(name=name, gameVersionTypeID=1) for name in names]
        PUBLISH['check_curseforge_versions'](data, versions)
        with self.assertRaisesRegex(ValueError, 'Unsupported CurseForge'):
            PUBLISH['check_curseforge_versions'](data, [v for v in versions if v['name'] != '26.2'])
        existing = dict(id=1587941, files=[dict(id=1, display=data['entries'][0]['version_name'])])
        with self.assertRaisesRegex(ValueError, 'Existing CurseForge'):
            PUBLISH['check_curseforge_duplicates'](data, existing)
        PUBLISH['check_curseforge_duplicates'](data, dict(id=1587941, files=[]))

    def test_resume_requires_exact_artifact_receipt(self):
        import json
        data = PUBLISH['publication_plan']()
        row = data['entries'][0]
        for entry in data['entries']:
            entry.update(sha256='synthetic-hash', size=100)
        receipt = dict(platform='curseforge', target=row['target'], mod_version=data['mod_version'],
                       project_id=data['curseforge_project_id'], artifact=Path(row['artifact']).name,
                       sha256=row['sha256'], size=row['size'], id='synthetic-id')
        with patch.object(Path, 'is_file', return_value=True), patch.object(Path, 'read_text', return_value=json.dumps(receipt)):
            self.assertEqual(len(PUBLISH['pending_entries'](data, 'curseforge')), len(data['entries'])-1)
        receipt['sha256'] = 'wrong'
        with patch.object(Path, 'is_file', return_value=True), patch.object(Path, 'read_text', return_value=json.dumps(receipt)):
            with self.assertRaisesRegex(ValueError, 'Conflicting upload receipt'):
                PUBLISH['pending_entries'](data, 'curseforge')

    def test_exact_and_historical_duplicate_guards(self):
        data = PUBLISH["publication_plan"]()
        row = next(r for r in data['entries'] if r['target'] == '26.1.2-fabric')
        for number in (row['version_number'], data['mod_version']):
            existing = [dict(id='synthetic-version', version_number=number, game_versions=[row['minecraft']], loaders=[row['loader']])]
            with self.assertRaisesRegex(ValueError, 'Existing Modrinth'):
                PUBLISH['check_duplicates'](data, existing)
        PUBLISH['check_duplicates'](data, [])

    def test_missing_and_empty_notes_fail(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / 'gradle').mkdir()
            (root / 'gradle.properties').write_text('mod_version=2.0.1\narchives_name=sagittary\n')
            config = (ROOT / 'gradle/publishing.properties').read_text()
            (root / 'gradle/publishing.properties').write_text(config)
            with self.assertRaisesRegex(ValueError, 'release notes missing'):
                PUBLISH['publication_plan'](root)
            (root / 'docs/wiki').mkdir(parents=True)
            (root / 'docs/wiki/release-notes.md').write_text('## 2.0.1 Development\n\n## Next\n')
            with self.assertRaisesRegex(ValueError, 'nonempty release-notes'):
                PUBLISH['publication_plan'](root)


if __name__ == '__main__':
    unittest.main()
