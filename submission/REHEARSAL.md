# Tablet rehearsal video

`FixQuest-AI-tablet-rehearsal.mp4` is a **2 minute 20 second** walkthrough captured from the running app on an Amazon KFTRWI Fire tablet, using actual D-pad key events. It demonstrates the home, mission selection, an incorrect answer, contextual hint, toolkit, sequence puzzle, badge, virtual door alignment and local progress.

The video has English captions, locally generated Microsoft Hazel narration, and a persistent label identifying the tablet and the outstanding Fire TV recording requirement. The narration is editorial voice-over added after capture; it is **not** a recording or verification of the app's Android TextToSpeech output.

The device's raw screen recording is 1280×720. The delivered export is scaled to 1920×1080 at 30 fps, with a duration of 140.37 seconds. The still screenshots are separate native 1920×1080 captures.

This rehearsal helps review pacing and features. It does **not** meet the event's requirement for actual Fire TV / accepted Fire TV or Vega simulator footage. No public video upload has been made.

`tablet-rehearsal-captions.srt` contains the timed English captions. The longer manual capture plan in DEMO-SCRIPT.md is designed for a final Fire TV recording.

## Reproduce the rehearsal on a test install

Build/install the app and instrumentation APK as in the README, clear this app's test progress, then run:

```powershell
adb shell am instrument -w -e demo true com.fixquest.tv.test/com.fixquest.tv.RemoteFlowTest
```

The harness records for 175 seconds while the walkthrough finishes around 140 seconds. It writes the raw video to `/sdcard/fixquest-rehearsal.mp4` and the chapter data to the app's external-files `demo/chapters.json`. Pull these into `captures/tablet-rehearsal-raw.mp4` and `submission/rehearsal-work/chapters.json` respectively. On Windows, `python tools/build_rehearsal.py` adds captions, provenance labelling, and local narration. This optional media step requires Pillow, imageio-ffmpeg and the Microsoft Hazel Desktop system voice. These are not application dependencies.
