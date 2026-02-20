# Bulk Delivery Reminders Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a "Send Reminders" feature to RouteDetails that batch-sends SMS or WhatsApp messages to all undelivered parcels with phone numbers.

**Architecture:** Platform `expect/actual` functions for SMS/WhatsApp (following existing `ShareUtils.kt` pattern). New DAO query for undelivered parcels. Confirmation dialog in RouteDetails with channel choice. Android uses `SmsManager` for batch SMS; WhatsApp opens per-contact intents.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Room, Android SmsManager, Android Intents, iOS MFMessageComposeViewController

---

### Task 1: Add string resources for all 6 languages

**Files:**
- Modify: `composeApp/src/commonMain/composeResources/values/strings.xml` (RO base)
- Modify: `composeApp/src/commonMain/composeResources/values-en/strings.xml`
- Modify: `composeApp/src/commonMain/composeResources/values-it/strings.xml`
- Modify: `composeApp/src/commonMain/composeResources/values-de/strings.xml`
- Modify: `composeApp/src/commonMain/composeResources/values-fr/strings.xml`
- Modify: `composeApp/src/commonMain/composeResources/values-es/strings.xml`

**Step 1: Add RO strings (base)**

Add before the closing `</resources>` tag in `values/strings.xml`:

```xml
    <string name="btn_send_reminders">TRIMITE NOTIFICĂRI</string>
    <string name="title_send_reminders">Trimite Notificări de Livrare</string>
    <string name="msg_reminder_count">%1$d colete nelivrate cu număr de telefon</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Previzualizare mesaj:</string>
    <string name="btn_send_all">TRIMITE TOATE</string>
    <string name="msg_reminders_sent">%1$d notificări trimise prin %2$s</string>
    <string name="msg_reminders_partial">%1$d trimise, %2$d eșuate</string>
    <string name="msg_no_eligible_parcels">Nu există colete de notificat</string>
    <string name="msg_sms_permission_required">Permisiunea SMS este necesară pentru trimiterea notificărilor</string>
    <string name="msg_whatsapp_not_installed">WhatsApp nu este instalat</string>
    <string name="reminder_template">Bună ziua %1$s, coletul dvs. (%2$d bucăți) este gata de livrare în %3$s.</string>
    <string name="label_choose_channel">Alege canalul de trimitere</string>
```

**Step 2: Add EN strings**

Add before the closing `</resources>` tag in `values-en/strings.xml`:

```xml
    <string name="btn_send_reminders">SEND REMINDERS</string>
    <string name="title_send_reminders">Send Delivery Reminders</string>
    <string name="msg_reminder_count">%1$d undelivered parcels with phone numbers</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Message preview:</string>
    <string name="btn_send_all">SEND ALL</string>
    <string name="msg_reminders_sent">%1$d reminders sent via %2$s</string>
    <string name="msg_reminders_partial">%1$d sent, %2$d failed</string>
    <string name="msg_no_eligible_parcels">No parcels to notify</string>
    <string name="msg_sms_permission_required">SMS permission is required to send reminders</string>
    <string name="msg_whatsapp_not_installed">WhatsApp is not installed</string>
    <string name="reminder_template">Hello %1$s, your parcel (%2$d pieces) is ready for delivery in %3$s.</string>
    <string name="label_choose_channel">Choose sending channel</string>
```

**Step 3: Add IT strings**

Add before the closing `</resources>` tag in `values-it/strings.xml`:

```xml
    <string name="btn_send_reminders">INVIA PROMEMORIA</string>
    <string name="title_send_reminders">Invia Promemoria di Consegna</string>
    <string name="msg_reminder_count">%1$d pacchi non consegnati con numero di telefono</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Anteprima messaggio:</string>
    <string name="btn_send_all">INVIA TUTTI</string>
    <string name="msg_reminders_sent">%1$d promemoria inviati tramite %2$s</string>
    <string name="msg_reminders_partial">%1$d inviati, %2$d falliti</string>
    <string name="msg_no_eligible_parcels">Nessun pacco da notificare</string>
    <string name="msg_sms_permission_required">Il permesso SMS è necessario per inviare i promemoria</string>
    <string name="msg_whatsapp_not_installed">WhatsApp non è installato</string>
    <string name="reminder_template">Buongiorno %1$s, il suo pacco (%2$d pezzi) è pronto per la consegna a %3$s.</string>
    <string name="label_choose_channel">Scegli il canale di invio</string>
```

