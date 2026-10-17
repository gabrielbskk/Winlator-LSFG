# Repository housekeeping audit

Snapshot: main branch, 2026-10-17 (review before using this as a current inventory).

## Observations
- The repository contains Android Gradle sources, native components, Winlator runtime archives, Wine add-ons, drivers and external assets.
- Root `README.md`, `LICENSE`, `CHANGELOG.md` and existing issue templates provide a base for organization.
- Root `.gitignore` covers common build output, APKs, logs and `Lossless.dll`.
- `app/.idea/` includes tracked IDE configuration and device/deployment metadata.
- Large tracked binary dependencies include Wine Mono/Gecko installers and rootfs/Direct3D archives; these may be required by the app or installation tooling.
- The default branch did not have a `.github/workflows/` directory or a standard `app/app/src/test/` suite in the inspected tree. Experimental PR #1 has its own tests and release workflow; evaluate separately.

## Non-destructive cleanup decisions
1. **Keep binary assets in place** until every reference, expected digest, release size and dependency is audited. A deleted archive may break distribution.
2. **Do not delete `app/.idea/` in bulk** until reproducible Android Studio settings are distinguished from personal IDE state.
3. **Do not alter historic upstream credits or funding destinations** without identifying the intended recipient and license obligations.
4. Keep contributor docs and feature plans separate from unvalidated changes to LSFG-VK, GPU drivers or Wine runtimes.
5. Review generated artifacts and local secrets before committing them. Ignore new machine-specific IDE state and build intermediates.

## Suggested follow-on work
- Inventory each tracked archive with its source, license, consumer and SHA-256 hash.
- Identify large binaries suitable for release assets or Git LFS **only after** confirming reproducibility and deployment flow.
- Establish CI checks for documentation, Android build and regression testing with clearly stated environment dependencies.
- Add tests and confirm real-device results before merging PR #1 or any GPU/frame-generation optimization.
