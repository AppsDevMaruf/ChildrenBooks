#!/usr/bin/env python3
"""Render the local, vertical Children Books promotional video.

The animation uses only artwork shipped with the application. It intentionally
contains no third-party music or remotely hosted media.
"""

from __future__ import annotations

import math
import subprocess
import wave
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
OUT = ROOT / "promo/children_books_updated_promo.mp4"
AUDIO = ROOT / "promo/children_books_original_chime.wav"
WIDTH, HEIGHT, FPS, DURATION = 1080, 1920, 24, 27

FONT_BOLD = "/System/Library/Fonts/Supplemental/Arial Rounded Bold.ttf"
FONT_REGULAR = "/System/Library/Fonts/Supplemental/Arial.ttf"


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    path = FONT_BOLD if bold else FONT_REGULAR
    try:
        return ImageFont.truetype(path, size)
    except OSError:
        return ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial.ttf", size)


def clamp(value: float) -> float:
    return max(0.0, min(1.0, value))


def ease(value: float) -> float:
    value = clamp(value)
    return value * value * (3.0 - 2.0 * value)


def scene_progress(time: float, start: float, end: float) -> float:
    return ease((time - start) / (end - start))


def rgba(hex_color: str, alpha: int = 255) -> tuple[int, int, int, int]:
    hex_color = hex_color.lstrip("#")
    return tuple(int(hex_color[i : i + 2], 16) for i in (0, 2, 4)) + (alpha,)


def gradient(top: str, bottom: str) -> Image.Image:
    a = rgba(top)
    b = rgba(bottom)
    image = Image.new("RGBA", (WIDTH, HEIGHT))
    draw = ImageDraw.Draw(image)
    for y in range(HEIGHT):
        amount = y / (HEIGHT - 1)
        color = tuple(round(a[i] + (b[i] - a[i]) * amount) for i in range(4))
        draw.line((0, y, WIDTH, y), fill=color)
    return image


def load_resource(name: str, nodpi: bool = False) -> Image.Image:
    folder = "drawable-nodpi" if nodpi else "drawable"
    return Image.open(RES / folder / name).convert("RGBA")


def fit(image: Image.Image, max_width: int, max_height: int) -> Image.Image:
    scale = min(max_width / image.width, max_height / image.height)
    return image.resize((max(1, round(image.width * scale)), max(1, round(image.height * scale))), Image.Resampling.LANCZOS)


def rounded_art(image: Image.Image, radius: int) -> Image.Image:
    mask = Image.new("L", image.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, image.width, image.height), radius=radius, fill=255)
    result = image.copy()
    result.putalpha(ImageChops.multiply(result.getchannel("A"), mask))
    return result


def paste_center(canvas: Image.Image, image: Image.Image, center: tuple[int, int], max_width: int, max_height: int, alpha: float = 1.0) -> None:
    art = fit(image, max_width, max_height)
    if alpha < 1.0:
        art.putalpha(art.getchannel("A").point(lambda value: round(value * alpha)))
    canvas.alpha_composite(art, (round(center[0] - art.width / 2), round(center[1] - art.height / 2)))


def rounded(draw: ImageDraw.ImageDraw, box: tuple[int, int, int, int], radius: int, fill: tuple[int, int, int, int], outline=None, width: int = 1) -> None:
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=width)


def centered_text(draw: ImageDraw.ImageDraw, text: str, center: tuple[int, int], size: int, color, bold: bool = True, spacing: int = 10) -> None:
    active_font = font(size, bold)
    box = draw.multiline_textbbox((0, 0), text, font=active_font, spacing=spacing, align="center")
    x = center[0] - (box[2] - box[0]) / 2
    y = center[1] - (box[3] - box[1]) / 2
    draw.multiline_text((x, y), text, font=active_font, fill=color, spacing=spacing, align="center")


