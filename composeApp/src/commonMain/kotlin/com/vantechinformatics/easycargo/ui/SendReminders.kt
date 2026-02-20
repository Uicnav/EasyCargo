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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import com.vantechinformatics.easycargo.utils.isWhatsAppInstalled
import com.vantechinformatics.easycargo.utils.isViberInstalled
import com.vantechinformatics.easycargo.utils.openWhatsApp
import com.vantechinformatics.easycargo.utils.openViber
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_close
import easycargo.composeapp.generated.resources.label_channel_viber
import easycargo.composeapp.generated.resources.label_channel_whatsapp
import easycargo.composeapp.generated.resources.label_choose_channel
import easycargo.composeapp.generated.resources.msg_reminder_count
import easycargo.composeapp.generated.resources.msg_viber_not_installed
import easycargo.composeapp.generated.resources.msg_whatsapp_not_installed
import easycargo.composeapp.generated.resources.reminder_template
import easycargo.composeapp.generated.resources.title_send_reminders
import org.jetbrains.compose.resources.stringResource

enum class MessageChannel { WHATSAPP, VIBER }

@Composable
fun SendRemindersDialog(
    eligibleParcels: List<ParcelUi>,
    onDismiss: () -> Unit
) {
    val colors = EasyCargoTheme.colors
    var selectedChannel by remember { mutableStateOf(MessageChannel.WHATSAPP) }
    val whatsAppInstalled = remember { isWhatsAppInstalled() }
    val viberInstalled = remember { isViberInstalled() }
    val sentIds = remember { mutableStateListOf<Long>() }

    if (!whatsAppInstalled && viberInstalled) {
        selectedChannel = MessageChannel.VIBER
    }

    // Pre-build messages for each parcel
    val parcelMessages = eligibleParcels.map { parcel ->
        parcel to stringResource(
            Res.string.reminder_template,
            parcel.firstNameLastName,
            parcel.pieceCount,
            parcel.city
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .background(colors.glassSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.title_send_reminders),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = colors.contentPrimary)
                    }
                }

                // Count info
                Text(
                    text = stringResource(Res.string.msg_reminder_count, eligibleParcels.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.glassBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Channel selector
                Text(
                    text = stringResource(Res.string.label_choose_channel),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChannelChip(
                        label = stringResource(Res.string.label_channel_whatsapp),
                        isSelected = selectedChannel == MessageChannel.WHATSAPP,
                        enabled = whatsAppInstalled,
                        onClick = { if (whatsAppInstalled) selectedChannel = MessageChannel.WHATSAPP },
                        modifier = Modifier.weight(1f)
                    )
                    ChannelChip(
                        label = stringResource(Res.string.label_channel_viber),
                        isSelected = selectedChannel == MessageChannel.VIBER,
                        enabled = viberInstalled,
                        onClick = { if (viberInstalled) selectedChannel = MessageChannel.VIBER },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (!whatsAppInstalled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.msg_whatsapp_not_installed),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        fontStyle = FontStyle.Italic
                    )
                }
                if (!viberInstalled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.msg_viber_not_installed),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.glassBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Recipient list — each row has a send button
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parcelMessages, key = { it.first.id }) { (parcel, message) ->
                        val isSent = parcel.id in sentIds
                        RecipientRow(
                            name = parcel.firstNameLastName,
                            phone = parcel.phone,
                            city = parcel.city,
                            isSent = isSent,
                            enabled = (selectedChannel == MessageChannel.WHATSAPP && whatsAppInstalled) ||
                                      (selectedChannel == MessageChannel.VIBER && viberInstalled),
                            onSend = {
                                when (selectedChannel) {
                                    MessageChannel.WHATSAPP -> openWhatsApp(parcel.phone, message)
                                    MessageChannel.VIBER -> openViber(parcel.phone, message)
                                }
                                sentIds.add(parcel.id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.glassCard,
                        contentColor = colors.contentPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.action_close),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun RecipientRow(
    name: String,
    phone: String,
    city: String,
    isSent: Boolean,
    enabled: Boolean,
    onSend: () -> Unit
) {
    val colors = EasyCargoTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isSent) colors.greenLight.copy(alpha = 0.4f) else colors.glassBorder,
                RoundedCornerShape(12.dp)
            )
            .background(if (isSent) colors.greenLight.copy(alpha = 0.05f) else colors.glassCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.contentPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$phone  •  $city",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send / Sent indicator
            if (isSent) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = colors.greenLight,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                IconButton(
                    onClick = onSend,
                    enabled = enabled
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary else colors.textMuted,
                        modifier = Modifier.size(24.dp)
                    )
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
    val backgroundColor = when {
        !enabled -> colors.glassCard.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else -> colors.glassCard
    }
    val borderColor = when {
        !enabled -> colors.glassBorder.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> colors.glassBorder
    }
    val textColor = when {
        !enabled -> colors.textMuted.copy(alpha = 0.5f)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> colors.contentPrimary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
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
