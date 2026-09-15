# Rankin Realms — Android Wallpaper App

Native Android wallpaper application for Leland Rankin's full-resolution environment masters.

## Core principle

The app never distributes a pre-cropped wallpaper as the canonical artwork. Each artwork has a lightweight preview plus a full master. The user opens the full master, chooses the exact visible frame for their own device with pan/zoom, then applies it to Home, Lock, or Both.

This preserves the finished master and gives the buyer control over composition instead of forcing one 9:20 crop onto every phone.

## Product name

**Rankin Realms**  
Publisher/artist identity: **Rankin Environments**

## MVP features

- Curated master gallery
- Full-master detail view
- Device-shaped interactive framing preview
- Pinch-to-zoom and drag-to-frame
- Home / Lock / Both targets
- Three framing modes:
  - **Crop** — user chooses the exact visible region
  - **Whole Master** — entire artwork shown with neutral black matte where the phone aspect ratio differs
  - **Scroll** — full-width landscape treatment for launchers that support wallpaper panning
- Full-resolution master downloaded only when needed
- Memory-safe region decoding for very large 100–225 MP masters
- Display P3-aware rendering on compatible Android devices
- sRGB compatibility mode
- Favorite artworks
- Optional export of the untouched full master through Android's document picker
- Private cache of downloaded masters
- No account, ads, advertising ID, broad photo permission, or media-library permission in the MVP

## Store flavors

- `playDebug` / `playRelease` -> `com.rankinenvironments.realms.play`
- `galaxyDebug` / `galaxyRelease` -> `com.rankinenvironments.realms.galaxy`

Separate package IDs are intentional to avoid cross-store update collisions.

## Build target

- compileSdk 37
- targetSdk 36
- minSdk 26
- AGP 9.4.0
- Gradle 9.6.0
- Kotlin 2.3.21
- Compose BOM 2026.08.00

## Verified master registry

The development catalog now contains all ten verified V31/current master images: Mountain Basin City, The Veiled Valley, Autumn Canal-City, Alpine Sanctuary, Forest Ravine, Urban Megastructure, Sea-Cliff, Highland Citadel, The Last Terrace, and Mythic Environment Realism.

Exact Drive file IDs, filenames, dimensions, byte counts, ICC profiles, SHA-256 hashes, and verification notes are kept in `docs/VERIFIED_MASTER_REGISTRY.json` and summarized in `docs/SOURCE_MAP.md`. Drive share URLs are source records only; production builds should still use stable versioned HTTPS object/CDN URLs.

## Content delivery strategy

Do **not** put every 40–100 MB master into the base app. Ship small sRGB previews in the app (or from the CDN) and host full Display P3 masters behind stable HTTPS URLs. The app downloads and privately caches a master only when the user opens/applies/saves it.

This architecture works for both Google Play and Galaxy Store and avoids making the initial install unnecessarily large.

## Before production release

1. Export the final master set from Lightroom Mobile with embedded **Display P3** ICC profiles at maximum quality.
2. Keep a separate archival original; never overwrite it for app delivery.
3. Upload production P3 masters to a stable object store/CDN (Cloudflare R2, S3, Firebase Storage, etc.). Do not use expiring Google Drive signed URLs as permanent production URLs.
4. Populate `masterUrl` and, if desired, remote `previewUrl` values in `catalog.json` or host the catalog JSON remotely.
5. Set `BuildConfig.CATALOG_URL` to the production catalog endpoint.
6. Build and test both store flavors on at least one Samsung Galaxy phone and one non-Samsung Android device.
7. Add final app icon, screenshots, privacy-policy URL, support email, signing keys, and store metadata.

See `docs/PRODUCT_BLUEPRINT.md` and `docs/CONTENT_PIPELINE.md`.


## Build correction
This V4 build uses compileSdk 36 / targetSdk 36 to match the GitHub Actions SDK install step.
