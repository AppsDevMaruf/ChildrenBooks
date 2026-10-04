"""Original background music for the Children Books promo (synthesised, no samples).

120 BPM, C major, 16 bars = 32 s. Plucked-string chords, marimba melody, soft bass,
light kick / shaker / clap. Writes promo_music.wav (44.1 kHz stereo).
"""
import numpy as np
from scipy.io import wavfile
from scipy.signal import butter, lfilter

SR = 44100
BPM = 120
BEAT = 60 / BPM
BAR = 4 * BEAT
BARS = 16
TOTAL = BARS * BAR + 2.0  # ring-out tail
rng = np.random.default_rng(7)

N = int(TOTAL * SR)
L = np.zeros(N)
R = np.zeros(N)


def midi(n):
    return 440.0 * 2 ** ((n - 69) / 12)


def add(sig, t0, gain=1.0, pan=0.0):
    i = int(t0 * SR)
    if i >= N:
        return
    sig = sig[: N - i] * gain
    L[i:i + len(sig)] += sig * np.sqrt(0.5 * (1 - pan))
    R[i:i + len(sig)] += sig * np.sqrt(0.5 * (1 + pan))


_PLUCKS = {}


def pluck(freq, dur=1.2, bright=0.5):
    key = (round(freq, 3), dur, bright)
    if key not in _PLUCKS:
        _PLUCKS[key] = _pluck(freq, dur, bright)
    return _PLUCKS[key]


def _pluck(freq, dur=1.2, bright=0.5):
    """Karplus-Strong plucked string."""
    n = int(dur * SR)
    period = int(SR / freq)
    buf = rng.uniform(-1, 1, period)
    # soften the excitation for a nylon/ukulele feel
    buf = lfilter([1 - bright], [1, -bright], buf)
    out = np.empty(n)
    for k in range(n):
        out[k] = buf[k % period]
        nxt = buf[(k + 1) % period]
        buf[k % period] = 0.996 * 0.5 * (out[k] + nxt)
    env = np.minimum(1, np.arange(n) / (0.003 * SR))
    return out * env


def marimba(freq, dur=0.9):
    t = np.arange(int(dur * SR)) / SR
    tone = (np.sin(2 * np.pi * freq * t)
            + 0.35 * np.sin(2 * np.pi * freq * 4 * t) * np.exp(-t * 18)
            + 0.12 * np.sin(2 * np.pi * freq * 10 * t) * np.exp(-t * 40))
    env = np.exp(-t * 5.5) * np.minimum(1, t / 0.002)
    return tone * env


def bass(freq, dur):
    t = np.arange(int(dur * SR)) / SR
    tone = np.sin(2 * np.pi * freq * t) + 0.25 * np.sin(2 * np.pi * 2 * freq * t)
    env = np.minimum(1, t / 0.01) * np.exp(-t * 1.6)
    return tone * env


def kick():
    t = np.arange(int(0.35 * SR)) / SR
    f = 120 * np.exp(-t * 22) + 45
    ph = 2 * np.pi * np.cumsum(f) / SR
    return np.sin(ph) * np.exp(-t * 9)


def noise_hit(dur, hp, decay):
    n = int(dur * SR)
    t = np.arange(n) / SR
    b, a = butter(2, hp / (SR / 2), btype="high")
    return lfilter(b, a, rng.uniform(-1, 1, n)) * np.exp(-t * decay)


def clap():
    s = np.zeros(int(0.25 * SR))
    for off in (0, 0.008, 0.017):
        h = noise_hit(0.2, 1200, 28)
        i = int(off * SR)
        s[i:i + len(h)] += h[: len(s) - i]
    return s * 0.6


# C  G  Am F  (two bars of each pair through the song)
CHORDS = {
    "C": [60, 64, 67, 72], "G": [55, 62, 67, 71], "Am": [57, 64, 69, 72], "F": [53, 60, 65, 69],
}
ROOT = {"C": 36, "G": 43, "Am": 45, "F": 41}
prog = ["C", "G", "Am", "F"] * 4  # one chord per bar

# Original melody (scale degrees as MIDI), 8th-note grid, None = rest. Two 4-bar phrases.
A = [72, None, 76, 79, 76, None, 74, 72,   74, None, 71, 74, 79, None, 74, None,
     76, None, 72, 76, 81, 79, 76, None,   77, 76, 74, None, 72, None, None, None]
B = [79, None, 81, 79, 76, None, 72, 74,   74, 76, 74, 71, 67, None, 71, 74,
     76, 79, 76, 72, 69, None, 72, 76,     77, None, 76, 74, 72, None, 72, None]
melody = [None] * 16 + A + B + A + [79, 77, 76, 74, 72, None, None, None] + [72]

STRUM = [1, 0, 1, 1, 0, 1, 1, 0]  # 8th-note strum pattern

for bar, ch in enumerate(prog):
    t0 = bar * BAR
    intro = bar < 1
    outro = bar == BARS - 1
    # chords
    for step, on in enumerate(STRUM):
        if not on or (outro and step > 0):
            continue
        for j, note in enumerate(CHORDS[ch]):
            g = (0.16 if step % 2 == 0 else 0.11) * (0.8 if intro else 1.0)
            add(pluck(midi(note), 2.4 if outro else 1.1), t0 + step * BEAT / 2 + j * 0.012, g, pan=-0.25 + 0.15 * j)
    # bass
    if not intro:
        add(bass(midi(ROOT[ch]), BEAT * 1.9), t0, 0.32)
        add(bass(midi(ROOT[ch]), BEAT * 1.9), t0 + 2 * BEAT, 0.26)
    # drums
    if not intro and not outro:
        for b in range(4):
            if b in (0, 2):
                add(kick(), t0 + b * BEAT, 0.55)
            if b in (1, 3):
                add(clap(), t0 + b * BEAT, 0.18, pan=0.1)
        for e in range(8):
            add(noise_hit(0.06, 6000, 60), t0 + e * BEAT / 2, 0.05 if e % 2 else 0.035, pan=0.35)
    elif outro:
        add(kick(), t0, 0.5)

for k, note in enumerate(melody):
    if note is None:
        continue
    add(marimba(midi(note)), k * BEAT / 2, 0.22, pan=0.2)
    add(marimba(midi(note + 12)) * 0.25, k * BEAT / 2 + 0.003, 0.22, pan=0.2)

# Short "sparkle" on the logo hit at 0 s and the end card at 30 s
for t_s in (0.0, 30.0):
    for i, n in enumerate([84, 88, 91, 96]):
        add(marimba(midi(n), 0.6), t_s + i * 0.05, 0.12, pan=-0.3 + 0.2 * i)

mix = np.stack([L, R], axis=1)
# gentle fade-out over the last 2.5 s
fade = np.ones(N)
fo = int(2.5 * SR)
fade[-fo:] = np.linspace(1, 0, fo) ** 1.5
mix *= fade[:, None]
mix /= np.max(np.abs(mix)) + 1e-9
mix *= 0.89  # about -1 dBFS peak
wavfile.write("promo_music.wav", SR, (mix * 32767).astype(np.int16))
print("wrote promo_music.wav", round(N / SR, 2), "s")
