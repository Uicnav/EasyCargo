package com.vantechinformatics.easycargo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.btn_mark_delivered
import easycargo.composeapp.generated.resources.btn_mark_undelivered
import easycargo.composeapp.generated.resources.city
import easycargo.composeapp.generated.resources.detail_label_name
import easycargo.composeapp.generated.resources.detail_label_phone
import easycargo.composeapp.generated.resources.detail_label_pieces
import easycargo.composeapp.generated.resources.detail_label_total_pay
import easycargo.composeapp.generated.resources.detail_label_weight
import easycargo.composeapp.generated.resources.format_euro
import easycargo.composeapp.generated.resources.format_kg
import easycargo.composeapp.generated.resources.label_price_per_kg
import easycargo.composeapp.generated.resources.label_status_delivered
import easycargo.composeapp.generated.resources.label_status_pending
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParcelDetailsDialog(
    parcel: ParcelUi, viewModel: ParcelViewModel, onDismiss: () -> Unit
) {
    val colors = EasyCargoTheme.colors
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    val isDelivered = remember { mutableStateOf(parcel.isDelivered) }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.glassSurface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                // Glass header area with parcel ID + close button
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ParcelIdDisplay(displayId = parcel.displayId)
                    Spacer(Modifier.weight(1f))
                    // Status chip
                    val statusColor = if (isDelivered.value) colors.greenLight else colors.orangeLight
                    val statusText =
                        if (isDelivered.value) stringResource(Res.string.label_status_delivered)
                        else stringResource(Res.string.label_status_pending)
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.background(
                                statusColor.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            ).padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = colors.textSecondary)
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    // Customer info
                    DetailRow(
                        stringResource(Res.string.detail_label_name),
                        parcel.firstNameLastName
                    )

                    // Phone with call action
                    if (parcel.phone.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .clickable { uriHandler.openUri("tel:${parcel.phone}") },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.detail_label_phone),
                                color = colors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = parcel.phone,
                                fontWeight = FontWeight.Medium,
                                color = colors.orangeLight,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = colors.orangeLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // City with navigation
                    if (parcel.city.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.city),
                                color = colors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = parcel.city,
                                fontWeight = FontWeight.Medium,
                                color = colors.contentPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val mapUrl = "http://maps.google.com/maps?daddr=${parcel.city}"
                                    uriHandler.openUri(mapUrl)
                                }, modifier = Modifier.height(32.dp).width(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = colors.orangeLight
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp), color = colors.glassBorder
                    )

                    // Logistics details
                    DetailRow(
                        stringResource(Res.string.detail_label_weight),
                        parcel.weight.toString() + " " + stringResource(Res.string.format_kg)
                    )
                    DetailRow(
                        stringResource(Res.string.detail_label_pieces),
                        "${parcel.pieceCount}"
                    )
                    DetailRow(
                        stringResource(Res.string.label_price_per_kg),
                        parcel.pricePerKg.toString() + " " + stringResource(Res.string.format_euro)
                    )

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(Res.string.detail_label_total_pay),
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(100.dp)
                        )
                        val totalToPay = parcel.weight * parcel.pricePerKg
                        Text(
                            text = "$totalToPay ${stringResource(Res.string.format_euro)}",
                            fontWeight = FontWeight.Bold,
                            color = colors.greenLight
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Action buttons
                    if (!parcel.showOnlyInfo) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val newStatus = !isDelivered.value
                                    viewModel.updateParcelStatus(
                                        parcel.id, newStatus, isVisible = parcel.isVisible
                                    )
                                    isDelivered.value = newStatus
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDelivered.value) colors.textMuted
                                else colors.greenLight, contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isDelivered.value) stringResource(Res.string.btn_mark_undelivered)
                                else stringResource(Res.string.btn_mark_delivered),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParcelIdDisplay(displayId: Int, isDelivered: Boolean = false) {
    val colors = EasyCargoTheme.colors
    val routePart = displayId / 1000
    val parcelPart = displayId % 1000
    val parcelString = parcelPart.toString().padStart(3, '0')

    val prefixColor = if (isDelivered) colors.textMuted else colors.orangeLight
    val numberColor = if (isDelivered) colors.textMuted else colors.contentPrimary

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold, color = prefixColor, fontSize = 16.sp
                )
            ) {
                append("R$routePart")
            }
            withStyle(style = SpanStyle(color = colors.textMuted, fontSize = 14.sp)) {
                append("-")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold, color = numberColor, fontSize = 18.sp
                )
            ) {
                append(parcelString)
            }
        })
}

@Composable
fun DetailRow(label: String, value: String) {
    val colors = EasyCargoTheme.colors
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            label,
            color = colors.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )
        Text(
            value,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.contentPrimary
        )
    }
}
