# Rankin Realms — Product Blueprint v1.0

## Positioning

Rankin Realms is not a commodity wallpaper feed. It is a curated, high-resolution cinematic environment collection where each released image remains a complete artwork. The application is the viewing/framing tool.

The product hierarchy is:

**Artist master -> preview proxy -> full master download -> user-selected device frame -> wallpaper output**

No generative reconstruction, no automatic redesign, no AI outpainting, no synthetic detail pass inside the app.

## Why full masters are the correct product decision

A fixed portrait crop permanently discards much of a landscape master and assumes every user wants the same focal point. Full-master delivery solves that problem. On a tall phone one user may center architecture, another may prioritize mountains, water, or foreground. The application turns that choice into a deliberate feature.

## Primary user flow

1. Open gallery.
2. Choose an artwork.
3. Inspect the full composition and artwork information.
4. Tap **Frame for my phone**.
5. Select Crop / Whole Master / Scroll.
6. In Crop mode, pinch and drag the master inside an exact device-shaped viewport.
7. Select Home / Lock / Both.
8. App downloads the original master if it is not already cached.
9. App decodes only the required source region at near-target resolution.
10. App renders the final wallpaper and calls Android WallpaperManager.

## Quality architecture

### Preview

Use a 1,600–2,400 px sRGB JPEG or WebP proxy for browsing and framing interaction. The preview is never used to render the final wallpaper.

### Master

Recommended production delivery:

- JPEG quality 95–100 or lossless/high-quality alternative if storage allows
- embedded Display P3 ICC profile
- original master pixel dimensions retained
- no additional sharpening beyond the approved master
- no metadata dependency for color management; ICC profile must be embedded in the file

### Final wallpaper render

For Crop mode, the app derives a normalized crop rectangle from the preview interaction, maps it back to full-master coordinates, and uses `BitmapRegionDecoder` to decode only that source region. This avoids loading a 150 MP image into RAM.

The decoded region is downsampled near the phone's physical wallpaper dimensions and then filtered once to exact output dimensions. No repeated resize chain is used.

### Wide color

Android wide-color-gamut support begins at API 26. The app requests wide-color mode for its main activity and can render output bitmaps in Display P3 on compatible devices. On devices that do not report wide-color-gamut support, Auto mode falls back to sRGB.

Because OEM wallpaper pipelines can apply their own transformations, the app should be described as **Display-P3-aware**, not as guaranteeing identical P3 preservation on every launcher/device.

## Content slate

Current V31 master slate to migrate into the production catalog:

- Mountain Basin City
- The Veiled Valley
- Autumn Canal-City
- Forest Ravine
- Alpine Sanctuary
- Sea-Cliff
- The Last Terrace
- Urban Megastructure

Additional mythic/environment studies can be released as a separate or expanded collection after final commercial/IP review.

## Collections

- **REALMS** — monumental fantasy environments
- **SANCTUARIES** — alpine / forest / water
- **CITIES** — civilization-scale environments
- **DUSK** — sunset / darker atmospheric work
- **MEGASTRUCTURES** — modern / speculative urban work

## MVP release strategy

Launch with enough content and framing functionality that the app is clearly more than a single-wallpaper utility. A practical first public release is 8–12 finished masters, with the full-master framing experience as the core differentiator.

## Privacy position

MVP intentionally avoids:

- user accounts
- contacts/location
- advertising ID
- broad media-library access
- analytics SDKs
- background tracking

`INTERNET` downloads the catalog/master files. `SET_WALLPAPER` applies the chosen wallpaper. Saving a full master uses Android's user-initiated document picker rather than broad storage permission.

## Monetization path

Do not block v1 on cross-store billing. First prove product quality and store acceptance. The architecture can later add store-specific billing implementations behind the existing `play` and `galaxy` product flavors.

Possible later model:

- several free masters
- paid collection unlocks
- one-time ownership, not subscription-first
- no ad-supported degradation of the visual experience
