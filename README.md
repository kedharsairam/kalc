# Kalc

A calculator that keeps its own business. Scientific functions, variables, nine categories of unit conversion, EMI and GST — with a history that is encrypted on the device and never leaves it.

**Zero permissions.** Not "no tracking" — the manifest declares none at all, so there is no network access, no storage access, and nothing to revoke later.

<p align="center">
  <a href="https://github.com/kedharsairam/kalc/releases/latest"><img src="https://img.shields.io/github/v/release/kedharsairam/kalc?style=for-the-badge&label=Download" alt="Download APK"></a>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
  <img src="https://img.shields.io/badge/Permissions-none-blue?style=for-the-badge" alt="No permissions">
</p>

---

## What it does

**Basic and scientific** — A hand-written Shunting-Yard parser with operator precedence, parentheses and a live preview. Basic and scientific keypads, so the layout does not jump when you switch.

**The functions you actually reach for** — Trigonometry and its inverses, hyperbolic functions, DEG/RAD/GRAD, logarithms, exponentials, roots, powers, factorials, nCr and nPr, π/e/τ, DMS degrees, fractions and engineering notation.

**Variables** — Type `rent = 1200`, then use `rent/3` later in the session. Math Notes style, and the values survive until you close the app.

**Nine categories of unit** — Length, area, volume, mass, temperature, time, speed, pressure and energy. Live conversion, with a swap.

**EMI and GST** — Loan EMI with an amortisation breakdown, and an Indian GST calculator that splits CGST/SGST.

**A home-screen widget** — A Glance widget, so the answer is one tap away without opening the app.

## Privacy

No accounts. No network access. No analytics. No trackers.

History is stored on-device with backup excluded, and it is **encrypted at rest**: every field is sealed with AES-256-GCM under a Keystore-backed master key — hardware-backed where the device has it — with a fresh 12-byte IV per field. Tapping a history row to reuse it decrypts on the spot; the plaintext is never written to disk.

Full detail in [PRIVACY.md](PRIVACY.md).

---

<details>
<summary><strong>Tech</strong></summary>

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Widget | Glance |
| Database | Room, with field-level encryption |
| Preferences | DataStore |
| Parser | Hand-written Shunting-Yard |
| Tests | JUnit — 67 engine tests |

</details>

<details>
<summary><strong>Build from source</strong></summary>

```bash
./gradlew assembleDebug      # debug APK
./gradlew assembleRelease    # release APK, debug-signed
./gradlew test               # 67 unit tests
./gradlew lintDebug          # lint
```

Requires JDK 17, Android SDK 36 (`minSdk 26`).

</details>

## Support

If you enjoy Kalc, buy me a coffee:

<p align="center">
  <a href="https://buymeacoffee.com/kedhartech"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="182"></a>
</p>

## License

[MIT](LICENSE)
