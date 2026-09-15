# Production Content Pipeline

## 1. Export from the approved master

For each artwork, create a delivery copy from the final approved master. Do not re-edit the master for the app.

Recommended Lightroom Mobile export for the production master copy:

- Color space: **Display P3**
- Maximum available pixel dimensions / no intentional downscale
- Maximum JPEG quality
- Embedded color profile
- Output sharpening: Off unless the approved master workflow explicitly requires it
- No watermark
- Preserve the original aspect ratio

Also create an sRGB preview proxy at about 1,600–2,400 px on the long side.

## 2. File naming

Use stable machine names:

`mountain_basin_city_master_p3_v1.jpg`  
`mountain_basin_city_preview_srgb_v1.jpg`

Never replace a published binary silently. Increment the asset version and catalog revision.

## 3. Hosting

Use permanent HTTPS object URLs. Recommended structure:

```
/catalog/catalog-v1.json
/previews/mountain_basin_city_v1.jpg
/masters/mountain_basin_city_p3_v1.jpg
```

Set long cache headers on immutable versioned image objects. Keep the catalog short-lived so releases can update quickly.

## 4. Catalog record

Each artwork record contains:

- stable ID
- public title
- collection
- description
- preview URL
- master URL
- source pixel width/height
- byte size
- declared source color space
- featured flag

## 5. QA before publish

For every artwork:

- Preview and full master match composition exactly.
- Full master ICC profile reports Display P3.
- Pixel dimensions match catalog metadata.
- No accidental crop, watermark, or border.
- Test Crop mode at 1x and zoomed framing.
- Test Home, Lock, Both.
- Test full-master export.
- Test after app restart (cached master).
- Test Auto color mode on wide-gamut and non-wide-gamut devices.
- Verify no visible banding, oversharpening, or color clipping introduced by the app.
