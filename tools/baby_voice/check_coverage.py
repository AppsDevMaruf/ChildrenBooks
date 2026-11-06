"""Lists every line the app can send to text-to-speech and reports the ones with no baby-voice clip.

Mirrors BabyVoice.kt: exact clip, or "<template prefix> + <word>" for the word templates, existing
recordings (days, months, seasons, colours, number words) count as covered. Letter cards must have a baby clip per letter.
Run: python3 -I tools/baby_voice/check_coverage.py
"""
import html, pathlib, re, sys, zlib

root = pathlib.Path(__file__).resolve().parents[2]
main = root / "app/src/main"
raw = {p.stem for p in (main / "res/raw").iterdir()}
strings_xml = (main / "res/values/strings.xml").read_text()
catalog = (main / "java/com/childrenbooks/data/local/LearningCatalog.kt").read_text()
listen = (main / "java/com/childrenbooks/feature/listen/ListenContent.kt").read_text()


def s(name):
    v = re.search(r'<string name="%s"[^>]*>(.*?)</string>' % re.escape(name), strings_xml, re.S).group(1)
    return html.unescape(v.replace("\\'", "'").replace('\\"', '"'))


def key(text):
    return "bv_%08x" % (zlib.crc32(text.encode()) & 0xFFFFFFFF)


# Recordings the app already had (BabyVoice.existing): baby-voice days/months/seasons/colours, number words.
existing = set(re.findall(r'LessonItem\("([^"]+)"', catalog))
existing |= {s(w) for w in re.findall(r'NumberLesson\("[^"]*", [^,]+, [^,]+, R\.string\.(\w+)', catalog)}


def clip(text):
    return text in existing or key(text) in raw


templates = [s(n) for n in ["prompt_find", "feedback_hint", "feedback_reveal", "prompt_starts_with"]]


def covered(line):
    line = line.strip()
    if clip(line):
        return True
    for t in templates:
        before, after = t.split("%1$s")
        if line.startswith(before) and line.endswith(after) and len(line) > len(before) + len(after):
            word = line[len(before):len(line) - len(after)].strip()
            if clip(before.strip()) and clip(word):
                return True
    if re.fullmatch(re.escape(s("reward_great_job_name")).replace(re.escape("%1$s"), ".+"), line):
        return clip(s("reward_great_job"))
    return False


# Every card word Practice can name: picture cards, day/month/season/colour cards, letter-card words, number words.
picture_words = re.findall(r'PictureLesson\("([^"]+)"', catalog)
lesson_words = re.findall(r'LessonItem\("([^"]+)"', catalog)
letter_words = []
for letter, desc in re.findall(r'AlphabetLesson\("(\w)", R\.string\.(\w+)', catalog):
    phrase = s(desc).strip()
    letter_words.append(phrase.split(" is for ", 1)[1].strip() if " is for " in phrase else phrase)
number_words = [s(w) for w in re.findall(r'NumberLesson\("[^"]*", [^,]+, [^,]+, R\.string\.(\w+)', catalog)]
letters = [l for l, _ in re.findall(r'AlphabetLesson\("(\w)", R\.string\.(\w+)', catalog)]

lines = {}
def add(where, text):
    lines.setdefault(text, where)

for n in ["onboarding_hello_spoken", "onboarding_who", "age_young_spoken", "age_older_spoken"]:
    add("Onboarding", s(n))
for n in ["world_letters", "world_numbers", "world_time", "world_colours", "world_nature", "world_food", "world_things"]:
    add("Home tile", s(n))
add("Stickers tip", s("stickers_tip"))
add("Reward", s("reward_great_job"))
add("Reward (with name)", s("reward_great_job_name").replace("%1$s", "Mina"))
for t in re.findall(r'ListenItem\("[^"]*", "[^"]*", [^,]+, "([^"]+)"\)', listen) + re.findall(r'script = "([^"]+)"', listen):
    add("Listen", t)
for w in picture_words:
    add("Picture card", w)
for n in ["feedback_great", "feedback_try_again", "prompt_how_many"]:
    add("Practice", s(n))
for w in number_words:
    add("Number card spelling", "-".join(w.upper()) + ", " + w)
for l in letters:
    add("Letter card / starts with", l)
    add("Practice: starts with", s("prompt_starts_with").replace("%1$s", l))
for w in picture_words + lesson_words + letter_words + number_words:
    add("Practice: find", s("prompt_find").replace("%1$s", w))
    add("Practice: hint", s("feedback_hint").replace("%1$s", w))
    add("Practice: reveal", s("feedback_reveal").replace("%1$s", w))

missing = [(where, text) for text, where in lines.items() if not covered(text)]
print(f"{len(lines)} possible lines, {len(lines) - len(missing)} play the baby voice or an existing recording")
for where, text in missing:
    print(f"  ROBOT  [{where}] {text}")
sys.exit(1 if missing else 0)