def confetti(draw: ImageDraw.ImageDraw, time: float) -> None:
    colors = [rgba("#FFC93C"), rgba("#FF6B9C"), rgba("#63C7FF"), rgba("#8E65E9"), rgba("#75D65B")]
    positions = [(82, 176), (988, 220), (102, 1380), (938, 1452), (190, 670), (900, 850), (70, 1020), (1000, 1210)]
    for index, (x, y) in enumerate(positions):
        offset = math.sin(time * 2.5 + index) * 12
        draw.ellipse((x - 11, y - 11 + offset, x + 11, y + 11 + offset), fill=colors[index % len(colors)])


def entering(value: float) -> float:
    return ease(value / 0.18)


def scene_intro(time: float) -> Image.Image:
    canvas = gradient("#F3EBFF", "#DFF3FF")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    phase = entering(time)
    logo = rounded_art(load_resource("splash_logo.png", nodpi=True), 64)
    scale = 0.86 + phase * 0.14
    logo = logo.resize((round(logo.width * scale), round(logo.height * scale)), Image.Resampling.LANCZOS)
    paste_center(canvas, logo, (WIDTH // 2, 700), 620, 620, phase)
    centered_text(draw, "CHILDREN BOOKS", (WIDTH // 2, 1110), 74, rgba("#1B2367", round(255 * phase)))
    centered_text(draw, "Read  •  Listen  •  Learn", (WIDTH // 2, 1210), 42, rgba("#5E6795", round(255 * phase)), bold=False)
    rounded(draw, (230, 1325, 850, 1435), 55, rgba("#2D6DE3"), None)
    centered_text(draw, "A joyful learning adventure", (WIDTH // 2, 1380), 34, rgba("#FFFFFF", round(255 * phase)), bold=True)
    return canvas


def scene_home(time: float) -> Image.Image:
    local = time - 4
    canvas = gradient("#EEF8FF", "#FFFFFF")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    centered_text(draw, "Learn something new\nevery single day", (WIDTH // 2, 170), 58, rgba("#13235D"))
    phone_box = (108, 340, 972, 1600)
    rounded(draw, phone_box, 72, rgba("#FFFFFF"), rgba("#D7E8FF"), 4)
    rounded(draw, (138, 385, 942, 675), 42, rgba("#6F50E6"))
    banner = load_resource("home_slider_1.png")
    banner = fit(banner, 760, 250)
    canvas.alpha_composite(banner, (160, 430))
    centered_text(draw, "Children Books", (WIDTH // 2, 755), 44, rgba("#1B2367"))
    icons = [
        ("home_abc.png", "ABC Letters"),
        ("home_numbers.png", "Numbers"),
        ("home_days.png", "7 Days"),
        ("home_animals.png", "Animals"),
        ("home_fruits.png", "Fruits"),
        ("home_play.png", "Play & Learn"),
    ]
    for index, (asset, label) in enumerate(icons):
        column, row = index % 3, index // 3
        x, y = 175 + column * 255, 830 + row * 310
        rounded(draw, (x, y, x + 220, y + 250), 30, rgba("#F8FBFF"), rgba("#DDEBFA"), 2)
        paste_center(canvas, load_resource(asset), (x + 110, y + 105), 170, 150)
        centered_text(draw, label, (x + 110, y + 203), 24, rgba("#18245B"))
    slide = int(14 * entering(local))
    draw.rounded_rectangle((450 - slide, 1640, 630 + slide, 1656), radius=8, fill=rgba("#8D67F2"))
    return canvas


def scene_explore(time: float) -> Image.Image:
    local = time - 8
    canvas = gradient("#FFF8E8", "#E9F7FF")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    centered_text(draw, "Explore the world\nthrough play", (WIDTH // 2, 165), 62, rgba("#15245D"))
    cards = [
        ("alphabet_example_apple.png", "Letters & words", "#E7F4FF"),
        ("number_detail_7.png", "Numbers & counting", "#FFF1D6"),
        ("animal_bear.png", "Animals & nature", "#E6F7EA"),
        ("home_furniture.png", "Everyday things", "#F2EAFF"),
    ]
    for index, (asset, title, surface) in enumerate(cards):
        column, row = index % 2, index // 2
        x, y = 85 + column * 465, 420 + row * 560
        shift = int((1 - entering(local)) * (80 if column == 0 else -80))
        rounded(draw, (x + shift, y, x + 410 + shift, y + 485), 48, rgba(surface), rgba("#FFFFFF"), 3)
        paste_center(canvas, load_resource(asset, nodpi=asset.startswith(("alphabet_", "number_"))), (x + 205 + shift, y + 220), 340, 300)
        centered_text(draw, title, (x + 205 + shift, y + 410), 30, rgba("#1A2860"))
    rounded(draw, (170, 1570, 910, 1690), 58, rgba("#FFFFFF"), rgba("#DDEBFF"), 2)
    centered_text(draw, "Alphabet  •  Numbers  •  Animals  •  More", (WIDTH // 2, 1630), 31, rgba("#4E6098"))
    return canvas


def scene_play(time: float) -> Image.Image:
    local = time - 13
    canvas = gradient("#F0E9FF", "#DFF7F6")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    centered_text(draw, "Little hands.\nBig discoveries.", (WIDTH // 2, 165), 62, rgba("#18245B"))
    game_cards = [
        ("game_trace_letter.png", "Trace letters", "#DBF3FF"),
        ("game_letter_match.png", "Match & learn", "#E9F8DF"),
        ("game_puzzle_time.png", "Puzzle time", "#FFF0C9"),
        ("game_word_puzzle.png", "Word puzzle", "#E8E0FF"),
    ]
    for index, (asset, title, surface) in enumerate(game_cards):
        x = 100 if index % 2 == 0 else 550
        y = 420 + (index // 2) * 530
        bob = int(math.sin(time * 3 + index) * 10)
        rounded(draw, (x, y + bob, x + 430, y + bob + 445), 46, rgba(surface), rgba("#FFFFFF"), 3)
        paste_center(canvas, load_resource(asset, nodpi=True), (x + 215, y + 190 + bob), 350, 260)
        centered_text(draw, title, (x + 215, y + 365 + bob), 33, rgba("#1A2860"))
        rounded(draw, (x + 135, y + 395 + bob, x + 295, y + 425 + bob), 15, rgba("#FFFFFF", 150))
    rounded(draw, (170, 1575, 910, 1695), 58, rgba("#7450E8"), None)
    centered_text(draw, "Fun learning games for kids", (WIDTH // 2, 1635), 35, rgba("#FFFFFF"))
    return canvas


def scene_listen(time: float) -> Image.Image:
    canvas = gradient("#EAF5FF", "#FFF0F7")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    centered_text(draw, "Listen, say it,\nand remember it", (WIDTH // 2, 170), 62, rgba("#17245A"))
    rounded(draw, (100, 405, 980, 1325), 62, rgba("#FFFFFF"), rgba("#D9E9FF"), 3)
    sounds = [
        ("animal_dog.png", "Animal Sounds"),
        ("alphabet_example_apple.png", "Kids Stories"),
        ("home_rainbow.png", "Rhymes & songs"),
    ]
    for index, (asset, title) in enumerate(sounds):
        y = 465 + index * 270
        rounded(draw, (160, y, 920, y + 210), 42, rgba(["#DBF1FF", "#FFE2EE", "#E1F8DE"][index]), None)
        paste_center(canvas, load_resource(asset, nodpi=asset.startswith("alphabet_")), (305, y + 105), 180, 150)
        centered_text(draw, title, (610, y + 75), 34, rgba("#1D2B63"))
        draw.ellipse((565, y + 115, 655, y + 205), fill=rgba("#2D70E8"))
        draw.polygon(((598, y + 137), (598, y + 183), (636, y + 160)), fill=rgba("#FFFFFF"))
    centered_text(draw, "Every tap turns into a new word.", (WIDTH // 2, 1485), 38, rgba("#586899"), bold=False)
    return canvas


def scene_outro(time: float) -> Image.Image:
    local = time - 23
    canvas = gradient("#5A3DD9", "#237EE8")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    logo = rounded_art(load_resource("splash_logo.png", nodpi=True), 64)
    pulse = 0.94 + math.sin(local * 4) * 0.025
    logo = logo.resize((round(logo.width * pulse), round(logo.height * pulse)), Image.Resampling.LANCZOS)
    paste_center(canvas, logo, (WIDTH // 2, 680), 560, 560)
    centered_text(draw, "Children Books", (WIDTH // 2, 1125), 72, rgba("#FFFFFF"))
    centered_text(draw, "Read  •  Listen  •  Learn", (WIDTH // 2, 1225), 40, rgba("#EAF4FF"), bold=False)
    rounded(draw, (170, 1370, 910, 1510), 68, rgba("#FFFFFF"), None)
    centered_text(draw, "Made for curious little minds", (WIDTH // 2, 1440), 36, rgba("#315ED0"))
    centered_text(draw, "Learn  •  Play  •  Grow", (WIDTH // 2, 1650), 38, rgba("#EAF4FF"))
    return canvas


def frame_at(time: float) -> Image.Image:
    if time < 4:
        return scene_intro(time)
    if time < 8:
        return scene_home(time)
    if time < 13:
        return scene_explore(time)
    if time < 19:
        return scene_play(time)
    if time < 23:
        return scene_listen(time)
    return scene_outro(time)


def render_original_music() -> None:
    """Create a gentle, original chime accompaniment without external media."""
    sample_rate = 44_100
    samples = np.zeros(sample_rate * DURATION, dtype=np.float64)
    melody = [523.25, 659.25, 783.99, 659.25, 587.33, 698.46, 880.0, 698.46]
    for beat, frequency in enumerate(melody * 5):
        start = int((beat * 0.62 + 0.25) * sample_rate)
        if start >= samples.size:
            break
        length = min(int(0.75 * sample_rate), samples.size - start)
        time = np.arange(length, dtype=np.float64) / sample_rate
        envelope = (1.0 - np.exp(-time * 35.0)) * np.exp(-time * 3.8)
        tone = np.sin(2 * np.pi * frequency * time)
        overtone = 0.32 * np.sin(2 * np.pi * frequency * 2.02 * time)
        samples[start : start + length] += (tone + overtone) * envelope * 0.14
    pad = 0.025 * np.sin(2 * np.pi * 130.81 * np.arange(samples.size) / sample_rate)
    samples += pad
    samples = np.clip(samples, -0.92, 0.92)
    stereo = np.column_stack((samples * 0.92, samples)).astype(np.float32)
    with wave.open(str(AUDIO), "wb") as output:
        output.setnchannels(2)
        output.setsampwidth(2)
        output.setframerate(sample_rate)
        output.writeframes((stereo * 32767).astype("<i2").tobytes())


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    render_original_music()
    command = [
        "ffmpeg", "-y", "-loglevel", "error",
        "-f", "rawvideo", "-vcodec", "rawvideo", "-pix_fmt", "rgb24",
        "-s", f"{WIDTH}x{HEIGHT}", "-r", str(FPS), "-i", "-", "-i", str(AUDIO),
        "-c:v", "libx264", "-preset", "medium", "-crf", "20", "-c:a", "aac", "-b:a", "128k",
        "-pix_fmt", "yuv420p", "-movflags", "+faststart", str(OUT),
    ]
    process = subprocess.Popen(command, stdin=subprocess.PIPE)
    assert process.stdin is not None
    for index in range(FPS * DURATION):
        image = frame_at(index / FPS).convert("RGB")
        process.stdin.write(image.tobytes())
    process.stdin.close()
    if process.wait() != 0:
        raise SystemExit("ffmpeg could not render the promo video")
    print(OUT)


if __name__ == "__main__":
    main()
