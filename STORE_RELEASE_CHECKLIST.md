# Store Release Checklist

## Google Play

- Android App Bundle (`playRelease`)
- target API 36+
- app content/rating questionnaire complete
- Data safety form consistent with actual SDKs and permissions
- privacy policy hosted publicly
- meaningful wallpaper functionality demonstrated in screenshots
- internal/closed testing completed before production
- app screenshots show gallery, full master, framing screen, and Home/Lock/Both selection

## Galaxy Store

- `galaxyRelease` package ID
- 64-bit-compatible package (the app itself contains no custom native library)
- 4–8 listing screenshots
- privacy/support information completed
- verify Samsung-specific wallpaper behavior on One UI

## Both stores

- unique store package IDs retained
- stable CDN is reachable without authentication
- no franchise names/logos in commercial metadata unless separately licensed
- all release artwork cleared for commercial distribution
- support email and website live
- versioned catalog rollback tested
