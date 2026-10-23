# Product feedback — review before pasting

Only submit observations that match the final device test. These notes distinguish actual experience from follow-up testing.

## Android SDK / native platform
Used for: native UI, landscape rendering, focusable controls, local progress, APK packaging, and system text-to-speech.

Worked well: a dependency-light Java/Views implementation built with the locally available JDK and Android SDK. Canvas kept the illustrations original and scalable. Native focus navigation let us exercise remote choices with D-pad key events.

Needs work: the distinction between Fire TV compatibility checks and a Fire OS tablet test is easy to miss. A concise current matrix covering Fire OS levels, TV testing options, launcher assets, and narration availability would reduce setup uncertainty.

Onboarding: Java and the SDK were already installed. Cached Gradle and Android build tooling made a small native project practical under time pressure.

Would use again: yes, for an offline television learning experience with a small dependency footprint.

## Amazon Fire OS device testing
Used for: installing and launching the APK on a connected Amazon KFTRWI, Android API 28, and checking native navigation. This is a Fire tablet; it is not Fire TV certification or proof of Fire TV remote behaviour on real TV hardware.

Worked well: ADB installation and direct activity launch were available on the existing device.

Remaining: add observations from the final actual Fire TV or accepted simulator run. Do not invent TV-specific onboarding or SDK experience.

## Feature request
Important: a clearly signposted Fire TV validation checklist and readily available lightweight test environment for Android APKs, including a remote input overlay and TTS availability information.

## Friction log
| Task | Expected | Observed | Severity | Workaround / suggestion |
|---|---|---|---|---|
| Identify a suitable demo target | ADB target suitable for the Fire TV submission | Connected Amazon hardware was a KFTRWI tablet | Important | Query manufacturer, model and SDK with ADB; explicitly distinguish tablet testing from required TV footage in the onboarding checklist |
| Build API-22-compatible theme | Shared style works on all supported Fire OS bases | Android lint flagged a newer status-bar style attribute | Minor | Remove the unused attribute; provide a minimal current TV manifest/theme example |

No AWS runtime, Alexa+, Ring, Bee, or live generative model integration is claimed.

## Build and media tooling

- **Codex:** used to create the native application, mission content, automation harness, and submission materials. Direct access to the local SDK and connected device made rapid build-and-test iteration possible. The useful boundary was keeping device-test evidence separate from assumptions about Fire TV. Would use again, while retaining a final hardware and submission review.
- **Gradle / Android Gradle Plugin / JDK 17:** used for reproducible compilation, lint, APK signing and packaging. Existing compatible cached versions reduced setup time. Lint caught API-level and resource issues before handoff. Would use again; retain a small documented known-working version set.
- **ADB and Android instrumentation:** used for install, launch, D-pad events, screenshots, restart checks, and the real screen recording. The first dialog test needed explicit handling of its separate window; the public accessibility tree provided that. Would use again for automated remote-flow coverage, alongside physical remote testing.
- **Windows System.Speech / Microsoft Hazel Desktop:** used only for the rehearsal's editorial English voice-over. Local synthesis avoided credentials and cloud setup. Installed voice availability is machine-specific; document that dependency. This did not validate Android narration.
- **Pillow / imageio-ffmpeg / FFmpeg:** used to add a provenance label, English captions and audio to real captured footage. The device recording had sparse variable-rate frames; converting to a constant 30 fps kept subtitle timing reliable. Would use again for a small reproducible local rehearsal workflow.
