# Kraft Standards — Full Framework
# Reconstructed from Kedhar's session data (Hermes to OpenCode migration 2026-09-21)
# These are the 6 documents that define how every app in the Kraft portfolio is built.
# Source: hermes-config repo (kedharsairam/hermes-config) + session JSON data

# ═══════════════════════════════════════════════════════════════════════
# 1. MANIFESTO.md
# ═══════════════════════════════════════════════════════════════════════

# Software, built right.
#
# Software is craft. Every line matters. Every decision has a reason.
#
# We build software that:
# - Respects the user's privacy by default — no data leaves the device unless the user explicitly chooses to send it
# - Feels like it was designed by Apple — not just looks, but the entire experience: motion, typography, interaction, edge cases
# - Is reliable — it does what it says, every time, on every device it supports
# - Has minimal footprint — fast install, fast start, low memory, low battery, no telemetry
# - Is tested, not hoped — automated tests cover the logic that matters; manual QA covers the rest
#
# We do not:
# - Collect data we don't need
# - Ship features that aren't tested
# - Prioritize speed of delivery over quality of product
# - Treat the user's device as a data source
#
# This is not a feature checklist. It's a philosophy. Every engineering decision is measured against it.

# ═══════════════════════════════════════════════════════════════════════
# 2. PRINCIPLES.md (priority order — first is most important)
# ═══════════════════════════════════════════════════════════════════════

# 1. Zero Trust
#    Never trust input, never trust network, never trust the system.
#    Validate early. Sanitize everywhere. Fail closed.
#
# 2. Lightweight
#    The app should be small, fast, and use minimal resources.
#    No heavy frameworks unless they earn their place.
#    Every dependency is a liability.
#
# 3. Robust
#    The app handles edge cases gracefully.
#    No crashes on bad input. No deadlocks on slow I/O.
#    Degrades gracefully when things go wrong.
#
# 4. Reliable
#    The app does what it says it does, every time.
#    Consistent behavior across devices and OS versions.
#    Predictable state — no random resets, no silent data loss.
#
# 5. Secure
#    Data at rest is encrypted. Data in transit is authenticated.
#    No leaked secrets in source. No insecure storage.
#    Permissions are minimal and justified.
#
# 6. Accessible
#    Works for users with disabilities.
#    Touch targets are 44dp minimum. Text is readable.
#    Screen reader labels are meaningful. Contrast meets WCAG.
#
# 7. Offline-First
#    The app works without a network connection.
#    Network is an enhancement, not a requirement.
#    Local data is the source of truth.
#
# 8. Predictable
#    The app behaves the same way every time.
#    No hidden state. No surprising side effects.
#    User actions have visible, immediate consequences.
#
# 9. Battery-Conscious
#    No unnecessary wake locks. No aggressive polling.
#    Background work is scheduled, not continuous.
#    The app doesn't drain the battery when idle.
#
# 10. Testable
#     Every layer of the app can be tested in isolation.
#     Domain logic has unit tests. UI has screenshot/integration tests where practical.
#     Tests run in CI on every commit.

# ═══════════════════════════════════════════════════════════════════════
# 3. DESIGN.md (Apple HIG-inspired — Apple design language in Kraft context)
# ═══════════════════════════════════════════════════════════════════════

