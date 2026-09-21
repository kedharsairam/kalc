# OpenCode Knowledge Base — Kalc v2.2.0
# Created: 2026-09-21 (Hermes → OpenCode migration)
# Purpose: ALL session knowledge, standards, audit results, and context
#          packaged for OpenCode to read and become "Jarvis" — the coding
#          assistant for Kedhar Sairam's Kraft portfolio.
#
# THIS IS THE SOURCE OF TRUTH for OpenCode when working on Kalc.
# Read these files in order.

# ═══════════════════════════════════════════════════════════════════════
# DIRECTORY STRUCTURE
# ═══════════════════════════════════════════════════════════════════════

# opencode-knowledge/
# ├── INDEX.md                  ← You are here — read this first
# ├── TRIGGER_PROMPT.md         ← System prompt that makes OpenCode become Jarvis
# ├── KRAFT_STANDARDS.md        ← 6 documents: MANIFESTO, PRINCIPLES, DESIGN,
# │                               ARCHITECTURE, STANDARDS, CHECKLIST
# ├── HERMES_SESSION_SUMMARY.md ← Everything Hermes learned during the Kalc session
# │                               (user profile, Kalc details, audit results, code changes,
# │                               remaining work, user instructions)
# └── LOAD_SCRIPT.sh            ← Optional: bash script that loads all knowledge
#
# Also in the Kalc project root:
# ├── AUDIT.md                  ← 784-line full audit & score (83/100)
# ├── PRIVACY.md                ← Kalc privacy policy (72 lines)
# ├── CHANGELOG.md              ← Keep a Changelog, v2.2.0 entry
# └── OPENCODE_HANDOFF.md       ← Detailed transfer document (alternative to this index)

# ═══════════════════════════════════════════════════════════════════════
# READING ORDER (do this in sequence)
# ═══════════════════════════════════════════════════════════════════════

# 1. TRIGGER_PROMPT.md
#    Becomes your system prompt. Read it FIRST. It tells you who you are
#    (Jarvis), who the user is (Kedhar Sairam), what the Kalc project is,
#    what state it's in, and what remains to be done.
#
# 2. KRAFT_STANDARDS.md
#    The 6 documents that define how every Kraft app is built. You MUST know
#    these to work on any app in the portfolio. They define Apple design
#    language, architecture rules, coding conventions, and the 38-item checklist.
#
# 3. HERMES_SESSION_SUMMARY.md
#    Everything Hermes learned. User profile, Kalc full details (every file,
#    every layer), complete audit results, all code changes made this session,
#    build/deployment status, remaining work, and user's key instructions.
#
# 4. AUDIT.md (in project root)
#    The full 784-line audit. Detailed scoring, all P0/P1/P2/P3 issues,
#    what was fixed, what remains. Reference for specific findings.
#
# 5. PRIVACY.md (in project root)
#    Kalc's privacy policy. Required reading — Kalc has no network, no analytics,
#    encrypted local storage only.
#
# 6. CHANGELOG.md (in project root)
#    Version history. v2.2.0 is the current version with full feature list.

# ═══════════════════════════════════════════════════════════════════════
# QUICK START (if you just want to start working)
# ═══════════════════════════════════════════════════════════════════════

# You are Jarvis. Your user is Kedhar Sairam. You are working on Kalc v2.2.0
# at C:\Users\kedhar\projects\kalc (or ~/projects/kalc).
#
# Current state:
#   - All P0/P1 issues from the audit are FIXED
#   - Token plumbing complete (CalculatorButton, CalculatorDisplay, Theme.kt,
#     BasicKeypad, ScientificKeypad all use Kraft tokens now)
#   - Build is green: ./gradlew assembleDebug
#   - APK deployed to phone
#   - Remaining: ConverterScreen, HistorySheet, SettingsScreen still have inline
#     values; visual redesign (motion, component changes) not started
#
# To continue the Apple design pass (Phase 2):
#   1. Read KRAFT_STANDARDS.md (DESIGN.md section especially)
#   2. Read HERMES_SESSION_SUMMARY.md (REMAINING WORK section)
#   3. Read the 3 remaining files with inline values:
#      - app/src/main/java/com/kraft/calculator/presentation/ConverterScreen.kt
#      - app/src/main/java/com/kraft/calculator/presentation/HistorySheet.kt
#      - app/src/main/java/com/kraft/calculator/presentation/SettingsScreen.kt
#   4. Apply Kraft tokens (KraftSpacing, KraftRadius, KraftTypeScale) to replace
#      all inline dp/sp values
#   5. Add motion system (animateContentSize, AnimatedVisibility, etc.) per DESIGN.md
#   6. Add ReduceMotion support
#   7. Build and deploy: ./gradlew assembleDebug && adb install -r ...

# ═══════════════════════════════════════════════════════════════════════
# GITHUB REPOS (source of truth for all pushed data)
# ═══════════════════════════════════════════════════════════════════════

# Kalc:      ssh://git@github.com/kedharsairam/kalc.git (master branch)
# Hermes-config: ssh://git@github.com/kedharsairam/hermes-config.git (main branch)
#   - Contains: session data (306 sessions), Kraft Standards references, skills,
#               memory, BACKUP_MANIFEST.md

# ═══════════════════════════════════════════════════════════════════════
# END OF INDEX
# ═══════════════════════════════════════════════════════════════════════
