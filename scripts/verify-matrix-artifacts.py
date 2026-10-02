#!/usr/bin/env python3
"""Inspect installable matrix jars; this is not a runtime/gameplay test."""
import json
from pathlib import Path
import re
import struct
from zipfile import BadZipFile, ZipFile

ROOT = Path(__file__).resolve().parents[1]


def quoted(text, key):
    match = re.search(rf'^\s*{re.escape(key)}\s*=\s*"([^"\n]*)"', text, re.MULTILINE)
    assert match, f"Missing metadata field: {key}"
    return match.group(1)


def release_jar(matrix):
    """One installable archive, never a classifier or a stale other-version jar."""
    target = matrix.stem
    jars = [p for p in (ROOT / "build/libs" / target).glob("*.jar")
            if not p.name.endswith(("-dev.jar", "-sources.jar", "-javadoc.jar",
                                    "-dev-shadow.jar", "-raw.jar"))]
    assert len(jars) == 1, f"Expected one release jar: {jars}"
    root_pins = dict(line.strip().split("=", 1) for line in (ROOT / "gradle.properties").read_text().splitlines()
                     if "=" in line and not line.lstrip().startswith("#"))
    minecraft, loader = matrix.stem.rsplit("-", 1)
    expected = f"{root_pins['archives_name']}-{loader}-{minecraft}-{root_pins['mod_version']}.jar"
    assert jars[0].name == expected, f"Wrong/stale release artifact: {jars[0]} (expected {expected})"
    return jars[0]


def inspect(matrix):
    pins = dict(line.strip().split("=", 1) for line in matrix.read_text().splitlines()
                if "=" in line and not line.lstrip().startswith("#"))
    target = matrix.stem
    artifact = release_jar(matrix)
    legacy = pins["loom_generation"] == "legacy"
    with ZipFile(artifact) as jar:
        files = set(jar.namelist())
        read_json = lambda name: json.loads(jar.read(name))
        main = jar.read("net/vg/sagittary/Sagittary.class")
        assert struct.unpack(">H", main[6:8])[0] == int(pins["java_version"]) + 44
        for name in files:
            if name.endswith(".json"):
                data = read_json(name)
                if name.endswith("mixins.json"):
                    assert data["compatibilityLevel"] == "JAVA_" + pins["java_version"]
                    for side in ("mixins", "client", "server"):
                        for mixin in data.get(side, []):
                            cls = (data["package"] + "." + mixin).replace(".", "/") + ".class"
                            assert cls in files, f"Missing mixin class: {cls}"
                if legacy and "/recipe/" in name:
                    assert not any(isinstance(data.get(k), str)
                                   for k in ("ingredient", "base", "addition", "template")), name
                    assert not any(isinstance(v, str) for v in data.get("ingredients", [])), name
                    assert not any(isinstance(v, str) for v in data.get("key", {}).values()), name
                if legacy and name.startswith("assets/") and "/models/" in name:
                    namespace, model_path = name[7:].split("/models/", 1)
                    assert data.get("parent") != namespace + ":" + model_path[:-5], name
        if pins["loader"] == "fabric":
            meta = read_json("fabric.mod.json")
            assert meta["depends"]["minecraft"] == pins["minecraft_version"]
            for dependency, pin in (("java", "java_version"), ("fabricloader", "fabric_loader_version"),
                                    ("architectury", "architectury_api_version")):
                assert meta["depends"][dependency] == ">=" + pins[pin]
            assert {"jei", pins["trinkets_mod_id"], "spelunkery"} <= set(meta["suggests"])
            assert ({"trinkets", "trinkets_updated"} & set(meta["suggests"])) == {pins["trinkets_mod_id"]}
            assert not {"jei", "trinkets", "trinkets_updated", "spelunkery"} & set(meta["depends"])
        else:
            meta = jar.read("META-INF/neoforge.mods.toml").decode()
            blocks = re.findall(r'\[\[dependencies\.sagittary\]\](.*?)(?=\n\[|\Z)', meta, re.DOTALL)
            deps = {quoted(block, "modId"): block for block in blocks}
            assert quoted(deps["minecraft"], "versionRange") == "[" + pins["minecraft_version"] + "]"
            for dependency, pin in (("neoforge", "neoforge_version"), ("architectury", "architectury_api_version")):
                assert quoted(deps[dependency], "versionRange") == "[" + pins[pin] + ",)"
            assert quoted(deps["spelunkery"], "type") == "optional"
            assert ("trinkets_updated" in deps) != legacy
            assert "trinkets" not in deps
            if not legacy:
                assert quoted(deps["trinkets_updated"], "type") == "optional"
            assert quoted(meta, "javaVersion") == "[" + pins["java_version"] + ",)"
        mixins = read_json("sagittary.mixins.json")
        assert ("client.BundleMouseActionsMixin" in mixins["client"]) != legacy
        assert any(f.startswith("assets/") and "/items/" in f for f in files) != legacy
        assert not any(f.startswith("META-INF/jars/") for f in files)
        assert not any(f.endswith(".jar") for f in files), "Embedded dependency jar"
        assert not any(f.startswith(prefix) for f in files for prefix in
                       ("net/vg/spelunkery/", "dev/emi/trinkets/", "eu/pb4/trinkets/"))
        assert "net/vg/sagittary/mixin/client/QuiverInventoryScrollMixin.class" in files
        assert "net/vg/sagittary/mixin/client/ContainerScreenAccessor.class" in files
        assert read_json("data/trinkets/entities/sagittary.json")["slots"] == ["chest/back"]
        if legacy:
            source = json.loads((ROOT / "common/src/main/resources/assets/sagittary/items/component_arrow.json").read_text())
            expected = {int(v): choice["model"]["model"] for choice in source["model"]["cases"]
                        for v in (choice["when"] if isinstance(choice["when"], list) else [choice["when"]])}
            actual = read_json("assets/sagittary/models/item/component_arrow.json")["overrides"]
            assert {entry["predicate"]["custom_model_data"]: entry["model"] for entry in actual} == expected
            for entry in actual:
                namespace, path = entry["model"].split(":", 1)
                assert f"assets/{namespace}/models/{path}.json" in files
            for quiver in ("quiver", "hunter_quiver", "ranger_quiver"):
                model = read_json(f"assets/sagittary/models/item/{quiver}.json")
                assert model["overrides"][0]["predicate"]["sagittary:fullness"] == 0.0001
            assert "net/vg/sagittary/client/LegacyItemProperties.class" in files
            assert "net/vg/sagittary/compat/LegacyVillagerTrades.class" in files
            if pins["loader"] == "fabric":
                assert b"net/minecraft/class_" in jar.read("net/vg/sagittary/mixin/PlayerProjectileMixin.class")
        assert not any(b"${" in jar.read(name) for name in files
                       if name in ("fabric.mod.json", "META-INF/neoforge.mods.toml") or name.endswith("mixins.json"))
    print(f"{target}: release metadata, classes, formats OK")
    return artifact


if __name__ == "__main__":
    matrices = sorted((ROOT / "gradle/matrix").glob("*.properties"))
    assert matrices, "No matrix properties found"
    for matrix in matrices:
        try:
            inspect(matrix)
        except (AssertionError, KeyError, ValueError, OSError, BadZipFile) as error:
            raise SystemExit(f"{matrix.stem}: {error}") from error
