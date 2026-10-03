# RideFlow Design System — Source of Truth

This file defines every design token and base component for RideFlow.
It is used twice:

1. **Now:** Claude Code builds the `Design System` page in Penpot from it via the Penpot MCP.
2. **Later:** Claude Code generates `:core:designsystem` (Compose theme + components) from it.

If a value changes, change it here first, then update Penpot and code.

---

## 1. Penpot File Setup

| Item | Value |
|---|---|
| File name | `RideFlow Design` |
| First page | `Design System` |
| Other pages (later) | `P02 Onboarding`, `P03 Home`, `P04 Ride Booking`, ... (one per phase) |
| Screen frame size | 360 × 800 |
| Grid | 4pt baseline, 8pt layout grid, 16 side margins |

Boards on the `Design System` page, left to right:
`Colors` → `Typography` → `Spacing & Shape` → `Elevation` → `Icons & Markers` → `Buttons` → `Inputs` → `Navigation & Sheets` → `Cards` → `Feedback` → `Status` → `Pricing` → `Driver`

Every component is a Penpot **main component**, so screens use instances, not copies.

---

## 2. Color

Names follow Material 3 roles so they map 1:1 to `ColorScheme` in Compose.

### Light (design this first)

| Token | Hex | Use |
|---|---|---|
| `primary` | `#0F766E` | Main actions, selected states, route line |
| `onPrimary` | `#FFFFFF` | Text/icons on primary |
| `primaryContainer` | `#CCFBF1` | Selected vehicle card, chips |
| `onPrimaryContainer` | `#042F2E` | Text on primaryContainer |
| `secondary` | `#1E293B` | Secondary buttons, dark accents |
| `onSecondary` | `#FFFFFF` | Text on secondary |
| `background` | `#F8FAFC` | Screen background |
| `onBackground` | `#0F172A` | Main text |
| `surface` | `#FFFFFF` | Cards, sheets, app bars |
| `onSurface` | `#0F172A` | Text on surface |
| `surfaceVariant` | `#F1F5F9` | Input fill, list row background |
| `onSurfaceVariant` | `#475569` | Secondary text, hints |
| `outline` | `#CBD5E1` | Borders, dividers |
| `success` | `#16A34A` | Payment success, driver online |
| `warning` | `#CA8A04` | Off-route, taking longer than usual |
| `error` | `#DC2626` | Errors, cancel actions |
| `onError` | `#FFFFFF` | Text on error |
| `surge` | `#EA580C` | Surge badge and surge fare line only |
| `scrim` | `#0F172A` at 40% | Behind dialogs and expanded sheets |

### Dark (Phase 11 — define now, design later)

| Token | Hex |
|---|---|
| `primary` | `#5EEAD4` |
| `onPrimary` | `#042F2E` |
| `primaryContainer` | `#115E59` |
| `onPrimaryContainer` | `#CCFBF1` |
| `secondary` | `#CBD5E1` |
| `onSecondary` | `#0F172A` |
| `background` | `#0B1220` |
| `onBackground` | `#E2E8F0` |
| `surface` | `#111827` |
| `onSurface` | `#E2E8F0` |
| `surfaceVariant` | `#1E293B` |
| `onSurfaceVariant` | `#94A3B8` |
| `outline` | `#334155` |
| `success` | `#4ADE80` |
| `warning` | `#FACC15` |
| `error` | `#F87171` |
| `onError` | `#450A0A` |
| `surge` | `#FB923C` |

Rules:
- `surge` is used only for surge pricing so users learn its meaning.
- Never rely on color alone: surge, error, and success always have an icon or label too.
- Body text must meet 4.5:1 contrast against its background.

---

## 3. Typography

Font: **Roboto** (matches Android system font; available in Penpot via Google Fonts).

