# FixQuest AI

## Tagline
Turn everyday home problems into interactive repair adventures.

## Inspiration
Home maintenance can feel like a language nobody taught us. A dripping tap or a sticking door can trigger guesswork before we understand the problem. We wanted to make those first observations approachable: a living-room learning game where a family can pause, discuss a choice, and build confidence together.

## What it does
FixQuest AI turns a virtual house into three short learning adventures: The Dripping Tap, The Slow Sink, and The Sticking Door. Each mission combines an illustrated scenario, five challenges, contextual feedback, and an interactive puzzle. Learners sequence safe observations or align a virtual door, then earn XP and a badge. Wrong answers lead to an explanation and another attempt.

The entire experience uses D-pad, Select, and Back. Large typography, bright focus states, narrated prompts, replay controls, and local progress support learning on a television. It runs offline, with no account, subscription, or API key.

QuestGuide is a Smart Learning Guide built from curated, context-aware explanations and hints. It is deterministic and available offline; it is not live generative AI. The product's AI name describes the concept and future direction, not an online model integration in this build.

## How we built it
We built a native Android application targeting Fire OS, using Java, Android Views, Canvas, local JSON, SharedPreferences, and Android TextToSpeech. Native focusable controls support remote navigation. Original procedural illustrations render the house and rooms without downloaded images or third-party art. Mission content drives choices, feedback, sequence puzzles, and a virtual alignment puzzle.

Correct answers earn 20 XP, mission completion adds 50 XP, and a perfect attempt adds 30 XP. Only each mission's best completed result contributes to the total, so replay improves skills without inflating progress. The app requests no permissions and calls no backend or model API.

## Challenges
The main design challenge was making a practical subject work with a television remote. Short choices and small puzzles keep the controls simple while still asking learners to observe and reason. Another challenge was maintaining clear boundaries: the game models selected situations and teaches when to stop, rather than pretending to diagnose a real household fault.

## Accomplishments
Three complete missions, original room illustrations, remote focus navigation, contextual explanations, optional narration, persistent XP, badges, and replay all fit in a small offline application with no runtime service dependencies.

## What we learned
A useful learning coach can begin with well-scoped, curated content. Immediate explanations and a chance to retry create a productive loop. Television navigation also makes information hierarchy concrete: the current question, the focused choice, and the next step must always be clear.

## What's next
More household scenarios reviewed by qualified practitioners; localisation; adjustable text size; more varied virtual puzzles; testing with families and first-time renters. An optional generative coach could be added through a secure backend with strict scope, offline fallback, and clear labelling, after the core learning experience is evaluated.

## Built with
Java, Android SDK, Android Views, Canvas, TextToSpeech, JSON, SharedPreferences, Gradle, Fire OS compatibility.

## Primary track
Fire TV — family entertainment and practical learning.

## Published submission links
- Repository URL: https://github.com/lanray07/FixQuest-AI — public source with the included MIT license.
- Public English tablet rehearsal: https://youtu.be/dGE7lSID0Ng — 2:20, explicitly labelled Fire OS tablet footage; not actual Fire TV or accepted simulator evidence.
- Submitted Devpost entry: https://devpost.com/software/fixquest-ai — submission confirmed on 23 October 2026. The saved online story includes the actual tablet validation results and outstanding Fire TV validation.

Do not claim an AWS integration, live model API, Fire TV hardware test, Appstore publication, or an Open Source mini-challenge contribution that has not actually happened. The Open Source mini challenge asks for an additional qualifying project or contribution alongside the primary submission; the MIT license alone does not establish eligibility.