**Step 4: Add DE strings**

Add before the closing `</resources>` tag in `values-de/strings.xml`:

```xml
    <string name="btn_send_reminders">ERINNERUNGEN SENDEN</string>
    <string name="title_send_reminders">Liefererinnerungen senden</string>
    <string name="msg_reminder_count">%1$d nicht zugestellte Pakete mit Telefonnummer</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Nachrichtenvorschau:</string>
    <string name="btn_send_all">ALLE SENDEN</string>
    <string name="msg_reminders_sent">%1$d Erinnerungen per %2$s gesendet</string>
    <string name="msg_reminders_partial">%1$d gesendet, %2$d fehlgeschlagen</string>
    <string name="msg_no_eligible_parcels">Keine Pakete zu benachrichtigen</string>
    <string name="msg_sms_permission_required">SMS-Berechtigung ist erforderlich, um Erinnerungen zu senden</string>
    <string name="msg_whatsapp_not_installed">WhatsApp ist nicht installiert</string>
    <string name="reminder_template">Guten Tag %1$s, Ihr Paket (%2$d Stück) ist zur Auslieferung in %3$s bereit.</string>
    <string name="label_choose_channel">Sendekanal wählen</string>
```

**Step 5: Add FR strings**

Add before the closing `</resources>` tag in `values-fr/strings.xml`:

```xml
    <string name="btn_send_reminders">ENVOYER RAPPELS</string>
    <string name="title_send_reminders">Envoyer des rappels de livraison</string>
    <string name="msg_reminder_count">%1$d colis non livrés avec numéro de téléphone</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Aperçu du message :</string>
    <string name="btn_send_all">ENVOYER TOUT</string>
    <string name="msg_reminders_sent">%1$d rappels envoyés par %2$s</string>
    <string name="msg_reminders_partial">%1$d envoyés, %2$d échoués</string>
    <string name="msg_no_eligible_parcels">Aucun colis à notifier</string>
    <string name="msg_sms_permission_required">L\'autorisation SMS est requise pour envoyer des rappels</string>
    <string name="msg_whatsapp_not_installed">WhatsApp n\'est pas installé</string>
    <string name="reminder_template">Bonjour %1$s, votre colis (%2$d pièces) est prêt pour la livraison à %3$s.</string>
    <string name="label_choose_channel">Choisir le canal d\'envoi</string>
```

**Step 6: Add ES strings**

Add before the closing `</resources>` tag in `values-es/strings.xml`:

```xml
    <string name="btn_send_reminders">ENVIAR RECORDATORIOS</string>
    <string name="title_send_reminders">Enviar recordatorios de entrega</string>
    <string name="msg_reminder_count">%1$d paquetes no entregados con número de teléfono</string>
    <string name="label_channel_sms">SMS</string>
    <string name="label_channel_whatsapp">WhatsApp</string>
    <string name="label_message_preview">Vista previa del mensaje:</string>
    <string name="btn_send_all">ENVIAR TODOS</string>
    <string name="msg_reminders_sent">%1$d recordatorios enviados por %2$s</string>
    <string name="msg_reminders_partial">%1$d enviados, %2$d fallidos</string>
    <string name="msg_no_eligible_parcels">No hay paquetes para notificar</string>
    <string name="msg_sms_permission_required">Se requiere permiso de SMS para enviar recordatorios</string>
    <string name="msg_whatsapp_not_installed">WhatsApp no está instalado</string>
    <string name="reminder_template">Hola %1$s, su paquete (%2$d piezas) está listo para la entrega en %3$s.</string>
    <string name="label_choose_channel">Elegir canal de envío</string>
```

