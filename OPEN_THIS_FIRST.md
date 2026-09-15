# Rankin Realms V3 — Native Wallpaper Test

The HTML file is only a visual prototype. A browser page cannot call Android WallpaperManager and therefore cannot directly change the Home or Lock wallpaper.

This Android project contains the real wallpaper path:

Gallery -> Artwork -> Set as wallpaper -> choose Crop / Whole master / Scroll -> Home / Lock / Both -> Apply wallpaper.

V3 development behavior:
- The Apply Wallpaper button is enabled for every artwork.
- Until production CDN master URLs are configured, the app uses the bundled preview image as a test source.
- This allows the native wallpaper-changing UX to be tested without pretending the preview is the production-quality master.
- Production release must replace the fallback with verified full-resolution master delivery.

The project declares INTERNET and SET_WALLPAPER and uses Android WallpaperManager with FLAG_SYSTEM / FLAG_LOCK.

A GitHub Actions workflow is included to build an installable debug APK on a hosted Android build runner.
