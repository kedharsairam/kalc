# Kalc

A clean, private calculator for Android. No ads. No analytics. No trackers.

<p align="center">
  <a href="https://github.com/kedharsairam/kalc-android/releases/latest"><img src="https://img.shields.io/github/v/release/kedharsairam/kalc-android?style=for-the-badge&label=Download" alt="Download APK"></a>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
  <img src="https://img.shields.io/badge/Platform-Android-blue?style=for-the-badge" alt="Android">
</p>

---

## Features

**Basic + Scientific** — Full expression parser with operator precedence, parentheses, and live preview. Switch between basic and scientific keypads with stable layout.

**Scientific functions** — Trigonometry (sin/cos/tan + inverses + hyperbolic), logarithms (ln/log), exponentials, roots (√/∛/nth), powers, factorials, combinations (nCr/nPr), constants (π/e/τ), DMS degrees, fractions, engineering notation.

**Unit converter** — 9 categories: Length, Area, Volume, Mass, Temperature, Time, Speed, Pressure, Energy. Live conversion with swap.

**Finance tools** — Loan EMI calculator with amortization breakdown. India GST calculator (add/remove, CGST/SGST split).

**History** — Every calculation saved locally with Room database. Tap to reload, swipe to delete. Size configurable.

**Private** — No accounts. No network access. History stored on-device with backup excluded. Never uploaded.

**Customizable** — Vibration toggle, decimal precision (2-15), history size, dark theme.

---

## Tech

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Database | Room (history) |
| Preferences | DataStore |
| Parser | Hand-written Shunting-Yard |
| Tests | JUnit |

---

## Build

```bash
./gradlew assembleDebug      # debug APK
./gradlew assembleRelease    # release APK (debug-signed)
./gradlew test               # unit tests
./gradlew lintDebug          # lint
```

Requires JDK 21+, Android SDK 36.

---

## Privacy

No permissions beyond basics. No internet access. No analytics. See source for verification.

## License

[MIT](LICENSE)
