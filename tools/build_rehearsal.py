"""Add English captions and local system narration to the real tablet capture.

Requires Pillow and imageio-ffmpeg. This is labelled rehearsal footage, not
evidence of a Fire TV run. No cloud voice, model API, or stock media is used.
"""
from pathlib import Path
import json
import subprocess
import wave
from PIL import Image, ImageDraw, ImageFont
import imageio_ffmpeg

root = Path(__file__).resolve().parents[1]
work = root / "submission/rehearsal-work"
data = json.loads((work / "chapters.json").read_text(encoding="utf-8"))
ffmpeg = imageio_ffmpeg.get_ffmpeg_exe()

def timestamp(seconds):
    ms = round(seconds * 1000)
    return f"{ms // 3600000:02}:{ms // 60000 % 60:02}:{ms // 1000 % 60:02},{ms % 1000:03}"

captions = "\n".join(
    f"{i+1}\n{timestamp(c['start'])} --> {timestamp(c['start']+c['duration'])}\n{c['text']}\n"
    for i, c in enumerate(data["chapters"])
)
(work / "captions.srt").write_text(captions, encoding="utf-8")
(root / "submission/tablet-rehearsal-captions.srt").write_text(captions, encoding="utf-8")

overlay = Image.new("RGBA", (1920, 1080))
draw = ImageDraw.Draw(overlay)
draw.rectangle((0, 0, 1920, 34), fill=(4, 14, 22, 245))
font = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 21)
draw.text((960, 2), "FIRE OS TABLET REHEARSAL  |  FIRE TV RECORDING STILL REQUIRED", font=font, fill=(255, 204, 115), anchor="mt")
overlay.save(work / "provenance.png")

subprocess.run(["powershell", "-NoProfile", "-File", str(root / "tools/synthesize-rehearsal.ps1"), "-WorkFolder", str(work)], check=True)

command = [ffmpeg, "-y", "-i", str(root / "captures/tablet-rehearsal-raw.mp4"), "-i", "provenance.png"]
filters = ["[0:v]fps=30,scale=1920:1080[base];[base][1:v]overlay=0:0,subtitles=captions.srt:force_style='FontName=Segoe UI,FontSize=19,PrimaryColour=&H00FFFFFF,OutlineColour=&H00120E04,BorderStyle=3,Outline=2,MarginV=27'[v]"]
voices = []
for i, c in enumerate(data["chapters"]):
    voice = work / f"voice-{i:02}.wav"
    command.extend(["-i", str(voice)])
    with wave.open(str(voice), "rb") as audio:
        duration = audio.getnframes() / audio.getframerate()
    tempo = max(1.0, duration / (c["duration"] - 0.2))
    if tempo > 1.6:
        raise SystemExit(f"Chapter {i} narration is too long for its shot: {tempo:.2f}x")
    tag = f"a{i}"
    filters.append(f"[{i+2}:a]atempo={tempo:.4f},adelay={round(c['start']*1000)}:all=1[{tag}]")
    voices.append(f"[{tag}]")
filters.append("".join(voices) + f"amix=inputs={len(voices)}:normalize=0[a]")
command.extend(["-filter_complex", ";".join(filters), "-map", "[v]", "-map", "[a]", "-t", str(data["end"]), "-c:v", "libx264", "-preset", "fast", "-crf", "21", "-pix_fmt", "yuv420p", "-c:a", "aac", "-b:a", "128k", "-movflags", "+faststart", str(root / "submission/FixQuest-AI-tablet-rehearsal.mp4")])
subprocess.run(command, cwd=work, check=True)
print(f"Created labelled rehearsal, {data['end']:.1f} seconds")
