# Children Books 1.2.0 promo video

`children_books_promo_1.2.0.mp4`: portrait 1080×1920, 33 s, 30 fps, H.264 + AAC, about 6.7 MB.
It is made for the Google Play preview video (upload to YouTube, then paste the link in Play Console).

## Contents
- Real screens of the 1.2.0 app, captured from a Samsung SC-51A via Android Studio's phone mirror (`screens/`).
  Status bars were replaced with a clean "9:41" bar; `home_scroll.jpg` is Home stitched with the worlds grid for the scroll shot.
- `music.py` writes the original background music (`promo_music.wav`, synthesised with numpy/scipy, no samples or third-party audio).
- `promo.html` is the animation: every frame is drawn by `render(t)`; uses the app's own Nunito fonts and icon.
- `render.js` renders 990 JPEG frames with Playwright; `preview.js` renders a few still frames for checking.

## Rebuild
```
cd promo/v2
python3 music.py                       # -> promo_music.wav
npm i -D playwright && node render.js  # -> frames/
ffmpeg -y -framerate 30 -i frames/f%05d.jpg -i promo_music.wav -map 0:v -map 1:a \
  -c:v libx264 -preset slow -crf 18 -pix_fmt yuv420p -movflags +faststart \
  -c:a aac -b:a 192k -af "afade=t=out:st=31.4:d=1.6" -t 33 children_books_promo_1.2.0.mp4
```

## Scenes
0–2 s logo · 2–6 Home and 7 worlds · 6–10 lesson cards · 10–14 practice with hints · 14–18 reward and stickers ·
18–22 Listen and Bedtime mode · 22–26 games · 26–30 grown-ups (parent check, report) · 30–33 end card.