| Token | Size / Line height | Weight | Typical use |
|---|---|---|---|
| `displaySmall` | 36 / 44 | Regular | Fare total on receipt |
| `headlineMedium` | 28 / 36 | Regular | Onboarding titles |
| `headlineSmall` | 24 / 32 | Regular | Screen titles |
| `titleLarge` | 22 / 28 | Regular | Sheet titles, top app bar |
| `titleMedium` | 16 / 24 | Medium | Card titles, driver name |
| `titleSmall` | 14 / 20 | Medium | Section labels |
| `bodyLarge` | 16 / 24 | Regular | Main body text, addresses |
| `bodyMedium` | 14 / 20 | Regular | Secondary info |
| `bodySmall` | 12 / 16 | Regular | Captions, timestamps |
| `labelLarge` | 14 / 20 | Medium | Button text |
| `labelMedium` | 12 / 16 | Medium | Chips, badges |
| `labelSmall` | 11 / 16 | Medium | ETA chip, tiny labels |

---

## 4. Spacing & Shape

### Spacing

| Token | Value | Use |
|---|---|---|
| `xxs` | 4 | Icon-to-text gap |
| `xs` | 8 | Inside compact components |
| `sm` | 12 | Between related items |
| `md` | 16 | Screen side margins, card padding |
| `lg` | 24 | Between sections |
| `xl` | 32 | Large section breaks |

### Shape (corner radius)

| Token | Value | Use |
|---|---|---|
| `small` | 8 | Chips, badges, input fields |
| `medium` | 12 | Cards, buttons |
| `large` | 16 | Dialogs, large cards |
| `sheet` | 24 | Bottom sheet top corners only |
| `full` | 999 | Avatars, FAB, toggle, ETA chip |

---

## 5. Elevation

| Token | Shadow | Use |
|---|---|---|
| `level0` | none | Flat list rows |
| `level1` | 0 1 3 rgba(15,23,42,0.12) | Cards |
| `level2` | 0 4 12 rgba(15,23,42,0.14) | Floating search card on map, FAB |
| `level3` | 0 -4 16 rgba(15,23,42,0.16) | Bottom sheets (shadow points up) |

Anything floating over the map needs at least `level2` so it stays readable.

---

## 6. Icons & Map Markers

- Icons: **Material Symbols Rounded**, 24 × 24, stroke matches `onSurface` / `onSurfaceVariant`.
- Minimum touch target for any icon button: 48 × 48.

| Marker | Size | Design |
|---|---|---|
| Current location | 20 | `primary` dot, white 3px ring, soft `primary` halo at 20% |
| Pickup pin | 32 | Circle, `success` fill, white inner dot |
| Drop pin | 32 | Square, `secondary` fill, white inner square |
| Driver car | 40 | Top-down car icon on white circle, `level2` shadow, rotatable |
| Route polyline | 5px | `primary`, round caps; completed part `outline` |
| ETA chip | height 24 | `secondary` fill, `labelSmall` white text, `full` radius |

---

## 7. Components

Build each as a main component with the listed variants. Sizes are in dp.

### Buttons

| Component | Size | Variants |
|---|---|---|
| Primary button | height 56, full width, `medium` radius | Default, Pressed, Disabled, Loading (spinner replaces text) |
| Secondary button | height 56, outline 1px `outline` | Default, Pressed, Disabled |
| Text button | height 40 | Default, Pressed, Disabled, Destructive (`error` text) |
| Icon button | 48 × 48, icon 24 | Default, Filled (`surface` + `level2`, for over-map use) |
| FAB (recenter) | 56 × 56, `full` radius | Default |

### Inputs

| Component | Size | Variants |
|---|---|---|
| Text field | height 56, `surfaceVariant` fill, `small` radius | Empty, Focused (`primary` 2px border), Filled, Error (`error` border + helper text), Disabled |
| Phone field | height 56 | Country code segment + number segment; same states as text field |
| OTP input | 6 boxes, each 48 × 56, gap 8 | Empty, Active box, Filled, Error, Success |
| Search field | height 48, `full` radius, leading search icon | Empty, Focused, Typing (clear icon) |
| Promo code field | text field + inline "Apply" text button | Default, Applying, Applied, Invalid |

