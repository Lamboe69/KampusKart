# KampusKart — Project Status

## Goal
Cross-platform campus marketplace with web frontend/backend and Flutter mobile app pulling live data from the same API.

## Constraints
- Node.js + SQLite backend on `localhost:3001` serves the web SPA
- Spring Boot backend is an in-progress rewrite (not running)
- All seed users: password `password123`
- Web files under `web/`, Flutter app under `mobile/`
- Flutter tested via `flutter run -d chrome`
- Android APK build blocked by disk space issues & Kotlin daemon bug

## Architecture
- **Backend**: `web/server/` — Express.js + SQLite (`server/database.sqlite`)
- **Web SPA**: `web/` — React frontend (unchanged, leave alone)
- **Flutter app**: `mobile/` — Dart/Flutter with `provider` state management

## Key Config
- `mobile/lib/config/api_config.dart`: `baseUrl` = `http://10.0.2.2:3001/api` (Android emulator) or `http://localhost:3001/api` (web/desktop)
- Server first-run auto-seeds SQLite via `web/server/seed.js`

## Completed Features
- Flutter project scaffold (10 screens, 4 widgets, dark theme, provider state)
- All model fixes for real API:
  - `api_config.dart` — platform-aware baseUrl
  - `user.dart` — String id, maps type→role, wallet_balance→balance, verified 1/0→bool
  - `shop.dart` — String id, verified 1/0→bool
  - `product.dart` — parse JSON-string delivery_zones/delivery_fees
  - `order.dart` — String id/userId, price from amount, deliveryCampus from delivery_to
  - `product_card.dart` — FittedBox price row to prevent overflow
- Home screen redesign:
  - Categories + Browse button in a Wrap row under title (no horizontal scroll)
  - Browse button at far right of Wrap
  - Bottom nav: Home, Shops, Orders, Profile, Wallet
  - Browse Products as standalone Scaffold with AppBar (Navigator.push)
- `app_provider.dart`: selectedCategory, filteredProducts, setCategory()
- Verified API returns 1900 products, 22 sellers
- `flutter build web` succeeded — output in `mobile/build/web/`
- Gradle caches cleaned (~9.6 GB freed, C: drive now 12 GB free)
- Git: committed as `c44d517`

## Next Steps (Priority Order)
1. `flutter build apk --debug` — retry now that C: drive has 12 GB free
2. WebSocket chat in Flutter (mirror `server/routes/messages.js`)
3. Image upload screen for seller flow (currently text-only URL)
4. Search, filter browsing, wallet withdrawal, dispute screens
5. Push notifications / real-time order updates

## Blockers
- Android APK: Kotlin daemon incremental cache issue (shared_preferences_android); disk space resolved (12 GB free)

## Models Overview
| Model | File | Key Mappings |
|-------|------|-------------|
| User | `models/user.dart` | `type`→`role`, `wallet_balance`→`balance`, `verified` int→bool |
| Shop | `models/shop.dart` | `id` String, `verified` int→bool |
| Product | `models/product.dart` | `delivery_zones`/`delivery_fees` JSON-string parsed via dart:convert |
| Order | `models/order.dart` | `id`/`userId` String, `price` from `amount`, `deliveryCampus` from `delivery_to` |

## Relevant Files
- `mobile/lib/main.dart` — app entry, MultiProvider
- `mobile/lib/config/api_config.dart` — platform-aware baseUrl
- `mobile/lib/providers/app_provider.dart` — auth, products, cart, state
- `mobile/lib/screens/home_screen.dart` — Wrap categories, bottom nav
- `mobile/lib/theme/app_theme.dart` — dark theme constants
- `web/server/` — Express.js backend (untouched)
