# EasyCargo UI Overhaul Design

## Goal

Transform the app from a functional prototype into a bold, modern delivery app with a consistent orange-themed visual identity, clean white backgrounds, and rich information density.

## Color System

| Role | Color | Hex |
|------|-------|-----|
| Primary | Deep Orange | `#E65100` |
| Primary Variant | Bright Orange | `#FF6D00` |
| Secondary | Warm Gray | `#455A64` |
| Background | Off-white | `#FAFAFA` |
| Surface (cards) | White | `#FFFFFF` |
| Success (delivered) | Green | `#2E7D32` |
| Pending | Amber | `#FFA000` |
| Error / Delete | Red | `#D32F2F` |
| Money text | Dark Green | `#1B5E20` |
| On Primary | White | `#FFFFFF` |

## Theme

Create a custom `EasyCargoTheme` composable wrapping `MaterialTheme` with:
- Custom `lightColorScheme()` using the palette above
- Consistent shapes: `16.dp` cards, `12.dp` inputs, `CircleShape` badges
- Default Material3 typography (no custom fonts)
- Replace ALL hardcoded `Color(0xFF...)` values with theme semantics

Remove the background image from all screens. Use solid `#FAFAFA` background.

## Home Screen

### Top App Bar
- Solid orange `#E65100` background, white title "EasyCargo"
- Elevated with subtle shadow

### Route Cards (Rich Preview)
Each card shows:
- **Orange left border stripe** (4dp)
- **Route name** in `titleMedium` bold
- **Route ID + creation date** in `bodySmall` gray
- **Three stat chips**: delivered count, total parcels, total money collected
- **Mini progress bar**: green at 100%, orange otherwise
- Elevation: `8.dp`, corners: `16.dp`

Data requirement: Add a DAO query joining routes with aggregated parcel stats (delivered count, total count, total money) so the home screen can display these without loading each route's parcels.

### FAB
Orange background, white `+` icon.

### Empty State
Styled empty message card (existing `EmptyResultMessage` with better styling).

### Swipe-to-Delete
Keep existing behavior (works well). Update delete background to match theme red.

## Route Details Screen

### Top App Bar
- Orange background with white back arrow
- Route name as title, route date as subtitle

### Stats Header
- Delivery count (left) and total money (right) prominently displayed
- Wide progress bar below with percentage label
- Green bar at 100%, orange otherwise
- Search field integrated below with rounded corners and clear icon

### Parcel List Items
- **Parcel ID badge**: Orange rounded rectangle (replace yellow circle)
  - Shows route prefix (R5) and parcel number (001)
  - Gray when delivered
- **Customer name**: `bodyMedium` semi-bold
- **City**: Blue location pin icon + city name (keep existing)
- **Phone**: Gray phone icon + number (keep existing)
- **Price**: Bold green text
- **Piece count**: Gray text next to price
- **Delivered checkmark**: Green circle with white check (right side)
- **Delivered cards**: Badge turns gray, slight opacity reduction
- Thin divider between info section and price/pieces row

## Dialogs

### Add Route Dialog
- Same structure, orange "Create" button
- Orange-tinted outline on focused input
- Better spacing

### Add Parcel Dialog
- Same field structure (works well)
- Orange-tinted focus color on all inputs
- Total card gets orange-tinted background
- "Generaza Tichet" button: orange, full-width
- Divider between personal info and logistics sections
- Red border on validation failure

### Parcel Details Dialog
- Orange-tinted header area with prominent parcel ID
- Status as a rounded chip (green "Livrat" / amber "In asteptare")
- "Marcheaza Livrat" button in green, "Marcheaza Nelivrat" in gray
- Google Maps navigation button more prominent
- "Close" button as outlined text button

### Confirm Delete Dialog
- Keep existing AlertDialog style
- Red confirm button (already there)

## Files to Modify

1. **New file**: `ui/theme/Theme.kt` - Custom `EasyCargoTheme` with color scheme and shapes
2. **App.kt** - Wrap with `EasyCargoTheme`, remove background image, update HomeScreen and RouteCard
3. **RouteDetails.kt** - Redesign stats header, update parcel list items, update progress bar colors
4. **AddParcel.kt** - Orange accents, total card styling, button color
5. **AddRoute.kt** - Orange accents, button color
6. **ParcelDetails.kt** - Header styling, status chips, button colors
7. **Delete.kt** - Theme-consistent colors
8. **RouteDao.kt** - Add query for route stats (for rich home cards)
9. **RouteViewModel.kt** - Expose route stats for home screen

## Implementation Order

1. Create theme (foundation for everything)
2. Update App.kt and HomeScreen (most visible change)
3. Add route stats query + update RouteCard
4. Update RouteDetails screen
5. Update parcel list items
6. Update all dialogs
7. Polish and test
