# Kalc — OpenCode Handoff Document
# Created: 2026-09-21 (Hermes session with Jarvis persona)
# Purpose: Full transfer of all session knowledge, code changes, and findings
# to OpenCode for continued work.

================================================================================
SESSION CONTEXT
================================================================================
Persona: "Jarvis" (app development focus)
Date: 2026-09-21
App: Kalc v2.2.0 (versionCode 5)
Goal: Audit, fix all issues, implement Apple's design language from Kraft Standards
Status: Phase 1 (fixes) DONE. Phase 2 (token wiring) PARTIALLY DONE.

================================================================================
KRAFT STANDARDS (your rules — created for Apple design implementation)
================================================================================
MANIFESTO.md: "Software, built right." — craft, privacy by default, Apple-quality,
reliable, minimal footprint, tested not hoped.

PRINCIPLES.md (10, priority order):
Zero Trust, Lightweight, Robust, Reliable, Secure, Accessible, Offline-First,
Predictable, Battery-Conscious, Testable.

DESIGN.md (Apple HIG-inspired):
- Colors: never hardcode, one accent per view, destructive red only for destructive
- Typography: SF Pro/Roboto scale, largeTitle 34 → caption2 11, bold=interactive only
- Spacing: 8px rhythm, 16px edges/card padding, 8px icon-label
- Motion: 350ms easeInOutCubic push, 400ms sheets, 250ms entry/200ms exit,
  100ms touch, spring 220/25, respect ReduceMotion
- 44dp min touch targets
- Components: sheets (drag handle, sentence case, primary pinned bottom),
  cards (radius 12/20/8, no elevation), buttons, toggles, lists (44h rows)

ARCHITECTURE.md: 3-layer strict (PRES→DOMAIN→DATA). Domain zero UI imports.
Immutable state, state down/events up.

STANDARDS.md: kebab-case files, PascalCase classes, camelCase fns/vars,
is/has/can/should bool prefix, 2-space indent, 80-char comments/120-char code,
trailing commas, K&R braces, comments explain WHY not WHAT, doc comments on
public APIs, no commented-out code, no TODO without issue, Result<T> over
exceptions, never swallow errors, immutable state, no global mutable state,
no stored BuildContext, const constructors, .builder() lists, width/height for
images, avoid Opacity/ClipPath, conventional commits <72 chars imperative mood.

CHECKLIST.md: 38 items across 8 sections (Code Quality, Testing, Security,
Performance, Design, Build & Release, Documentation, Post-Release).

================================================================================
AUDIT RESULTS
================================================================================
Score: 75/100 → 83/100 (after Phase 1 fixes)
Architecture 79, Code Quality 74, Design 72, Principles 80, Checklist 60.

P0 (FIXED): PRIVACY.md created, proguard-rules cleaned of Flutter rules.
P1 (FIXED): Dark theme hardcoded → reads from settings, saveHistory hardcoded
  100 → uses settings.historySize, double-lock race fixed, duplicate import
  removed, CI now runs tests.
P2 (FIXED): Two theme systems consolidated, files renamed (Legacy/Room repos),
  CHANGELOG.md created.
P3 (NOT DONE): No auto-formatter, ui-tooling-preview wrong config, no docs/ dir.

================================================================================
CODE CHANGES (12 files, 349 insertions, 173 deletions)
================================================================================
CONFIG: ci.yml (+testDebugUnitTest), proguard-rules.pro (cleaned)
DATA: HistoryRepository.kt → LegacyHistoryRepository.kt,
       HistoryRepository2.kt → RoomHistoryRepository.kt
THEME: Theme.kt (+spacing1/spacing6/tiny tokens, fromTheme() fn)
PRES: CalculatorScreen (theme→settings), CalculatorDisplay (DisplayFontSizes,
       DisplayMinHeight, full tokens), CalculatorButton (CalculatorFontSizes,
       uppercase enums, better comments), BasicKeypad (enum fix),
       ScientificKeypad (all enum refs fixed), SettingsScreen (theme picker works)

NEW FILES: PRIVACY.md, CHANGELOG.md, AUDIT.md, OPENCODE_HANDOFF.md (this file)

