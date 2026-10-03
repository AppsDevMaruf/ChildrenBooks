#!/usr/bin/env python3
"""Render a fast, screen-led promotional video for Children Books."""

from __future__ import annotations

import math
import subprocess
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
REFERENCE = ROOT / "promo/reference_screens.png"
AUDIO = ROOT / "promo/children_books_original_chime.wav"
OUT = ROOT / "promo/children_books_updated_promo.mp4"
WIDTH, HEIGHT, FPS, DURATION = 1080, 1920, 24, 16
FONT_BOLD = "/System/Library/Fonts/Supplemental/Arial Rounded Bold.ttf"
FONT_REGULAR = "/System/Library/Fonts/Supplemental/Arial.ttf"


def color(value: str, alpha: int = 255) -> tuple[int, int, int, int]:
    value = value.lstrip("#")
    return tuple(int(value[index : index + 2], 16) for index in (0, 2, 4)) + (alpha,)


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    try:
        return ImageFont.truetype(FONT_BOLD if bold else FONT_REGULAR, size)
    except OSError:
        return ImageFont.truetype(FONT_REGULAR, size)


def ease(value: float) -> float:
    value = max(0.0, min(1.0, value))
    return value * value * (3.0 - 2.0 * value)


def gradient(top: str, bottom: str) -> Image.Image:
    first, second = color(top), color(bottom)
    image = Image.new("RGBA", (WIDTH, HEIGHT))
    draw = ImageDraw.Draw(image)
    for y in range(HEIGHT):
        amount = y / (HEIGHT - 1)
        draw.line(
            (0, y, WIDTH, y),
            fill=tuple(round(first[index] + (second[index] - first[index]) * amount) for index in range(4)),
        )
    return image


def center_text(draw: ImageDraw.ImageDraw, text: str, x: int, y: int, size: int, fill, bold: bool = True) -> None:
    active_font = font(size, bold)
    box = draw.multiline_textbbox((0, 0), text, font=active_font, spacing=8, align="center")
    draw.multiline_text((x - (box[2] - box[0]) / 2, y - (box[3] - box[1]) / 2), text, font=active_font, fill=fill, spacing=8, align="center")


def resource(name: str, nodpi: bool = True) -> Image.Image:
    folder = "drawable-nodpi" if nodpi else "drawable"
    return Image.open(RES / folder / name).convert("RGBA")


def rounded_image(image: Image.Image, radius: int) -> Image.Image:
    mask = Image.new("L", image.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, image.width, image.height), radius=radius, fill=255)
    output = image.copy()
    output.putalpha(ImageChops.multiply(output.getchannel("A"), mask))
    return output


def fit(image: Image.Image, max_width: int, max_height: int) -> Image.Image:
    ratio = min(max_width / image.width, max_height / image.height)
    return image.resize((round(image.width * ratio), round(image.height * ratio)), Image.Resampling.LANCZOS)


def paste_center(canvas: Image.Image, image: Image.Image, x: int, y: int, max_width: int, max_height: int, alpha: float = 1.0) -> None:
    visual = fit(image, max_width, max_height)
    if alpha < 1.0:
        visual.putalpha(visual.getchannel("A").point(lambda value: round(value * alpha)))
    canvas.alpha_composite(visual, (round(x - visual.width / 2), round(y - visual.height / 2)))


def confetti(draw: ImageDraw.ImageDraw, time: float) -> None:
    dots = [(72, 205, "#FFC93C"), (1005, 255, "#F76F9B"), (88, 1515, "#55BDF7"), (982, 1450, "#8C66E9")]
    for index, (x, y, shade) in enumerate(dots):
        move = math.sin(time * 3 + index) * 14
        draw.ellipse((x - 12, y - 12 + move, x + 12, y + 12 + move), fill=color(shade))


def panel_crops() -> tuple[Image.Image, Image.Image, Image.Image, Image.Image]:
    source = Image.open(REFERENCE).convert("RGBA")
    return (
        source.crop((10, 8, 430, 878)),
        source.crop((455, 8, 875, 878)),
        source.crop((900, 8, 1320, 878)),
        source.crop((1345, 8, 1765, 878)),
    )


PANELS = panel_crops()


