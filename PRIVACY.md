# Kalc Privacy Policy

**Last updated:** September 2026

## Overview

Kalc is a private calculator for Android. This document explains what data Kalc handles, how it is stored, and where it goes.

## Data We Collect

**We do not collect any data.**

Kalc does not collect, transmit, or share any user data. There are no analytics, no telemetry, no crash reports, no tracking pixels, and no advertising SDKs.

## Data We Store

Kalc stores the following data locally on your device:

### Calculation History
- Every calculation you perform is saved locally in a Room database (`kalc_history.db`).
- History entries are encrypted at rest using AES-256-GCM. The encryption key is stored in the Android KeyStore (`kalc_history_key`), which uses hardware-backed storage when available.
- Each entry includes the expression, the result, and a timestamp.
- History is limited to a configurable maximum size (default 50 entries, configurable up to 500).
- You can clear history at any time from the History panel.

### Settings
- Your preferences (vibration toggle, theme selection, decimal precision, history size) are stored locally using Android DataStore (`kalc_settings`).
- DataStore provides encrypted storage for preferences.

### Legacy Data
- If you used an earlier version of Kalc that stored history in SharedPreferences, your history is migrated to the encrypted Room database on first launch of v2.0.0+. The legacy SharedPreferences are cleared after migration.

## Data Sharing

**Kalc does not share any data with any third party.**

- No internet permission is declared in the manifest. Kalc cannot make network requests.
- No analytics or crash reporting SDKs are included.
- No advertising SDKs are included.
- No unique device identifiers are collected or stored.

## Backup and Transfer

- Calculation history and settings are excluded from Android backup and device transfer. This prevents your calculation data from being copied to cloud backups or transferred to another device without your explicit action.
- Exclusion is configured in `backup_rules.xml` and `data_extraction_rules.xml`.

## Permissions

Kalc requests no permissions beyond what is required to function as a calculator. Specifically:

- **No internet permission** — Kalc cannot access the network.
- **No storage permission** — History and settings are stored in app-private storage.
- **No camera, location, contacts, or microphone permissions.**

## Security

- History encryption uses AES-256-GCM with a random 12-byte initialization vector per entry.
- The encryption key is generated and stored in the Android KeyStore, which provides hardware-backed security on supported devices.
- If encryption fails, entries are stored with a plaintext fallback marker (`plain:` prefix) to preserve data availability. This is a degradation, not a compromise — the data remains on-device and is migrated to encrypted storage on the next successful encryption.

## Updates

Kalc updates are distributed via GitHub Releases. The update mechanism is manual (you download and install the APK). No automatic update check is performed — no network access means no background update checks.

## Contact

For questions about this policy, see the source code at [github.com/kedharsairam/kalc](https://github.com/kedharsairam/kalc).

---

*This policy is accurate as of Kalc v2.2.0. It may be updated in future versions; the updated version will be dated accordingly.*
