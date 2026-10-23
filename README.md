# FixQuest AI

**Turn everyday home problems into interactive repair adventures.**

A native, offline-first learning game for Fire TV. Explore an illustrated home, solve five challenges in each of three rooms, and collect experience points and badges. Everything is operated with D-pad, Select, and Back. No login, backend, network permission, or API key.

## Build and install

Requires JDK 17, Android SDK Platform 35 and Build Tools 35, and internet for the first Gradle dependency download. Java/Android Views were chosen for minimal dependencies and compatibility with Fire OS. Minimum API 22; target API 35.

1. Open this folder in Android Studio, or set `ANDROID_HOME` to your SDK directory. Android Studio can generate the machine-specific `local.properties`.
2. Run `./gradlew :app:assembleDebug :app:lintDebug` (`.\gradlew.bat` on Windows).
3. Install `app/build/outputs/apk/debug/app-debug.apk` using `adb install -r <apk>`.
4. Launch `adb shell am start -n com.fixquest.tv/.MainActivity` or choose FixQuest AI in the device launcher.

The demo APK uses an Android debug signing certificate. It is for sideloading and hackathon evaluation, not an Appstore production release. The release build is unsigned; use your own production signing key for distribution. Never commit signing keys. On Unix systems, make the wrapper executable with `chmod +x gradlew` if the archive extractor does not retain executable permissions.

## Play

- **The Dripping Tap:** observe a drip, consider the seal, choose a tool, sequence water isolation in a model, recognise when to stop.
- **The Slow Sink:** observe drainage, consider debris, choose a gentle inspection, sequence safe checks, learn prevention.
- **The Sticking Door:** inspect gaps, check model hinges, select a matching tool, align a virtual door, recognise signs needing qualified help.

Correctly solving a challenge earns 20 XP, finishing a mission earns 50 XP, and a run without mistakes adds 30 XP. Incorrect choices get contextual feedback and a retry. Hints do not cost XP. The highest completed score for each mission is saved using SharedPreferences, so replaying does not farm XP. Maximum total: 540 XP. Badges and scores survive app restarts. An unfinished attempt restarts after leaving it; instance state survives activity recreation.

**QuestGuide is curated deterministic content, not live generative AI.** It uses the active mission and challenge to offer an explanation and hint. Android TextToSpeech reads prompts and feedback when an appropriate system voice is available. How It Works contains narration and pace settings. Repeat and hint controls are available during each challenge. All teaching text remains visible if no TTS engine or voice is installed. The app requires no network; the system TTS service's available offline voices depend on the device.

This is an educational simulation, not professional repair advice or diagnostics. It teaches observation, safe limits, and when to seek qualified help. It avoids chemical mixing, electrical work, and instructions to alter fire doors.

## Source map

- `MainActivity.java`: native focusable UI, navigation, local progress, narration, scoring.
- `SceneView.java`: original procedural Canvas illustrations; no downloaded art.
- `assets/missions.json`: three complete missions, answers, hints, puzzles, explanations.
- `src/androidTest/.../RemoteFlowTest.java`: UI flow test using real D-pad key events.
- `submission/`: Devpost copy, video script, screenshots, test evidence, and delivery checklist.

## Verify on a connected device

Use a dedicated test install: the comprehensive flow expects **fresh progress** and intentionally earns all badges. The command below clears only this app's data.

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell pm clear com.fixquest.tv
adb shell am instrument -w com.fixquest.tv.test/com.fixquest.tv.RemoteFlowTest
```

The UI test finds a path through native directional focus navigation and sends real D-pad key events for every choice; it does not click by coordinates or request focus on its target. Dialog focus is verified through the public accessibility tree. The application and test use public platform APIs only.

## Submission status

Submitted to the Build, Ship, Shape: Amazon Developer Hackathon on 23 October 2026; Devpost confirmed "Project submitted!".

- [Devpost entry](https://devpost.com/software/fixquest-ai)
- [Public tablet rehearsal demo](https://youtu.be/dGE7lSID0Ng)

See `submission/CHECKLIST.md` and `submission/TEST-REPORT.md` for actual verification and remaining external steps. A Fire OS tablet is useful for compatibility testing but does **not** meet the hackathon requirement to demonstrate on an actual Fire TV or Fire TV/Vega simulator. Do not label tablet footage as Fire TV evidence.

A labelled tablet rehearsal with English captions and local editorial voice-over is included for review; see `submission/REHEARSAL.md`. The voice-over is separate from the app's optional Android narration. Run `python tools/package.py` after building to recreate the portable source archive, APK copy, checksums and handoff ZIP.

Official references checked 23 October 2026:

- [Hackathon requirements](https://amazonappdev2026.devpost.com/)
- [Amazon Fire TV differences from Android TV](https://developer.amazon.com/docs/fire-tv/differences-from-android-tv-development.html)
- [Amazon device filtering and compatibility](https://developer.amazon.com/docs/app-submission/device-filtering-and-compatibility.html)

## Privacy

The app stores only local scores, badges, narration preference, and speech pace. No analytics, accounts, advertising, data upload, or runtime permissions. Uninstalling or clearing app storage removes progress. No external assets or model services are called.