================================================================================
WHAT IS DONE vs NOT DONE
================================================================================
DONE: All P0/P1/P2 fixes. Token plumbing in CalculatorButton, CalculatorDisplay,
       BasicKeypad, ScientificKeypad, Theme.kt, CalculatorScreen, SettingsScreen.

NOT DONE — token adoption remaining:
- ConverterScreen.kt: 30+ inline values (16.sp, 36.sp, 24.dp, 8.dp, 12.dp)
- HistorySheet.kt: 30+ inline values (22.sp, 18.sp, 15.sp, 24.sp, 44.dp, 56.dp)
- SettingsScreen.kt: MaterialTheme.typography refs, inline 16.dp/12.dp
- KeypadSizing.kt: mostly clean (const val, no UI)

NOT DONE — visual redesign (none of this touched yet):
- NO motion/animations (mode switches instant, no sheet transitions)
- NO visual spacing/typography/color/radius changes
- App looks IDENTICAL on phone — values are same, just referenced differently

================================================================================
BUILD STATUS
================================================================================
Last build: BUILD SUCCESSFUL (50s)
Warnings: MasterKey deprecation (HistoryCrypto.kt), menuAnchor deprecation
          (ConverterScreen.kt) — pre-existing, not from this session.
Build: ./gradlew assembleDebug --no-daemon
Test: ./gradlew testDebugUnitTest --no-daemon
Deploy: adb -s 65KFMBUWLRSS8HN7 install -r app/build/outputs/apk/debug/app-debug.apk
Device: 65KFMBUWLRSS8HN7

================================================================================
REMAINING WORK
================================================================================
1. Finish token adoption in ConverterScreen, HistorySheet, SettingsScreen
2. Visual redesign: motion (350ms push, 400ms sheet, 100ms touch, spring),
   ReduceMotion, typography refinement, component polish
3. Code quality: spotless/ktlint, ui-tooling-preview config, docs/ dir
4. Audit items: dynamic type test, cold start benchmark, perf/memory profiling

================================================================================
APP DETAILS
================================================================================
Kalc v2.2.0 — Private calculator (Android)
GitHub: kedharsairam/kalc
Tech: Kotlin 2.1.0, Compose, Material3, Room, DataStore, Glance
Engine: 672-line Shunting-Yard parser, 67 JUnit tests
Privacy: No internet permission, AES-256-GCM (AndroidKeyStore), backup excluded
Features: Basic+Scientific, trig (DEG/RAD/GRAD), logs/explog/powers/roots,
  factorial, nCr/nPr, π/e/τ, DMS, variables, memory, 9-unit converter,
  EMI + India GST, eng notation, decimal-to-fraction, 4 theme modes
Author: Kedhar Sairam (kedharsairam), Palakollu India, MIT license

================================================================================
KEY FILES
================================================================================
C:\Users\kedhar\projects\kalc\
  app/src/main/java/com/kraft/calculator/presentation/
    CalculatorScreen.kt, CalculatorViewModel.kt, CalculatorDisplay.kt,
    CalculatorButton.kt, BasicKeypad.kt, ScientificKeypad.kt,
    HistorySheet.kt, ConverterScreen.kt, SettingsScreen.kt, KeypadSizing.kt
  app/src/main/java/com/kraft/calculator/ui/theme/
    Theme.kt (= KraftSpacing, KraftRadius, fromTheme(), LocalThemeColors, KraftTheme)
    Color.kt (= AccentBlue/Green/Red/Orange/Purple + variants, ThemeColors, KraftThemeColors)
    Type.kt (= KraftTypography, KraftFontSizes)
  app/src/main/java/com/kraft/calculator/domain/
    CalculatorEngine.kt (672-line Shunting-Yard parser, 30+ functions)
  app/src/main/java/com/kraft/calculator/data/
    RoomHistoryRepository.kt, LegacyHistoryRepository.kt, SettingsRepository.kt
  .github/workflows/ci.yml, PRIVACY.md, CHANGELOG.md, AUDIT.md, OPENCODE_HANDOFF.md
