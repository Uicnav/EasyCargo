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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.vantechinformatics.easycargo.utils.isWhatsAppInstalled
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
    val whatsAppInstalled = remember { isWhatsAppInstalled() }

    // Preview message using first parcel's data
    val firstParcel = eligibleParcels.firstOrNull()
    val previewMessage = if (firstParcel != null) {
        stringResource(
            Res.string.reminder_template,
            firstParcel.firstNameLastName,
            firstParcel.pieceCount,
            firstParcel.city
        )
    } else {
        ""
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .background(colors.glassSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title row with close button
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
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = colors.contentPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Info: count of eligible parcels
                Text(
                    text = stringResource(Res.string.msg_reminder_count, eligibleParcels.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colors.glassBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Channel selector label
                Text(
                    text = stringResource(Res.string.label_choose_channel),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Channel chips row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChannelChip(
                        label = stringResource(Res.string.label_channel_sms),
                        isSelected = selectedChannel == MessageChannel.SMS,
                        enabled = true,
                        onClick = { selectedChannel = MessageChannel.SMS },
                        modifier = Modifier.weight(1f)
                    )
                    ChannelChip(
                        label = stringResource(Res.string.label_channel_whatsapp),
                        isSelected = selectedChannel == MessageChannel.WHATSAPP,
                        enabled = whatsAppInstalled,
                        onClick = {
                            if (whatsAppInstalled) {
                                selectedChannel = MessageChannel.WHATSAPP
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // WhatsApp not installed warning
                if (!whatsAppInstalled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.msg_whatsapp_not_installed),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colors.glassBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Message preview label
                Text(
                    text = stringResource(Res.string.label_message_preview),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message preview card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.glassBorder, RoundedCornerShape(12.dp))
                        .background(colors.glassCard)
                ) {
                    Text(
                        text = previewMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.contentPrimary,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.action_cancel),
                            color = colors.textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Send All button
                    Button(
                        onClick = { onSend(selectedChannel) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.btn_send_all),
                            fontWeight = FontWeight.Bold
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
