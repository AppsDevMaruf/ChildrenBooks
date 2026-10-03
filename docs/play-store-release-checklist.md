# Play Store Release Checklist

App ID: `com.childrenbooks`

## Build

- Build a signed Android App Bundle with `./gradlew :app:bundleRelease`.
- Configure upload signing with either `keystore.properties` or these environment variables:
  - `CHILDREN_BOOKS_STORE_FILE`
  - `CHILDREN_BOOKS_STORE_PASSWORD`
  - `CHILDREN_BOOKS_KEY_ALIAS`
  - `CHILDREN_BOOKS_KEY_PASSWORD`
- Do not commit the `.jks`, `.keystore`, `.p12`, or `keystore.properties` files.

## Privacy Policy

- Publish `docs/privacy-policy.md` at a public URL before Play submission.
- Add the same public privacy policy URL in Play Console and keep the app Settings policy button aligned with that URL.

## Data Safety

Use the final Play Console answers only after confirming the release build still matches this behavior:

- No account creation.
- No third-party ads.
- No analytics SDK.
- No location, contacts, camera, microphone, storage, or phone permissions.
- Text-to-Speech is handled by the device speech engine.

## Families

- Select the correct target age groups for a kids learning app.
- Declare that the app is child-directed if it targets children.
- Keep all external links, sharing and rating behind the parent check.
- Use child-safe store listing text, screenshots, and graphics.

## Release QA

- Test all home categories.
- Test Alphabet and Number swipe/navigation/audio.
- Test Listen categories: Animal Sounds, Kids Stories, Rhymes.
- Test Play games: Trace Letter, Match Game, Drag & Drop, Quiz Mode, Find Object.
- Test Settings parent gate for all external actions.
- Test on at least one small phone and one large phone.
