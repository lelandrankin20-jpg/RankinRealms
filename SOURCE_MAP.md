# Verified Master Source Map

Checked against the actual **V31 portfolio imagery** and the original Drive files. The runtime app ships only lightweight previews; these Drive links are an internal source registry and are **not** intended as permanent production-CDN URLs.

| Master | Authoritative Drive file | Native raster | Current ICC | Verification |
|---|---|---:|---|---|
| Mountain Basin City | [`20260903-enhanced_1000077306.jpg`](https://drive.google.com/file/d/1CZ1Ogl6QI4UtakHy2d1RGbTPA0SwuTN0/view?usp=drivesdk) | 15,000 × 10,056 | sRGB | Exact visual match to V31 selected-master page 3 |
| The Veiled Valley | [`movie still3 (32).jpg`](https://drive.google.com/file/d/1z2IvpfRoKmQ4dhVctW10UUSC_ZjeCSwM/view?usp=drivesdk) | 15,000 × 8,438 | sRGB | Exact visual match to V31 selected-master page 4; later master replacing the older Veiled Valley source |
| Autumn Canal-City | [`20260903-enhanced_1000077305.jpg`](https://drive.google.com/file/d/1jmep0uBrNUJfR0bWze0sfkrO5pNmcg0W/view?usp=drivesdk) | 15,000 × 8,472 | sRGB | Exact visual match to V31 selected-master page 5 |
| Alpine Sanctuary | [`movie still3 (3).jpg`](https://drive.google.com/file/d/1hJ5S_eWMXWSjdpLQBsA0adW7GYTbCHkM/view?usp=drivesdk) | 15,000 × 9,360 | sRGB | Best/current V31 visual match; later near-duplicate of movie still3 (2).jpg |
| Forest Ravine | [`20260903-enhanced_1000077304.jpg`](https://drive.google.com/file/d/1TJknIYUkKBKxDe2z9mmxgkdG7cKqjBrh/view?usp=drivesdk) | 15,000 × 8,424 | sRGB | Exact visual match to V31 selected-master page 7 |
| Urban Megastructure | [`movie still3 (49).jpg`](https://drive.google.com/file/d/1NP3XMmjixu1O3ZvIoHqsb8c2Eg9hAqCq/view?usp=drivesdk) | 4,092 × 6,144 | sRGB | Exact visual match to the finished-VFX image on V31 page 8 |
| Sea-Cliff | [`movie still2.jpg`](https://drive.google.com/file/d/19ogVyi8NDg9serubTrDYLapHJ3HSQwUd/view?usp=drivesdk) | 15,000 × 8,433 | sRGB | Explicitly identified by the proven Sea-Cliff workflow and visually matched to V31 page 9 |
| Highland Citadel | [`enhanced_1000074315 (1).jpg`](https://drive.google.com/file/d/1xjfFVazehxre6bL4upss5NRf2PSIHorZ/view?usp=drivesdk) | 15,000 × 9,984 | Display P3 | Exact visual match to V31 selected-master page 10 and identified in the original workflow chat |
| The Last Terrace | [`movie still (10).jpg`](https://drive.google.com/file/d/1rDjmBajIeSBiqKnLHcnxMNEA2AWiaGT9/view?usp=drivesdk) | 15,000 × 9,984 | sRGB | Corrected master explicitly promoted in workflow records and visually matched to V31 page 11 |
| Mythic Environment Realism | [`movie still3 (43).jpg`](https://drive.google.com/file/d/1Yfctc8AFX5XZvd5SapGL91pTJUDnDBbn/view?usp=drivesdk) | 15,000 × 15,000 | sRGB | Pixel/visual match to V31 page 12; supersedes the previous erroneous movie still3 (42).jpg mapping |

## Corrections made during the V31 cross-check

- **The Veiled Valley:** the V31 master is `movie still3 (32).jpg`, not the older `movie still2 (12).jpg` that was authoritative in the earlier V23 set.
- **Mythic Environment Realism:** the V31 image matches `movie still3 (43).jpg`; the earlier MVP mapping to `(42)` was wrong.
- **Alpine Sanctuary:** V31 matches the later `movie still3 (3).jpg` marginally better than the near-duplicate `(2)` and it is the later Drive copy, so `(3)` is the current app-source mapping.
- **Urban Megastructure:** the finished VFX frame on V31 page 8 is `movie still3 (49).jpg`.

## Color-space check

These are the ICC profiles embedded in the exact Drive files at verification time. **Highland Citadel is already Display P3. The other nine verified Drive masters are currently sRGB.** For the commercial app, export delivery copies from the approved masters in Display P3 where the Lightroom source supports that export, retain the originals untouched, and keep an sRGB compatibility path.

## Production rule

Never substitute a portfolio-PDF extraction, preview JPEG, generated reconstruction, or arbitrary crop for these source masters. The app should give the user the full master and let the device framing UI decide what is visible.
