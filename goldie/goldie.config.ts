import type { GoldieConfig } from "goldie";

const APP_ROOT = "/Users/marufalam/StudioProjects/ChildrenBooks";
const RELEASE_APK = `${APP_ROOT}/app/build/outputs/apk/release/app-release.apk`;

const config: GoldieConfig = {
  appRoot: APP_ROOT,
  appPath: RELEASE_APK,
  bundleId: "com.childrenbooks",
  android: {
    appPath: RELEASE_APK,
    applicationId: "com.childrenbooks",
  },
  devices: ["pixel-10-pro"],
  locales: ["en-US"],
  appearance: "light",
  frame: { variant: "17-pro-blue" },
  theme: {
    background: "linear-gradient(160deg, #E6F5FF 0%, #FFFFFF 52%, #F3EEFF 100%)",
    headlineColor: "#17255E",
    subheadColor: "#53658F",
    fontFamily: '"Nunito", "DM Sans", system-ui, sans-serif',
    copyHeightRatio: 0.25,
    deviceWidthRatio: 0.84,
    template: ["hero", "offset", "tilt-right", "minimal", "copy-below"],
  },
  store: {
    name: "Children Books",
    subtitle: { "en-US": "Learn, play and grow" },
    developer: "Children Books",
    category: "Education",
    rating: 4.8,
    ratingCount: "New",
    ageRating: "3+",
    price: "Free",
    description: {
      "en-US": "Children Books turns everyday learning into a joyful, colorful adventure. Little learners can explore letters, numbers, animals, seasons, colors, sounds and more.\n\nDesigned for young children with clear navigation, friendly illustrations and playful activities that encourage curiosity.",
    },
  },
  scenes: [
    {
      kind: "screenshot",
      id: "home",
      flow: "store-01-home",
      headline: { "en-US": "A joyful world of learning" },
      subhead: { "en-US": "Letters, numbers, animals and everyday discoveries." },
    },
    {
      kind: "screenshot",
      id: "alphabet",
      flow: "store-02-alphabet",
      headline: { "en-US": "Learn letters with confidence" },
      subhead: { "en-US": "Tap, listen and explore every colorful letter." },
    },
    {
      kind: "screenshot",
      id: "animals",
      flow: "store-03-animals",
      headline: { "en-US": "Meet animals and more" },
      subhead: { "en-US": "Friendly pictures and sounds bring words to life." },
      layout: "classic",
    },
    {
      kind: "screenshot",
      id: "games",
      flow: "store-04-games",
      headline: { "en-US": "Playful games build skills" },
      subhead: { "en-US": "Match, trace, solve puzzles and learn by doing." },
    },
    {
      kind: "screenshot",
      id: "listen",
      flow: "store-05-listen",
      headline: { "en-US": "Stories, sounds and rhymes" },
      subhead: { "en-US": "Listen, repeat and grow a love for learning." },
      layout: "classic",
    },
  ],
};

export default config;
