#!/usr/bin/env python3
"""
Smart Gradle Build Wrapper for Minecraft Mods
Reduces verbose output to only essential error information.
Works with any Architectury/Fabric/NeoForge mod project.

Usage: python build-smart.py [build|runClient|clean|any-gradle-task]
Default task: build
"""

import subprocess
import sys
import re
import os
from collections import defaultdict
from pathlib import Path

# Detect OS for gradle wrapper
GRADLEW = "gradlew.bat" if os.name == "nt" else "./gradlew"

def find_project_root():
    """Find project root by looking for gradlew"""
    current = Path.cwd()
    while current != current.parent:
        if (current / "gradlew").exists() or (current / "gradlew.bat").exists():
            return current
        current = current.parent
    return Path.cwd()

def shorten_path(full_path):
    """Convert full path to just filename:line"""
    # Match patterns like: /long/path/File.java:123: error
    match = re.search(r'[/\\]([^/\\]+\.java):(\d+)', full_path)
    if match:
        return f"{match.group(1)}:{match.group(2)}"
    return full_path

def parse_java_error(line):
    """Extract error info from a Java compiler error line"""
    # Pattern: path/File.java:line: error: message
    match = re.match(r'(.+\.java):(\d+):\s*error:\s*(.+)', line)
    if match:
        filepath, line_num, message = match.groups()
        # Extract just filename from path
        filename = Path(filepath).name
        short_path = f"{filename}:{line_num}"
        return {
            'file': short_path,
            'line': line_num,
            'message': message.strip(),
            'full_path': filepath
        }
    return None

def parse_symbol_error(lines, start_idx):
    """Parse multi-line 'cannot find symbol' errors"""
    symbols = []
    i = start_idx + 1
    while i < len(lines) and i < start_idx + 5:
        line = lines[i].strip()
        if 'symbol:' in line:
            match = re.search(r'symbol:\s*(?:class|variable|method)\s+(\w+)', line)
            if match:
                symbols.append(match.group(1))
        elif 'location:' in line:
            break
        i += 1
    return symbols

def run_build(task="build"):
    """Run gradle and capture output"""
    project_root = find_project_root()
    os.chdir(project_root)

    cmd = [GRADLEW, task]

    print(f"Running: {' '.join(cmd)}")
    print("-" * 50)

    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        shell=(os.name == "nt")
    )

    output_lines = []
    for line in process.stdout:
        output_lines.append(line.rstrip())

    process.wait()
    return process.returncode, output_lines

def analyze_errors(lines):
    """Parse build output and extract unique errors"""
    errors = []
    symbol_errors = defaultdict(list)  # Group "cannot find symbol" by file

    i = 0
    while i < len(lines):
        line = lines[i]

        # Skip gradle boilerplate
        if any(skip in line for skip in [
            'FAILURE:', 'What went wrong:', 'Try:', 'Run with',
            'Deprecated Gradle', 'warning-mode', 'actionable task',
            'Problems report', 'Build Scan', 'help.gradle.org',
            'Note:', 'Recompile with'
        ]):
            i += 1
            continue

        error = parse_java_error(line)
        if error:
            if 'cannot find symbol' in error['message']:
                # Parse the symbol details from following lines
                symbols = parse_symbol_error(lines, i)
                if symbols:
                    for sym in symbols:
                        symbol_errors[error['file']].append(sym)
                else:
                    symbol_errors[error['file']].append('unknown')
                i += 5  # Skip the multi-line symbol error block
                continue
            else:
                errors.append(error)
        i += 1

    return errors, symbol_errors

def print_summary(errors, symbol_errors, return_code):
    """Print a concise error summary"""
    total_errors = len(errors) + sum(len(v) for v in symbol_errors.values())

    if return_code == 0:
        print("\n" + "=" * 50)
        print("BUILD SUCCESS")
        print("=" * 50)
        return

    print("\n" + "=" * 50)
    print(f"BUILD FAILED - {total_errors} error(s)")
    print("=" * 50)

    # Print symbol errors grouped by file
    if symbol_errors:
        print("\nMissing symbols:")
        for file, symbols in symbol_errors.items():
            unique_symbols = list(set(symbols))
            if len(unique_symbols) <= 3:
                print(f"  {file} - cannot find: {', '.join(unique_symbols)}")
            else:
                print(f"  {file} - cannot find: {', '.join(unique_symbols[:3])} (+{len(unique_symbols)-3} more)")

    # Print other errors
    if errors:
        print("\nOther errors:")
        seen = set()
        for err in errors:
            key = (err['file'], err['message'][:50])
            if key not in seen:
                seen.add(key)
                msg = err['message']
                if len(msg) > 80:
                    msg = msg[:77] + "..."
                print(f"  {err['file']} - {msg}")

    print("\n" + "-" * 50)
    print(f"Fix these {total_errors} error(s) and rebuild")

def analyze_runtime_errors(lines):
    """Parse runtime errors from runClient output"""
    errors = []
    in_error = False
    current_error = []

    for line in lines:
        # Detect error patterns in runtime logs
        if '/ERROR]' in line or 'Exception' in line or 'Error:' in line:
            if 'Missing' in line or 'missing' in line:
                errors.append(line.strip())
            elif 'Couldn\'t load' in line:
                errors.append(line.strip())
            elif 'Failed to' in line:
                errors.append(line.strip())
            elif 'IllegalStateException' in line:
                errors.append(line.strip())

    # Deduplicate
    return list(set(errors))[:15]  # Max 15 unique errors

def main():
    task = sys.argv[1] if len(sys.argv) > 1 else "build"

    return_code, lines = run_build(task)

    if task in ["runClient", "runServer", "fabric:runClient", "neoforge:runClient"]:
        # For run tasks, look for runtime errors
        runtime_errors = analyze_runtime_errors(lines)
        if runtime_errors:
            print("\n" + "=" * 50)
            print(f"RUNTIME ERRORS DETECTED")
            print("=" * 50)
            for err in runtime_errors:
                # Shorten long lines
                if len(err) > 100:
                    err = err[:97] + "..."
                print(f"  {err}")
            print("-" * 50)
        return return_code

    if return_code != 0:
        errors, symbol_errors = analyze_errors(lines)
        print_summary(errors, symbol_errors, return_code)
    else:
        print_summary([], {}, return_code)

    # Also check for successful jar output
    if return_code == 0:
        for line in lines:
            if 'jar' in line.lower() and ('fabric' in line.lower() or 'neoforge' in line.lower()):
                print(f"  {line.strip()}")

    return return_code

if __name__ == "__main__":
    sys.exit(main())