### Navigation & Sheets

| Component | Spec | Variants |
|---|---|---|
| Top app bar | height 64, `surface`, `titleLarge` | With back, With back + action |
| Bottom sheet | `sheet` radius top, `level3`, drag handle 32 × 4 `outline` | Peek (~25%), Half, Full |
| Dialog | width 312, `large` radius, padding 24 | Info, Confirm (2 buttons), Destructive |

### Cards

| Component | Spec | Variants |
|---|---|---|
| Vehicle option card | height 72; icon 48, name + ETA left, price right | Default, Selected (`primaryContainer` + `primary` border), Unavailable (50% opacity), Surge (surge badge) |
| Ride summary card | pickup row, dotted connector, drop row, divider, fare + payment | Default, Compact |
| Place row | height 64, icon + title + subtitle | Recent (clock icon), Search result (pin icon), Saved (star) |
| Driver info card | avatar 56, name, rating, car model, plate badge, call + message icon buttons | Default |
| Payment method row | height 64, method icon, label, default marker | Default, Selected, Disabled |
| Ride history row | date, route (2 lines), fare right, status chip | Completed, Cancelled |

### Feedback

| Component | Spec | Variants |
|---|---|---|
| Snackbar | `secondary` fill, white text, optional action | Plain, With action, With countdown (undo) |
| Status banner | full width, icon + text + optional action | Info, Success, Warning, Error |
| Offline banner | height 32, `secondary` fill | Offline, Reconnecting (with spinner) |
| Skeleton loader | `surfaceVariant` blocks, `small` radius | Text line, Card, List row, Map placeholder |
| Spinner | 24 and 48 | `primary` |

### Status

| Component | Spec | Variants |
|---|---|---|
| Empty state block | 120 illustration, `titleMedium`, `bodyMedium`, optional button | Generic, No rides, No drivers, No results |
| Error state block | same layout as empty, error icon | Network, Server, Not serviceable |
| Countdown ring | 56, `primary` stroke 4px, number in center | Full, Half, Ending (`error` stroke) |
| Rating stars | 5 stars, 24 or 32 | Display only, Interactive (0–5) |
| Avatar | 40 / 56 / 96, `full` radius | Image, Initials, Placeholder |
| Chip | height 32, `small` radius | Default, Selected, With icon |

### Pricing

| Component | Spec | Variants |
|---|---|---|
| Fare line row | label left (`bodyMedium`), amount right | Normal, Surge (`surge` color + icon), Discount (`success`, negative amount) |
| Total row | `titleMedium` label, `titleLarge` amount, divider above | Default |
| Surge badge | `surge` fill, white `labelMedium`, flame icon | e.g. "1.5×" |

### Driver

| Component | Spec | Variants |
|---|---|---|
| Online toggle | pill 160 × 56, `full` radius | Offline (`outline`), Going online (spinner), Online (`success`) |
| Ride offer actions | Reject (secondary) + Accept (primary) side by side, countdown ring above | Default, Accepting, Expired |

---

## 8. Rules for Claude Code

When building in Penpot:
1. Create the `Colors` and `Typography` boards first as Penpot color and typography assets, then **stop for review**.
2. Build every component using only these tokens. No new colors or font sizes.
3. Name layers and components by the names in this file (e.g. `Button/Primary/Loading`).
4. After each board, report what was created and any value you had to guess.

When generating `:core:designsystem` later:
- `Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`, `Elevation.kt` map directly from sections 2–5.
- `success`, `warning`, `surge` are not part of M3's `ColorScheme`: expose them through a `RideFlowExtendedColors` via `CompositionLocal`.
- Each component in section 7 becomes one composable with its variants as parameters, plus `@Preview`s for every variant.