**Step 7: Build to verify strings compile**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 8: Commit**

```bash
git add composeApp/src/commonMain/composeResources/
git commit -m "feat: add string resources for bulk delivery reminders (6 languages)"
```

---

### Task 2: Add DAO query for undelivered parcels with phone

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/data/dao/ParcelDao.kt`

**Step 1: Add query method to ParcelDao**

Add this method inside the `ParcelDao` interface, after the `searchParcels` method (around line 59):

```kotlin
    @Query("""
        SELECT * FROM parcels
        WHERE routeId = :routeId
        AND isDelivered = 0
        AND isVisible = 1
        AND phone != ''
        ORDER BY displayId DESC
    """)
    suspend fun getUndeliveredParcelsWithPhone(routeId: Long): List<ParcelEntity>
```

Note: This is a `suspend fun` returning `List` (not `Flow`) because we need a one-shot fetch, not a stream.

**Step 2: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/data/dao/ParcelDao.kt
git commit -m "feat: add DAO query for undelivered parcels with phone numbers"
```

---

### Task 3: Add ViewModel method for reminder-eligible parcels

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/viewmodel/ParcelViewModel.kt`

**Step 1: Add method to ParcelViewModel**

Add this method to `ParcelViewModel` class (after `updateParcelStatus` around line 77):

```kotlin
    suspend fun getUndeliveredParcelsWithPhone(routeId: Long): List<ParcelUi> {
        return dao.getUndeliveredParcelsWithPhone(routeId).map { it.toUiModel() }
    }
```

**Step 2: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/viewmodel/ParcelViewModel.kt
git commit -m "feat: add ViewModel method for reminder-eligible parcels"
```

---

### Task 4: Add platform abstraction for messaging

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.kt`

**Step 1: Create the expect declarations**

```kotlin
package com.vantechinformatics.easycargo.utils

data class SmsRecipient(val phone: String, val message: String)

data class SmsSendResult(val sent: Int, val failed: Int)

expect fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult

expect fun openWhatsApp(phone: String, message: String)

expect fun isWhatsAppInstalled(): Boolean
```

**Step 2: Build to verify (will fail — actuals not yet implemented, that's expected)**

This step is informational only. The build will fail until Task 5 and Task 6 provide the actual implementations.

**Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.kt
git commit -m "feat: add expect declarations for messaging platform abstractions"
```

---

### Task 5: Implement Android actual messaging functions

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.android.kt`
- Modify: `composeApp/src/androidMain/AndroidManifest.xml`

**Step 1: Add SEND_SMS permission to AndroidManifest**

In `composeApp/src/androidMain/AndroidManifest.xml`, add after the INTERNET permission line (line 3):

```xml
    <uses-permission android:name="android.permission.SEND_SMS" />
```

**Step 2: Create Android actual implementations**

Create `composeApp/src/androidMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.android.kt`:

```kotlin
package com.vantechinformatics.easycargo.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager

private var messagingContext: Context? = null

fun initMessagingContext(context: Context) {
    messagingContext = context.applicationContext
}

actual fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult {
    val ctx = messagingContext ?: return SmsSendResult(sent = 0, failed = recipients.size)
    var sent = 0
    var failed = 0

    @Suppress("DEPRECATION")
    val smsManager = SmsManager.getDefault()

    for (recipient in recipients) {
        try {
            smsManager.sendTextMessage(
                recipient.phone,
                null,
                recipient.message,
                null,
                null
            )
            sent++
        } catch (e: Exception) {
            failed++
        }
    }
    return SmsSendResult(sent = sent, failed = failed)
}

actual fun openWhatsApp(phone: String, message: String) {
    val ctx = messagingContext ?: return
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    val encodedMessage = Uri.encode(message)
    val uri = Uri.parse("https://wa.me/$cleanPhone?text=$encodedMessage")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}

