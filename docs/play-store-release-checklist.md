# Play Store Release Checklist

App ID: `com.childrenbooks` · Current release: **1.2.0 (versionCode 6)**, branch `redesign/v2`

## Build

- Build a signed Android App Bundle: `./gradlew :app:bundleRelease`, or Android Studio > Build > Generate Signed App Bundle or APK.
- Configure upload signing with either `keystore.properties` or these environment variables:
  - `CHILDREN_BOOKS_STORE_FILE`
  - `CHILDREN_BOOKS_STORE_PASSWORD`
  - `CHILDREN_BOOKS_KEY_ALIAS`
  - `CHILDREN_BOOKS_KEY_PASSWORD`
- Do not commit the `.jks`, `.keystore`, `.p12`, `keystore.properties` or `google-services.json` files.
- Before uploading, run `./gradlew :app:testDebugUnitTest :app:lintRelease` (39 unit tests).
- Expected release size: about 11–12 MB (pictures are WebP, sounds are OGG). If the AAB is far bigger, check that no PNG/WAV crept back into `res/`.

## Privacy Policy

- `docs/privacy-policy.md` was updated for 1.2.0: it now lists what is stored only on the device (progress, settings, optional first name) and the grown-up report sharing.
- Publish that text at the public policy URL (the Blogspot page) **before** submitting 1.2.0, and keep the in-app Privacy policy link pointing to the same URL.

## Data Safety

Nothing is sent off the device, so the Data safety answers stay "no data collected, no data shared". Confirm the release still matches:

- No account creation, no third-party ads, no analytics or crash SDKs.
- No permissions (no internet, location, contacts, camera, microphone or storage).
- Progress, settings and the optional child's name are stored only on the device (Play does not count on-device-only data as collected).
- Report sharing uses the system share sheet chosen by a grown-up; the app transmits nothing itself.
- Text-to-Speech is handled by the device speech engine.

## Families

- Target age groups: 5 and under, 6–8 (matches the in-app age bands 3–5 and 6–8).
- Keep the Grown-ups area and every external action (privacy policy, rate, share, more apps, report sharing) behind the parent check.
- Use child-safe store listing text, screenshots and graphics.

## Store Listing

- Release notes: `docs/release-notes/1.2.0.txt` (under 500 characters).
- **Replace the screenshots**: 1.2.0 has a completely new design. Suggested set: Home, a Letters lesson card, Practice, Reward with sticker, Listen (Bedtime), Play hub, Grown-ups report.
- The Goldie screenshot flows in `goldie/` and `.argent/flows/` still follow the 1.1.x screens and need updating before they can be reused.

## Release QA (1.2.0)

- First run: hello → age choice → Home.
- Home: greeting, Up next / Keep going card, 7 worlds, dock (Play, Listen, Stickers), stickers badge.
- Each world: open a set, page through all cards, Hear button and audio, Back/Next.
- Practice: wrong answer → try again, 2 misses → hint, 3 misses → reveal; Reward shows stars and a first-time sticker; Next set / Play again / Home.
- Stickers album: new sticker badge, locked slots.
- Listen: all three shelves play and stop; Bedtime colours, voice and status bar.
- Play: open every game once; back returns to the hub.
- Grown-ups: parent gate (wrong answers, 30-second lock), report week/month, needs-practice chips open the lesson, share report, settings (name, age, show words, bedtime, reset progress), external links.
- Test on at least one small phone (360 dp wide) and one large phone or tablet, and with the system font size set to large.
