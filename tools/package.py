"""Package the built APK and portable source; excludes SDK paths and build caches."""
from pathlib import Path
import hashlib
import shutil
import zipfile

root = Path(__file__).resolve().parents[1]
out = root / "submission"
out.mkdir(exist_ok=True)
apk = root / "app/build/outputs/apk/debug/app-debug.apk"
if not apk.exists():
    raise SystemExit("Build first: gradlew.bat :app:assembleDebug")
shutil.copy2(apk, out / "FixQuest-AI-1.0.0-demo.apk")
source_files = []
for path in root.rglob("*"):
    relative = path.relative_to(root)
    if any(part in {".git", ".gradle", "build", "__pycache__", "captures", "rehearsal-work"} for part in relative.parts):
        continue
    if not path.is_file() or path.name == "local.properties":
        continue
    if relative.parts[0] == "submission" and path.suffix.lower() not in {".md", ".log"}:
        continue
    source_files.append(path)
with zipfile.ZipFile(out / "FixQuest-AI-source.zip", "w", zipfile.ZIP_DEFLATED) as archive:
    for path in source_files:
        archive.write(path, Path("FixQuest-AI") / path.relative_to(root))
files = [out / "FixQuest-AI-1.0.0-demo.apk", out / "FixQuest-AI-source.zip"]
rehearsal = out / "FixQuest-AI-tablet-rehearsal.mp4"
if rehearsal.exists():
    files.append(rehearsal)
checksums = "".join(f"{hashlib.sha256(path.read_bytes()).hexdigest()}  {path.name}\n" for path in files)
(out / "SHA256SUMS.txt").write_text(checksums, encoding="utf-8")
with zipfile.ZipFile(out / "FixQuest-AI-handoff.zip", "w", zipfile.ZIP_DEFLATED) as archive:
    for path in out.rglob("*"):
        if path.is_file() and path.name != "FixQuest-AI-handoff.zip" and "rehearsal-work" not in path.parts:
            if path.name in {"ui-debug.xml", "restart-ui.xml"}:
                continue
            archive.write(path, path.relative_to(out))
print(checksums)
print("Created submission/FixQuest-AI-handoff.zip")