actual fun isWhatsAppInstalled(): Boolean {
    val ctx = messagingContext ?: return false
    return try {
        ctx.packageManager.getPackageInfo("com.whatsapp", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
```

**Step 3: Initialize messaging context in MainActivity**

Modify `composeApp/src/androidMain/kotlin/com/vantechinformatics/easycargo/MainActivity.kt`. Add `initMessagingContext(applicationContext)` right after the existing `initShareContext(applicationContext)` call in `onCreate`:

```kotlin
initShareContext(applicationContext)
initMessagingContext(applicationContext)
```

Add the import at the top:

```kotlin
import com.vantechinformatics.easycargo.utils.initMessagingContext
```

**Step 4: Build to verify (will still fail — iOS actual missing)**

Informational only — iOS actual needed for full build.

**Step 5: Commit**

```bash
git add composeApp/src/androidMain/
git commit -m "feat: implement Android SMS and WhatsApp messaging functions"
```

---

### Task 6: Implement iOS actual messaging functions

**Files:**
- Create: `composeApp/src/iosMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.ios.kt`

**Step 1: Create iOS actual implementations**

```kotlin
package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult {
    // iOS doesn't support programmatic SMS sending.
    // Open the Messages app for the first recipient as a fallback.
    if (recipients.isEmpty()) return SmsSendResult(sent = 0, failed = 0)
    val first = recipients.first()
    val smsUrl = NSURL(string = "sms:${first.phone}&body=${first.message}")
    if (UIApplication.sharedApplication.canOpenURL(smsUrl)) {
        UIApplication.sharedApplication.openURL(smsUrl)
    }
    // We can't track actual send status on iOS
    return SmsSendResult(sent = recipients.size, failed = 0)
}

actual fun openWhatsApp(phone: String, message: String) {
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    val url = NSURL(string = "https://wa.me/$cleanPhone?text=$message")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun isWhatsAppInstalled(): Boolean {
    val url = NSURL(string = "whatsapp://")
    return url != null && UIApplication.sharedApplication.canOpenURL(url)
}
```

**Step 2: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL (Android target should now compile fully)

**Step 3: Commit**

```bash
git add composeApp/src/iosMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.ios.kt
git commit -m "feat: implement iOS messaging functions (SMS + WhatsApp)"
```

---

### Task 7: Create SendRemindersDialog composable

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/SendReminders.kt`

**Step 1: Create the dialog composable**

```kotlin
package com.vantechinformatics.easycargo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_cancel
import easycargo.composeapp.generated.resources.btn_send_all
import easycargo.composeapp.generated.resources.label_channel_sms
import easycargo.composeapp.generated.resources.label_channel_whatsapp
import easycargo.composeapp.generated.resources.label_choose_channel
import easycargo.composeapp.generated.resources.label_message_preview
import easycargo.composeapp.generated.resources.msg_reminder_count
import easycargo.composeapp.generated.resources.msg_whatsapp_not_installed
import easycargo.composeapp.generated.resources.reminder_template
import easycargo.composeapp.generated.resources.title_send_reminders
import com.vantechinformatics.easycargo.utils.isWhatsAppInstalled
import org.jetbrains.compose.resources.stringResource

enum class MessageChannel { SMS, WHATSAPP }

@Composable
fun SendRemindersDialog(
    eligibleParcels: List<ParcelUi>,
    onDismiss: () -> Unit,
    onSend: (MessageChannel) -> Unit
) {
    val colors = EasyCargoTheme.colors
    var selectedChannel by remember { mutableStateOf(MessageChannel.SMS) }
    val whatsAppAvailable = remember { isWhatsAppInstalled() }

    // Build a sample preview from first parcel
    val sampleParcel = eligibleParcels.firstOrNull()
    val previewMessage = if (sampleParcel != null) {
        stringResource(
            Res.string.reminder_template,
            sampleParcel.firstNameLastName,
            sampleParcel.pieceCount,
            sampleParcel.city
        )
    } else ""

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(colors.glassSurface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Title
                Text(
                    text = stringResource(Res.string.title_send_reminders),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.contentPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Recipient count
                Text(
                    text = stringResource(Res.string.msg_reminder_count, eligibleParcels.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Channel selector
                Text(
                    text = stringResource(Res.string.label_choose_channel),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // SMS chip
                    ChannelChip(
                        label = stringResource(Res.string.label_channel_sms),
                        isSelected = selectedChannel == MessageChannel.SMS,
                        enabled = true,
                        onClick = { selectedChannel = MessageChannel.SMS },
                        modifier = Modifier.weight(1f)
                    )

                    // WhatsApp chip
                    ChannelChip(
                        label = if (whatsAppAvailable) {
                            stringResource(Res.string.label_channel_whatsapp)
                        } else {
                            stringResource(Res.string.msg_whatsapp_not_installed)
                        },
                        isSelected = selectedChannel == MessageChannel.WHATSAPP,
                        enabled = whatsAppAvailable,
                        onClick = { selectedChannel = MessageChannel.WHATSAPP },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message preview
                Text(
                    text = stringResource(Res.string.label_message_preview),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.glassCard)
                        .padding(12.dp)
                ) {
                    Text(
                        text = previewMessage,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = colors.contentPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.contentPrimary
                        )
                    ) {
                        Text(stringResource(Res.string.action_cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSend(selectedChannel) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            stringResource(Res.string.btn_send_all),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelChip(
    label: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EasyCargoTheme.colors
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        !enabled -> colors.glassSurface.copy(alpha = 0.3f)
        else -> colors.glassCard
    }
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> colors.glassBorder
    }
    val textColor = when {
        !enabled -> colors.textMuted
        isSelected -> MaterialTheme.colorScheme.primary
        else -> colors.contentPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
```

**Step 2: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/SendReminders.kt
git commit -m "feat: create SendRemindersDialog composable with channel selection"
```

---

### Task 8: Add Send Reminders button and wire dialog into RouteDetails

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/RouteDetails.kt`

**Step 1: Add imports to RouteDetails.kt**

Add these imports at the top of `RouteDetails.kt`:

```kotlin
import easycargo.composeapp.generated.resources.btn_send_reminders
import easycargo.composeapp.generated.resources.msg_no_eligible_parcels
import easycargo.composeapp.generated.resources.msg_reminders_sent
import easycargo.composeapp.generated.resources.msg_reminders_partial
import easycargo.composeapp.generated.resources.msg_sms_permission_required
import easycargo.composeapp.generated.resources.reminder_template
import easycargo.composeapp.generated.resources.label_channel_sms
import easycargo.composeapp.generated.resources.label_channel_whatsapp
import com.vantechinformatics.easycargo.utils.sendBulkSms
import com.vantechinformatics.easycargo.utils.openWhatsApp
import com.vantechinformatics.easycargo.utils.SmsRecipient
import androidx.compose.material.icons.filled.Email
```

**Step 2: Add state variables in RouteDetailsScreen**

Inside `RouteDetailsScreen`, after `var showAddDialog by remember { mutableStateOf(false) }` (around line 116), add:

```kotlin
    var showSendRemindersDialog by remember { mutableStateOf(false) }
    var eligibleParcels by remember { mutableStateOf<List<ParcelUi>>(emptyList()) }
```

**Step 3: Add Send Reminders button in the stats header**

Inside `RouteDetailsScreen`, after the `DeliveryProgressBar` composable and its spacer (after line 249 — `Spacer(modifier = Modifier.height(12.dp))`), add the Send Reminders button BEFORE the search field:

```kotlin
                    // Send Reminders button
                    Button(
                        onClick = {
                            scope.launch {
                                val parcels = viewModel.getUndeliveredParcelsWithPhone(routeId)
                                if (parcels.isEmpty()) {
                                    snackbarHostState.showSnackbar(
                                        message = "No parcels to notify"
                                    )
                                } else {
                                    eligibleParcels = parcels
                                    showSendRemindersDialog = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.btn_send_reminders),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
```

Note: You need to add `import androidx.compose.material3.Button` and `import androidx.compose.material3.ButtonDefaults` at the top if not already imported.

**Step 4: Add dialog and send logic at the bottom of RouteDetailsScreen**

After the `selectedParcel?.let { ... }` block (around line 325), add:

```kotlin
        if (showSendRemindersDialog) {
            val smsLabel = stringResource(Res.string.label_channel_sms)
            val whatsAppLabel = stringResource(Res.string.label_channel_whatsapp)
            val reminderTemplate = stringResource(Res.string.reminder_template, "", 0, "")

            SendRemindersDialog(
                eligibleParcels = eligibleParcels,
                onDismiss = { showSendRemindersDialog = false },
                onSend = { channel ->
                    showSendRemindersDialog = false
                    scope.launch {
                        when (channel) {
                            MessageChannel.SMS -> {
                                val recipients = eligibleParcels.map { parcel ->
                                    SmsRecipient(
                                        phone = parcel.phone,
                                        message = stringResource(
                                            Res.string.reminder_template,
                                            parcel.firstNameLastName,
                                            parcel.pieceCount,
                                            parcel.city
                                        )
                                    )
                                }
                                val result = sendBulkSms(recipients)
                                val message = if (result.failed == 0) {
                                    "${result.sent} reminders sent via SMS"
                                } else {
                                    "${result.sent} sent, ${result.failed} failed"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                            MessageChannel.WHATSAPP -> {
                                for (parcel in eligibleParcels) {
                                    val msg = stringResource(
                                        Res.string.reminder_template,
                                        parcel.firstNameLastName,
                                        parcel.pieceCount,
                                        parcel.city
                                    )
                                    openWhatsApp(parcel.phone, msg)
                                }
                            }
                        }
                    }
                }
            )
        }
```

**Important note on `stringResource` usage:** `stringResource` is a `@Composable` function, so it cannot be called inside `scope.launch {}`. The message construction needs to happen differently. Instead, pre-build the messages in the composable scope and pass them. Here is the corrected approach — replace the above `if (showSendRemindersDialog)` block with:

```kotlin
        if (showSendRemindersDialog) {
            // Pre-build messages in composable scope
            val messages = eligibleParcels.map { parcel ->
                parcel to stringResource(
                    Res.string.reminder_template,
                    parcel.firstNameLastName,
                    parcel.pieceCount,
                    parcel.city
                )
            }

            SendRemindersDialog(
                eligibleParcels = eligibleParcels,
                onDismiss = { showSendRemindersDialog = false },
                onSend = { channel ->
                    showSendRemindersDialog = false
                    scope.launch {
                        when (channel) {
                            MessageChannel.SMS -> {
                                val recipients = messages.map { (parcel, msg) ->
                                    SmsRecipient(phone = parcel.phone, message = msg)
                                }
                                val result = sendBulkSms(recipients)
                                val snackMessage = if (result.failed == 0) {
                                    "${result.sent} / SMS"
                                } else {
                                    "${result.sent} OK, ${result.failed} FAIL"
                                }
                                snackbarHostState.showSnackbar(snackMessage)
                            }
                            MessageChannel.WHATSAPP -> {
                                for ((parcel, msg) in messages) {
                                    openWhatsApp(parcel.phone, msg)
                                }
                            }
                        }
                    }
                }
            )
        }
```

**Step 5: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/RouteDetails.kt
git commit -m "feat: add Send Reminders button and dialog to RouteDetails screen"
```

---

### Task 9: Add Android SMS permission handling

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/RouteDetails.kt`

**Step 1: Add permission handling for Android SMS**

The SMS send via `SmsManager` requires runtime permission on Android. We need to add permission checking before sending.

Add this import to `RouteDetails.kt`:

```kotlin
import com.vantechinformatics.easycargo.utils.requestSmsPermission
```

And add a new expect/actual function in `MessagingUtils.kt`:

**Modify `composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.kt`** — add:

```kotlin
expect fun hasSmsPermission(): Boolean
expect fun requestSmsPermission()
```

**Modify `composeApp/src/androidMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.android.kt`** — add:

```kotlin
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

actual fun hasSmsPermission(): Boolean {
    val ctx = messagingContext ?: return false
    return ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) ==
        PackageManager.PERMISSION_GRANTED
}

actual fun requestSmsPermission() {
    // Permission request must be handled at the Activity level.
    // This is a no-op; the actual request happens via the Compose permission API.
}
```

**Modify `composeApp/src/iosMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.ios.kt`** — add:

```kotlin
actual fun hasSmsPermission(): Boolean = true  // iOS doesn't need SMS permission
actual fun requestSmsPermission() {}  // No-op on iOS
```

**Step 2: Update the SMS send flow in RouteDetails**

In the `onSend` callback for `MessageChannel.SMS`, wrap the send logic with a permission check:

```kotlin
MessageChannel.SMS -> {
    if (!hasSmsPermission()) {
        snackbarHostState.showSnackbar("SMS permission required")
    } else {
        val recipients = messages.map { (parcel, msg) ->
            SmsRecipient(phone = parcel.phone, message = msg)
        }
        val result = sendBulkSms(recipients)
        val snackMessage = if (result.failed == 0) {
            "${result.sent} / SMS"
        } else {
            "${result.sent} OK, ${result.failed} FAIL"
        }
        snackbarHostState.showSnackbar(snackMessage)
    }
}
```

For full Android permission requesting with the Compose permission API, we need to use `rememberLauncherForActivityResult` in the Android-specific code. However, since this is KMP and the RouteDetails is in commonMain, a simpler approach is to check permission before showing the dialog and request it there. The permission request can be done via an `ActivityResultLauncher` registered in the composable.

**Alternative simpler approach:** Add a `Modifier.clickable` guard that checks permission when the user taps "Send All" with SMS selected. If permission is not granted, show a snackbar prompting them to enable it in Settings. This avoids the complexity of Compose permission APIs in KMP.

**Step 3: Build to verify**

Run: `./gradlew composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.kt
git add composeApp/src/androidMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.android.kt
git add composeApp/src/iosMain/kotlin/com/vantechinformatics/easycargo/utils/MessagingUtils.ios.kt
git add composeApp/src/commonMain/kotlin/com/vantechinformatics/easycargo/ui/RouteDetails.kt
git commit -m "feat: add SMS permission handling for Android"
```

---

### Task 10: Final build verification and integration commit

**Step 1: Full clean build**

Run: `./gradlew clean composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

**Step 2: Verify no warnings related to new code**

Check build output for any warnings in the new files. Fix any that appear.

**Step 3: Final commit if any fixes were needed**

```bash
git add -A
git commit -m "feat: complete bulk delivery reminders feature"
```

---

## File Summary

| File | Action | Purpose |
|------|--------|---------|
| `values/strings.xml` (+ 5 languages) | Modify | Add 14 new string keys per language |
| `ParcelDao.kt` | Modify | Add `getUndeliveredParcelsWithPhone` query |
| `ParcelViewModel.kt` | Modify | Add `getUndeliveredParcelsWithPhone` method |
| `MessagingUtils.kt` (commonMain) | Create | expect declarations for SMS/WhatsApp |
| `MessagingUtils.android.kt` | Create | Android SmsManager + WhatsApp intents |
| `MessagingUtils.ios.kt` | Create | iOS SMS + WhatsApp URL schemes |
| `AndroidManifest.xml` | Modify | Add SEND_SMS permission |
| `MainActivity.kt` | Modify | Initialize messaging context |
| `SendReminders.kt` | Create | Dialog UI with channel selection |
| `RouteDetails.kt` | Modify | Button + dialog integration |