def screen_scene(time: float, start: float, end: float, panel: Image.Image, title: str, subtitle: str, top: str, bottom: str) -> Image.Image:
    canvas = gradient(top, bottom)
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    local = (time - start) / (end - start)
    fade = min(ease(local / 0.18), ease((1 - local) / 0.14))
    center_text(draw, title, WIDTH // 2, 155, 59, color("#17255E", round(255 * fade)))
    center_text(draw, subtitle, WIDTH // 2, 255, 31, color("#5E6B9D", round(255 * fade)), bold=False)
    scaled = fit(rounded_image(panel, 42), 720, 1370)
    shift = round((1 - ease(local / 0.25)) * 85)
    frame = (WIDTH // 2 - scaled.width // 2 - 22 + shift, 350 - 22, WIDTH // 2 + scaled.width // 2 + 22 + shift, 350 + scaled.height + 22)
    draw.rounded_rectangle(frame, radius=58, fill=color("#FFFFFF", round(255 * fade)), outline=color("#FFFFFF", round(220 * fade)), width=3)
    if fade < 1:
        scaled.putalpha(scaled.getchannel("A").point(lambda value: round(value * fade)))
    canvas.alpha_composite(scaled, (WIDTH // 2 - scaled.width // 2 + shift, 350))
    return canvas


def intro(time: float) -> Image.Image:
    canvas = gradient("#7451E6", "#2C78E8")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    logo = rounded_image(resource("splash_logo.png"), 52)
    entrance = ease(time / 0.35)
    paste_center(canvas, logo, WIDTH // 2, 620, 510, 510, entrance)
    center_text(draw, "Children Books", WIDTH // 2, 1040, 74, color("#FFFFFF", round(255 * entrance)))
    center_text(draw, "Learn • Play • Grow", WIDTH // 2, 1135, 39, color("#EAF4FF", round(255 * entrance)), bold=False)
    center_text(draw, "A joyful learning app for kids", WIDTH // 2, 1460, 38, color("#FFFFFF", round(255 * entrance)))
    return canvas


def outro(time: float) -> Image.Image:
    canvas = gradient("#4D39C8", "#1478E8")
    draw = ImageDraw.Draw(canvas)
    confetti(draw, time)
    center_text(draw, "Ready to explore?", WIDTH // 2, 235, 66, color("#FFFFFF"))
    for index, panel in enumerate(PANELS[1:]):
        art = fit(rounded_image(panel, 28), 260, 520)
        x = 190 + index * 350
        draw.rounded_rectangle((x - art.width // 2 - 10, 460, x + art.width // 2 + 10, 460 + art.height + 20), radius=36, fill=color("#FFFFFF"))
        canvas.alpha_composite(art, (x - art.width // 2, 470))
    center_text(draw, "Children Books", WIDTH // 2, 1440, 72, color("#FFFFFF"))
    center_text(draw, "Read • Listen • Learn", WIDTH // 2, 1535, 38, color("#EAF4FF"), bold=False)
    draw.rounded_rectangle((180, 1670, 900, 1805), radius=68, fill=color("#FFFFFF"))
    center_text(draw, "Made for little minds", WIDTH // 2, 1735, 38, color("#315ED0"))
    return canvas


def frame_at(time: float) -> Image.Image:
    if time < 2:
        return intro(time)
    if time < 6:
        return screen_scene(time, 2, 6, PANELS[1], "Explore & Learn", "Alphabet • Numbers • Animals", "#EAF5FF", "#FFFFFF")
    if time < 10:
        return screen_scene(time, 6, 10, PANELS[2], "Fun Activities", "Trace • Match • Puzzle • Quiz", "#E9F9EF", "#E7F5FF")
    if time < 14:
        return screen_scene(time, 10, 14, PANELS[3], "Listen & Enjoy", "Stories • Sounds • Rhymes", "#FFF4DD", "#FFF0F8")
    return outro(time)


def main() -> None:
    if not REFERENCE.exists():
        raise SystemExit(f"Missing reference screen asset: {REFERENCE}")
    if not AUDIO.exists():
        raise SystemExit(f"Missing original soundtrack: {AUDIO}")
    command = [
        "ffmpeg", "-y", "-loglevel", "error", "-f", "rawvideo", "-pix_fmt", "rgb24",
        "-s", f"{WIDTH}x{HEIGHT}", "-r", str(FPS), "-i", "-", "-i", str(AUDIO),
        "-t", str(DURATION), "-c:v", "libx264", "-preset", "medium", "-crf", "19",
        "-c:a", "aac", "-b:a", "128k", "-pix_fmt", "yuv420p", "-movflags", "+faststart", str(OUT),
    ]
    process = subprocess.Popen(command, stdin=subprocess.PIPE)
    assert process.stdin is not None
    for index in range(FPS * DURATION):
        process.stdin.write(frame_at(index / FPS).convert("RGB").tobytes())
    process.stdin.close()
    if process.wait() != 0:
        raise SystemExit("ffmpeg could not render the fast screen demo")
    print(OUT)


if __name__ == "__main__":
    main()
