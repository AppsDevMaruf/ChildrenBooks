"""Records the newest downloads in takes.tsv for the next manifest lines that have no take yet.

Usage: python3 -I tools/baby_voice/add_takes.py <count> [downloads folder]
Lines get takes in manifest order and downloads in time order. Refuses if the count of new
downloads differs, or if one is identical to another (a missed generation re-downloads the old take).
"""
import hashlib, json, pathlib, sys
here = pathlib.Path(__file__).parent
count = int(sys.argv[1])
downloads = pathlib.Path(sys.argv[2] if len(sys.argv) > 2 else pathlib.Path.home() / "Downloads")
takes = here / "takes.tsv"
rows = [r.split("\t") for r in takes.read_text().splitlines() if r.strip() and not r.startswith("#")]
done = {k for k, _ in rows}
latest = max((n for _, n in rows), default="")
todo = [l["file"] for l in json.loads((here / "manifest.json").read_text()) if l["file"] not in done][:count]
new = sorted(p for p in downloads.glob("ElevenLabs_*baby_voice*.mp3") if p.name > latest)
if len(new) != count or len(todo) != count:
    sys.exit(f"expected {count} new downloads after {latest or 'start'}, found {len(new)}; {len(todo)} lines left")
digest = lambda p: hashlib.md5(p.read_bytes()).hexdigest()
seen = {digest(downloads / n) for _, n in rows[-3:] if (downloads / n).exists()}
for p in new:
    if digest(p) in seen:
        sys.exit(f"{p.name} repeats an earlier take; nothing added")
    seen.add(digest(p))
with takes.open("a") as f:
    f.writelines(f"{k}\t{p.name}\n" for k, p in zip(todo, new))
left = len(json.loads((here / "manifest.json").read_text())) - len(done) - count
print(f"added {count}; {left} lines left")
