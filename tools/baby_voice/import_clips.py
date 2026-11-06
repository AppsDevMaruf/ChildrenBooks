"""Turns ElevenLabs downloads into the app's baby-voice clips in res/raw.

takes.tsv lists "<clip name><TAB><downloaded file name>", one take per line, so each line is
generated once. Speaking speed is set here (TEMPO, pitch unchanged), so it can be changed later
without spending credits. Also trims silence at both ends and evens out loudness.

Usage: python3 -I tools/baby_voice/import_clips.py [downloads folder, default ~/Downloads]
"""
import json, pathlib, subprocess, sys

TEMPO = 0.88  # below 1 is slower; kids need slow, clear speech

here = pathlib.Path(__file__).parent
raw = here.parents[1] / "app/src/main/res/raw"
downloads = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else pathlib.Path.home() / "Downloads")
lines = json.loads((here / "manifest.json").read_text())

trim = "silenceremove=start_periods=1:start_threshold=-50dB:start_silence=0.05"
filters = ",".join([trim, "areverse", trim, "areverse", f"atempo={TEMPO}", "loudnorm=I=-16:TP=-1.5:LRA=11",
                    "apad=pad_dur=0.12"])

done = 0
for row in (here / "takes.tsv").read_text().splitlines():
    if not row.strip() or row.startswith("#"):
        continue
    key, name = row.split("\t")
    src = downloads / name
    if not src.exists():
        sys.exit(f"missing download for {key}: {name}")
    out = raw / f"{key}.mp3"
    subprocess.run(["ffmpeg", "-loglevel", "error", "-y", "-i", str(src), "-af", filters,
                    "-ar", "44100", "-ac", "1", "-c:a", "libmp3lame", "-b:a", "96k", str(out)], check=True)
    done += 1
print(f"imported {done} of {len(lines)} lines at tempo {TEMPO}")
