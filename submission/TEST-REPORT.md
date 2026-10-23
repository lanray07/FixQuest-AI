# Verification report — 23 October 2026

## Target
Connected **Amazon KFTRWI Fire tablet, Android API 28**. This is not a Fire TV. Tests used ADB-injected D-pad key events, rather than touchscreen coordinates. A temporary display-size override produced a 1920×1080 landscape viewport; the physical display is 1920×1200 in landscape.

## Build
- Gradle 8.13, Android Gradle Plugin 8.11.1, JDK 17.
- Compile / target SDK 35, minimum SDK 22.
- Debug APK successfully built, installed, and launched.
- Portable source archive extracted to a fresh temporary directory and built successfully with `:app:assembleDebug --offline`, using ANDROID_HOME and no local.properties. See portable-source-build.log.
- `:app:lintDebug`: **0 errors**, 9 advisory warnings, including newer tooling availability, landscape orientation, vector banner size, and draw allocations. See build-validation.log.
- APK signing verified using v1 and v2 signatures. Debug signing is intended for sideload evaluation.
- APK manifest inspected: landscape activity, standard and Leanback launchers, banner/icon, optional touchscreen/faketouch, no requested permissions, no runtime network dependency.

## End-to-end D-pad flow
**PASS: 95 Select actions.** The test searches the native focus graph for a route, sends the actual directional key events, and selects the focused control. Dialogs are inspected with the public accessibility tree.

- Home menu, mission selection, progress and help reachable.
- Narration toggle, speech pace and Repeat reachable.
- Tap: incorrect answer, retry feedback, hint dialog, repeat, Back cancellation, all decisions and the three-action sequence.
- Tap score: 150 XP after a mistake; perfect replay improves its record to 180.
- Sink: all five challenges and three-action safe-inspection sequence; 180 XP.
- Door: rejected misalignment, three marker adjustments, all questions and completion; 150 XP with a mistake.
- Total after those runs: 510 XP with three badges. A perfect door replay improves the total to 540 XP, without duplicating previous XP.
- Leaving an unfinished attempt returns to mission selection; Back then returns home.
- Local best-score data checked; app restart independently showed 540 XP and all three badges after the complete test run. The later fresh-install rehearsal completed two missions; restarting afterward retained 330 XP and two badges.

See remote-flow-test.log for the device result. The final gameplay build passed after the illustration and offline-voice changes. The optional rehearsal mode was added to the instrumentation harness afterward; it does not change application code.

## Visual checks
Inspected real 1920×1080 app captures for the home, mission cards, question/feedback panels, door alignment puzzle, illustrated toolkit, completion and progress screens. Fixed immersive mode restoration after dialogs. Screenshots are in screenshots/, clearly identified as Fire tablet captures. No mockup is represented as a TV device.

## Narration limits
Android TextToSpeech is implemented with language fallback, offline English voice selection, on/off and pace settings, and Repeat. Installed Ivona packages were detected on the test device. UI controls were tested; audible output was **not independently verified**. The app keeps all text visible and indicates when an offline voice is unavailable. Verify the final TV's installed voice and volume before recording a narration demonstration.

## Outstanding validation
- Actual Fire TV / accepted Fire TV or Vega simulator run: **blocked; user confirmed no target is available**.
- Hardware remote, TV overscan and TV-specific TTS: not verified on a Fire TV.
- API-22 device and newer Android device coverage: not executed. Lint checks minimum-API compatibility.
- Large system-font settings and screen-reader usability: not independently exercised.
- Appstore production signing, certification and publication: not performed.

The build is suitable for sideload testing. Full hackathon submission readiness still depends on the required Fire TV demo footage, repository publication/access, public video URL, and final Devpost submission.

## Rehearsal media verification
The delivered labelled tablet video is 140.37 seconds, 1920×1080, H.264 at a constant 30 fps, with AAC English editorial voice-over. The raw device video is 1280×720 and is scaled for the final export. Representative frames were inspected for captions, provenance label, real UI and correct timing. Audio stream levels were checked: mean approximately -18.7 dB, peak -1.1 dB. This checks the added voice-over signal, not the app's TextToSpeech audio.
