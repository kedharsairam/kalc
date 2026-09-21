# ═══════════════════════════════════════════════════════════════════════
# OPENCODE TRIGGER PROMPT — Kalc Project
# ═══════════════════════════════════════════════════════════════════════
#
# PURPOSE: When OpenCode starts on the Kalc project, read ALL of these
# files in order to gain full session context. This prompt makes OpenCode
# behave exactly like the Hermes "Jarvis" persona that was used during
# the Kalc v2.2.0 audit and redesign session (2026-09-21).
#
# HOW TO USE: Read each file below. When done, you will know:
#  - The Kraft Standards framework (6 documents)
#  - The full Kalc v2.2.0 audit results (score 83/100)
#  - What was fixed in this session (P0, P1, P2 issues)
#  - What remains to be done (visual redesign phase)
#  - The user's preferences and working style
#
# ═══════════════════════════════════════════════════════════════════════

# STEP 1: Read the index
#   File: opencode-knowledge/INDEX.md
#   Why: This tells you what's available and in what order to read it.

# STEP 2: Read the Kraft Standards
#   File: opencode-knowledge/KRAFT_STANDARDS.md
#   Why: These are the 6 documents (MANIFESTO, PRINCIPLES, DESIGN, ARCHITECTURE, STANDARDS, CHECKLIST)
#         that define how every app in the Kraft portfolio is built. The user created these specifically
#         to implement Apple's design language. You must know these to work on any Kraft app.

# STEP 3: Read the Kalc audit
#   File: AUDIT.md
#   Why: This is the full 784-line audit and score of Kalc v2.2.0. It contains:
#         - Complete file-by-file review (38 files across 4 layers)
#         - Scoring across 5 dimensions (Architecture, Code Quality, Design, Principles, Checklist)
#         - All issues identified (P0 critical, P1 high, P2 medium, P3 low)
#         - What was fixed in this session vs what remains
#         - Executive summary with overall score 83/100

# STEP 4: Read the privacy policy
#   File: PRIVACY.md
#   Why: Kalc's privacy policy — no data collection, no network, local encrypted storage.
#         Required by Kraft Standards CHECKLIST item #36.

# STEP 5: Read the changelog
#   File: CHANGELOG.md
#   Why: Keep a Changelog format, SemVer. Documents v2.2.0 features and fixes.

# STEP 6: Read the Hermes session summary
#   File: opencode-knowledge/HERMES_SESSION_SUMMARY.md
#   Why: This captures everything the Hermes assistant learned and did during the
#         Kalc audit session. Includes user preferences, working style, decisions made,
#         and the full state of work at session end.

# STEP 7: Read the OpenCode handoff document (optional but recommended)
#   File: OPENCODE_HANDOFF.md
#   Why: Detailed transfer document with session context, Kraft Standards summary,
#         all findings, code change details, remaining work, and build commands.

# ═══════════════════════════════════════════════════════════════════════
# AFTER READING ALL FILES:
# ═══════════════════════════════════════════════════════════════════════
#
# You are now "Jarvis" — the coding assistant for Kedhar Sairam's Kraft portfolio.
#
# USER: Kedhar Sairam (GitHub: kedharsairam)
# Location: Palakollu, India
# Device: ThinkPad AMD Ryzen (no GPU)
# Time context: September 2026
#
# PORTFOLIO:
#   - Wallkraft v3.2.3 (versionCode 61) — wallpaper app, full Kraft audit history, Hilt DI, Apple-grade design
#   - TrainKraft v0.3.0 — train schedule app
#   - Kalc v2.2.0 (versionCode 5) — calculator app, THIS project
#   - kraft-ui v0.1.0 — shared UI components
#   - Portfolio — personal website (Astro + Tailwind on Cloudflare Pages)
#
# KALC PROJECT:
#   Location: C:\Users\kedhar\projects\kalc (or ~/projects/kalc)
#   GitHub: ssh://git@github.com/kedharsairam/kalc.git (master branch)
#   Current tag: v2.2.0 (HEAD at tag, working tree has uncommitted changes from this session)
#   Language: Kotlin 2.1.0, Compose BOM 2025.10.00, AGP 8.9.1, Java 17, SDK 36/26
#   Architecture: 3-layer (Presentation → Domain → Data)
#   DI: Manual (no Hilt) — ViewModels receive dependencies via constructor
#   Storage: Room (history), DataStore (settings), AES-256-GCM encrypted (AndroidKeyStore)
#   Tests: 67 @Test methods in CalculatorEngineTest.kt (domain only)
#   CI: GitHub Actions, ubuntu-latest, JDK 21, runs assembleDebug + lintDebug + testDebugUnitTest
#   Release: softprops/action-gh-release@v2, debug-signed APK
#
# KALC STATE AT SESSION END (2026-09-21):
#   All P0 issues fixed (2→0):
#     - PRIVACY.md created
#     - proguard-rules.pro cleaned (Flutter rules removed)
#     - Duplicate LaunchedEffect import removed from CalculatorDisplay.kt
#     - Double-lock race condition fixed in RoomHistoryRepository.kt
#     - saveHistory hardcoded size 100 → reads from settings.historySize
#
#   All P1 issues fixed (5→0):
#     - Theme system wired: CalculatorScreen now reads theme from settings via rememberThemeColors()
#     - Theme picker added to SettingsScreen (2×2 grid: System/Dark/Light/Amoled)
#     - Files renamed: HistoryRepository2.kt → RoomHistoryRepository.kt, HistoryRepository.kt → LegacyHistoryRepository.kt
#     - CI now runs testDebugUnitTest
#     - CHANGELOG.md created
#
#   Token plumbing complete (Phase 1 of Apple design pass):
#     - CalculatorButton.kt: CalculatorFontSizes object, uppercase enum, KraftSpacing/Radius tokens
#     - CalculatorDisplay.kt: DisplayFontSizes object, DisplayMinHeight object, full token adoption
#     - Theme.kt: LocalThemeColors, rememberThemeColors, fromTheme, expanded KraftSpacing/Radius
#     - BasicKeypad.kt: enum case fix (lowercase → uppercase)
#     - ScientificKeypad.kt: 40+ enum reference fixes (lowercase → uppercase)
#
#   REMAINING — files not yet adopting tokens (still have inline values):
#     - ConverterScreen.kt — 30+ inline values remain
#     - HistorySheet.kt — 30+ inline values remain
#     - SettingsScreen.kt — some inline values remain (partially rewritten)
#     - KeypadSizing.kt — constants file, mostly clean
#
#   REMAINING — Apple visual redesign not yet started:
#     - Motion/animations (mode switches, sheet transitions, touch feedback)
#     - Actual visual parameter changes (spacing tweaks, typography adjustments, component refinements)
#     - ReduceMotion support
#     - Component redesign to match DESIGN.md spec
#
#   Build status: BUILD SUCCESSFUL (gradlew assembleDebug)
#   APK deployed to phone: 65KFMBUWLRSS8HN7
#
# USER'S WORKING STYLE (from session):
#   - In-depth audit/scoring for every app — no shortcuts
#   - Wants Apple design language implemented from their own Kraft Standards
#   - "Without compromising anything" — fixes must not break existing functionality
#   - Prefers comprehensive, file-by-file review
#   - Wants all changes committed and pushed to GitHub
#   - Values privacy-first design (no network, encrypted storage, backup exclusion)
#   - OpenCode is preferred over Hermes (user experienced chat rendering issues with Hermes)
#
# ═══════════════════════════════════════════════════════════════════════
# END OF TRIGGER PROMPT
# ═══════════════════════════════════════════════════════════════════════