## Colors
- Colors are NEVER hardcoded — they come from the theme system
- One accent color per view — not five competing colors
- Destructive actions use red (#FF453A / systemRed)
- Interactive elements use the accent; non-interactive uses neutral
# Background hierarchy: page < surface < surface-variant < opaque separator
# Text hierarchy: primary > secondary > tertiary > quaternary (opacity steps)
# No pure black (#000000) except on OLED devices where it's intentional

## Typography (SF Pro / Roboto type scale)
# largeTitle: 34sp, bold
# title1: 28sp, bold
# title2: 22sp, bold
# title3: 20sp, semibold
# headline: 17sp, semibold
# body: 17sp, regular
# callout: 16sp, regular
# subheadline: 15sp, regular
# footnote: 13sp, regular
# caption1: 12sp, regular
# caption2: 11sp, regular
# Bold = interactive/emphasized; regular = passive/supporting
# No custom fonts unless they serve a clear purpose

## Spacing
# 8px rhythm: 8, 16, 24, 32, 40, 48, 56, 64 dp
# Screen edges: 16dp padding minimum
# Card padding: 16dp
# Icon-label spacing: 8dp
# Section spacing: 24dp or 32dp
# No arbitrary spacing values — every spacing comes from KraftSpacing

## Motion
# Mode switches: 350ms easeInOutCubic push
# Sheets: 400ms easeInOutCubic
# Entry: 250ms; Exit: 200ms
# Touch feedback: 100ms
# Spring: 220ms damping 25 for dynamic/playful interactions
# Respect ReduceMotion preference — no animation when requested
# Motion serves clarity, not decoration

## Components
# App bars: standard height, title centered or leading, no clutter
# Sheets: drag handle, sentence case title, primary action pinned to bottom, swipe to dismiss
# Cards: radius 12dp (standard), 20dp (large/sheet), 8dp (tight grid); no elevation shadow — use surface color differentiation
# Buttons: 44dp minimum touch target, clear label, consistent placement
# Toggles: standard switch component, label on right, no custom toggle designs
# Lists: 44dp row height minimum, consistent leading/trailing placement
# Text fields: standard Material 3 text field, no custom decorations
# Search bars: standard height (44dp), consistent placement
# Skeleton loaders: shimmer animation, same shape as content, no placeholder text

## Accessibility
# Minimum touch target: 44dp x 44dp
# VoiceOver/TalkBack labels on all interactive elements
# Color contrast: WCAG AA minimum (4.5:1 for normal text, 3:1 for large text)
# Don't rely on color alone to convey information
# Support system font scaling

# ═══════════════════════════════════════════════════════════════════════
# 4. ARCHITECTURE.md (3-layer strict)
# ═══════════════════════════════════════════════════════════════════════

# PRESENTATION → DOMAIN → DATA
#
# PRESENTATION (UI layer):
# - Compose screens, ViewModels, widgets
# - Imports from DOMAIN and DATA, never the other way
# - State is StateFlow / Compose state; events flow up via callbacks
# - No business logic in presentation
#
# DOMAIN (business logic layer):
# - Pure Kotlin, no Android imports (no Context, no Activity, no View)
# - Entities, use cases, calculators, parsers, converters
# - Single source of truth for business rules
# - Testable without Android framework
#
# DATA (data layer):
# - Repositories, DAOs, DataStore, crypto, network (if any)
# - Implements domain interfaces
# - No UI imports
# - Room, SharedPreferences, DataStore, encrypted storage
#
# Rules:
# - Domain zero UI imports — if domain imports android.*, it's wrong
# - State is immutable — use data classes, never mutable shared state
# - State down, events up — ViewModel pushes state; UI sends events via callbacks
# - No stored BuildContext — pass Context only where needed, never store it
# - No global mutable state — use dependency injection or explicit state holders

# ═══════════════════════════════════════════════════════════════════════
# 5. STANDARDS.md (coding conventions)
# ═══════════════════════════════════════════════════════════════════════

# Files:
# - kebab-case: calculator-engine.kt, history-crypto.kt
# - Test files: calculator-engine-test.kt
#
# Classes/objects/interfaces:
# - PascalCase: CalculatorEngine, HistoryRepository, AppTheme
# - Enum values: UPPERCASE (NUMBER, OPERATOR, EQUALS, SCIENTIFIC, SHIFT_SCI)
#
# Functions/variables:
# - camelCase: calculateResult, historySize, isSystemInDark
# - Boolean prefix: is/has/can/should (isSystemInDark, hasHistory, canCalculate)
#
# Indentation: 2 spaces
# Line length: 120 chars code, 80 chars comments
# Trailing commas: yes (Kotlin style)
# Braces: K&R style (opening brace on same line)
#
# Comments:
# - Explain WHY, not WHAT — the code should explain what
# - Doc comments (/** */) on public APIs only
# - No commented-out code — delete it or put it behind a feature flag
# - No TODO without a tracking issue reference
#
# Error handling:
# - Prefer Result<T> over exceptions for EXPECTED failures
# - Never swallow errors silently — log or propagate
# - Validate early — fail fast on bad input
#
# State:
# - Immutable by default — data classes, val properties
# - No global mutable state
# - No stored BuildContext
#
# Composable functions:
# - const constructors where possible
# - Avoid closures in build scopes (use remember, derivedStateOf)
# - Use .builder() for list construction
# - Provide width/height for images (don't let them be unbounded)

# Commits (conventional commits):
# - imperative mood: "add", "fix", "remove", "update" — not "added", "fixed"
# - 72 chars max subject line
# - Reference issues: "fix: correct history migration race condition (closes #42)"
# - Types: feat, fix, docs, style, refactor, test, chore

# ═══════════════════════════════════════════════════════════════════════
# 6. CHECKLIST.md (38 items, 8 sections)
# ═══════════════════════════════════════════════════════════════════════

## Code Quality (7)
# [ ] 1. No hardcoded colors — all colors from theme
# [ ] 2. No hardcoded spacing — all spacing from KraftSpacing
# [ ] 3. No magic numbers — constants extracted to KraftConstants or local object
# [ ] 4. All boolean properties use is/has/can/should prefix
# [ ] 5. All files follow kebab-case naming
# [ ] 6. All classes follow PascalCase naming
# [ ] 7. All functions/variables follow camelCase naming

## Testing (7)
# [ ] 8. Domain logic has unit tests (CalculatorEngineTest level)
# [ ] 9. Tests cover edge cases (divide by zero, invalid input, overflow)
# [ ] 10. Tests run in CI on every commit (testDebugUnitTest in ci.yml)
# [ ] 11. Test count is meaningful — not just for show
# [ ] 12. Tests use delta-based comparison for floating point
# [ ] 13. No commented-out tests
# [ ] 14. Test names describe what they test

## Security (7)
# [ ] 15. No internet permission unless app needs network
# [ ] 16. No analytics SDKs
# [ ] 17. No crash reporting that sends data off-device
# [ ] 18. Sensitive data encrypted at rest (AES-256-GCM minimum)
# [ ] 19. Encryption keys in AndroidKeyStore, not in source
# [ ] 20. No hardcoded secrets (API keys, tokens, passwords)
# [ ] 21. Backup rules exclude sensitive data

## Performance (6)
# [ ] 22. No unnecessary recomposition — state is precise
# [ ] 23. No heavy work on main thread
# [ ] 24. Images have explicit size constraints
# [ ] 25. No memory leaks — no retained Context/View references
# [ ] 26. Lazy loading for large lists
# [ ] 27. Battery-conscious — no aggressive polling or wake locks

## Design (8)
# [ ] 28. One accent color per view
# [ ] 29. Typography follows KraftTypeScale (no inline sp values)
# [ ] 30. Spacing follows 8px rhythm (no arbitrary dp values)
# [ ] 31. Motion respects ReduceMotion
# [ ] 32. Touch targets minimum 44dp
# [ ] 33. Corner radii from KraftRadius (no inline RoundedCornerShape(8.dp))
# [ ] 34. Components match DESIGN.md spec (sheets, cards, buttons, lists)
# [ ] 35. Dark mode is functional (not just hardcoded dark)

## Build & Release (8)
# [ ] 36. PRIVACY.md exists and is accurate
# [ ] 37. CHANGELOG.md exists and follows Keep a Changelog format
# [ ] 38. CI runs tests (testDebugUnitTest) + lint on every push
# [ ] 39. proguard-rules.pro is app-specific, not copy-pasted from another project
# [ ] 40. Release builds are signed correctly
# [ ] 41. Version code increments with each release
# [ ] 42. Tag format: vMAJOR.MINOR.PATCH (SemVer)
# [ ] 43. No build warnings in release build

## Documentation (3)
# [ ] 44. README.md describes features, tech stack, and build instructions
# [ ] 45. Public APIs have doc comments
# [ ] 46. Architecture decisions are documented (ADRs or inline comments)

## Post-Release (3)
# [ ] 47. Release notes in CHANGELOG before tagging
# [ ] 48. Tagged release builds are verifiable (download + verify)
# [ ] 49. Known issues are documented (not hidden)

# ═══════════════════════════════════════════════════════════════════════
# END OF KRAFT STANDARDS
# ═══════════════════════════════════════════════════════════════════════
