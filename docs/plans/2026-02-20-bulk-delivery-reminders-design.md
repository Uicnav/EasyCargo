# Bulk Delivery Reminders

**Date:** 2026-02-20
**Status:** Approved

## Summary

Send delivery reminder messages to all undelivered parcels with phone numbers on a route. Users choose between SMS (batch send) or WhatsApp (sequential). A confirmation dialog shows recipient count and channel choice before sending.

## Message Template

```
Hello {name}, your parcel ({pieceCount} pieces) is ready for delivery in {city}.
```

Fixed template with parcel data substituted per recipient.

## Recipients

- Only parcels where `isDelivered == false` AND `phone` is not blank
- Scoped to the current route

## Architecture

### Platform Abstraction (expect/actual)

```kotlin
// commonMain
data class SmsRecipient(val phone: String, val message: String)
expect fun sendBulkSms(recipients: List<SmsRecipient>)
expect fun sendWhatsAppMessage(phone: String, message: String)
```

Follows existing `ShareUtils.kt` pattern.

### Data Flow

1. User taps "Send Reminders" button on RouteDetails screen
2. Confirmation dialog shows: recipient count, channel selector (SMS/WhatsApp), message preview
3. User confirms -> messages sent via chosen channel
4. Summary snackbar: "X reminders sent via SMS" or "X sent, Y failed"

## UI Components

### Send Reminders Button
- Located in RouteDetails stats header (glass card), below the progress bar
- Glassmorphism-styled button with message icon
- Disabled/hidden when no eligible parcels exist

### Confirmation Dialog
- Glass-styled dialog matching app theme
- Header: "Send Delivery Reminders"
- Info: "{count} undelivered parcels with phone numbers"
- Channel: Two tappable chips -- "SMS" | "WhatsApp"
- Message preview with sample parcel data
- Buttons: "Cancel" | "Send All"

### Result Snackbar
- Success: "12 reminders sent via SMS"
- Partial: "10 sent, 2 failed"

## Platform Implementation

### Android - SMS
- `android.telephony.SmsManager.sendTextMessage()` for programmatic batch send
- Requires `SEND_SMS` runtime permission
- Add `<uses-permission android:name="android.permission.SEND_SMS"/>` to AndroidManifest.xml
- Send in coroutine loop, track success/failure count

### Android - WhatsApp
- `Intent(Intent.ACTION_VIEW)` with `https://wa.me/{phone}?text={encodedMessage}`
- Opens WhatsApp per contact sequentially (no batch API available)

### iOS - SMS
- `MFMessageComposeViewController` -- opens native Messages compose
- User taps send per message (iOS doesn't allow silent SMS)

### iOS - WhatsApp
- `UIApplication.openURL` with `whatsapp://send?phone={phone}&text={message}`

## Error Handling

| Scenario | Behavior |
|----------|----------|
| No eligible parcels | Button disabled/hidden, "No parcels to notify" |
| Empty phone field | Skip parcel, count in summary |
| SMS permission denied | Snackbar with "Settings" action |
| SMS send failure | Count failures, report in summary |
| WhatsApp not installed | Gray out WhatsApp chip, "Not installed" label |

## i18n

All user-facing strings added to string resources in all existing languages: RO (base), EN, IT, DE, FR, ES.
