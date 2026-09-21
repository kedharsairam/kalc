# Changelog

All notable changes to Kalc are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Kalc follows [Semantic Versioning](https://semver.org/spec/v2.0.0/).

## [2.2.0] — 2026-09-21

### Added
- AES-256-GCM encrypted calculation history (AndroidKeyStore)
- Room database for history persistence
- DataStore for app settings (theme, precision, history size, vibration)
- 9-category unit converter (length, mass, temperature, speed, volume, area, time, digital storage, fuel consumption)
- Loan EMI calculator with India GST (CGST/SGST/IGST split)
- DMS input and decimal-to-DMS conversion
- Decimal-to-fraction conversion
- Engineering notation mode
- Variable assignment (`name = expression`) with session-persistent storage
- Memory functions (MC, MR, M+, M−)
- AMOLED dark theme variant
- Apple-inspired theme system (light/dark/system/amoled) with KraftTokens
- PRIVACY.md

### Changed
- Theme picker wired to settings (was hardcoded dark)
- CalculatorScreen now reads active theme from AppTheme settings
- Settings screen now shows theme selector (System/Dark/Light/Amoled)
- CI now runs `testDebugUnitTest` alongside assemble + lint
- proguard-rules.pro cleared of leftover Flutter rules
- File renames: HistoryRepository2.kt → RoomHistoryRepository.kt, HistoryRepository.kt → LegacyHistoryRepository.kt
- Duplicate LaunchedEffect import removed from CalculatorDisplay

### Fixed
- SaveHistory now uses configured history size from settings (was hardcoded 100)
- RoomHistoryRepository.migrateIfNeeded race condition fixed (migrated flag set inside synchronized block)
- Duplicate LaunchedEffect import in CalculatorDisplay.kt removed
- CI: removed flaky setup-android, JDK 17→21
- Keyboard overlap in converter: imePadding on scroll columns
- Badge text contrast: black on green/yellow/orange (WCAG)

### Removed
- Dead file: HistoryRepository2.kt (renamed to RoomHistoryRepository.kt)

## [2.1.0] — 2026-03-15

### Added
- Glance widget
- Material 3 theme integration

### Changed
- Room history database migration
- Swipe-delete replaced with visible delete button (no stuck states)
- History entries left-aligned for scannability
- History panel: single header, count badge, timestamps, monospace results
- BuildConfig generation enabled (AGP 8+)

## [2.0.0] — 2025-06-01

### Added
- Room history + DataStore settings infrastructure
- Glance widget
- Unit converter (9 categories, Fossify pattern)
- EMI + GST finance calculators
- Settings screen with vibration toggle

### Changed
- Backup rules added (history + settings excluded from cloud backup)
- Dependency updates, AGP 8.9.1

## [1.0.1] — 2025-01-15

### Fixed
- Build configuration

## [1.0.0] — 2024-06-01

### Added
- Initial release
- Basic + Scientific calculator modes
- Trigonometry (DEG/RAD/GRAD)
- Logarithms, exponentials, powers, roots
- Factorial, nCr, nPr
- Constants (π, e, τ)
- DMS input
- History with SharedPreferences storage
