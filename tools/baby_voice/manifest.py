"""Builds the list of lines the app speaks with text-to-speech, and the raw file name for each.

The file name must match BabyVoice.kt: "bv_" + crc32 of the exact text, as 8 hex digits.
Run: python3 -I tools/baby_voice/manifest.py  (writes tools/baby_voice/manifest.json)
"""
import json, re, zlib, pathlib, html

root = pathlib.Path(__file__).resolve().parents[2]
main = root / "app/src/main"
strings_xml = (main / "res/values/strings.xml").read_text()

def s(name):
    m = re.search(r'<string name="%s"[^>]*>(.*?)</string>' % re.escape(name), strings_xml, re.S)
    v = m.group(1)
    return html.unescape(v.replace("\\'", "'").replace('\\"', '"'))

def key(text):
    return "bv_%08x" % (zlib.crc32(text.encode("utf-8")) & 0xFFFFFFFF)

# Words that already have a baby-voice clip (days, months, seasons, colours): BabyVoice.kt reuses those.
catalog_src = (main / "java/com/childrenbooks/data/local/LearningCatalog.kt").read_text()
existing = set(re.findall(r'LessonItem\("([^"]+)"', catalog_src))

lines = []
def add(group, text):
    if text in existing:
        return
    if text not in [l["text"] for l in lines]:
        lines.append({"group": group, "text": text, "file": key(text)})

for n in ["onboarding_hello_spoken", "onboarding_who", "age_young_spoken", "age_older_spoken",
          "prompt_how_many", "feedback_great", "feedback_try_again", "stickers_tip"]:
    add("fixed", s(n))
for n in ["world_letters", "world_numbers", "world_time", "world_colours", "world_nature", "world_food", "world_things"]:
    add("fixed", s(n))
# Lines with a word in them: the part before the word, then the word's own clip.
for n in ["prompt_find", "feedback_hint", "feedback_reveal", "prompt_starts_with"]:
    add("prefix", s(n).split("%1$s")[0].strip())

catalog = (main / "java/com/childrenbooks/data/local/LearningCatalog.kt").read_text()
for w in re.findall(r'PictureLesson\("([^"]+)"', catalog):
    add("word", w)

# Letter-card words ("B is for Bee" -> "Bee"): Practice says "Look for the Bee" / "This is the Bee".
for desc in re.findall(r'AlphabetLesson\("\w", R\.string\.(\w+)', catalog):
    phrase = s(desc).strip()
    add("word", phrase.split(" is for ", 1)[1].strip() if " is for " in phrase else phrase)

listen = (main / "java/com/childrenbooks/feature/listen/ListenContent.kt").read_text()
for t in re.findall(r'ListenItem\("[^"]*", "[^"]*", [^,]+, "([^"]+)"\)', listen):
    add("listen", t)
for t in re.findall(r'script = "([^"]+)"', listen):
    add("listen", t)

out = root / "tools/baby_voice/manifest.json"
out.write_text(json.dumps(lines, indent=1, ensure_ascii=False))
from collections import Counter
print(Counter(l["group"] for l in lines), len(lines), sum(len(l["text"]) for l in lines), "chars")

# Letter cards: the letter itself in the baby voice ("What starts with A?" uses it too).
# "A is for Apple", number words and spellings keep the app's earlier recordings.
for letter in re.findall(r'AlphabetLesson\("(\w)"', catalog):
    add("letter", letter)
# Number cards: the second clip spells the number and says it ("O-N-E, one"), like the earlier
# recording, now in the baby voice. The first clip keeps the earlier number recording.
for w in re.findall(r'NumberLesson\("[^"]*", [^,]+, [^,]+, R\.string\.(\w+)', catalog):
    add("spelling", "-".join(s(w).upper()) + ", " + s(w))
out.write_text(json.dumps(lines, indent=1, ensure_ascii=False))
print(Counter(l["group"] for l in lines), len(lines))
