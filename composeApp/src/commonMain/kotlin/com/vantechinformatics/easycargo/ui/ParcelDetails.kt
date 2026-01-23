package com.vantechinformatics.easycargo.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vantechinformatics.easycargo.data.ParcelEntity
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_close
import easycargo.composeapp.generated.resources.btn_mark_delivered
import easycargo.composeapp.generated.resources.btn_mark_undelivered
import easycargo.composeapp.generated.resources.detail_label_name
import easycargo.composeapp.generated.resources.detail_label_phone
import easycargo.composeapp.generated.resources.detail_label_pieces
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
    val scope = rememberCoroutineScope()

    // Putem schimba statusul direct de aici
    val isDelivered = remember { mutableStateOf(parcel.isDelivered) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Header cu ID
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ParcelIdDisplay(displayId = parcel.displayId)
                    Spacer(Modifier.weight(1f))
                    if (isDelivered.value) {
                        Text(
                            stringResource(Res.string.label_status_delivered),
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            stringResource(Res.string.label_status_pending),
                            color = Color(0xFFFFC107),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Informații Detaliate
                DetailRow(stringResource(Res.string.detail_label_name), parcel.firstNameLastName)
                DetailRow(stringResource(Res.string.detail_label_phone), parcel.phone)
                Divider(Modifier.padding(vertical = 8.dp))
                DetailRow(
                    stringResource(Res.string.detail_label_weight),
                    parcel.weight.toString() + " " + stringResource(Res.string.format_kg)
                )
                DetailRow(stringResource(Res.string.detail_label_pieces), "${parcel.pieceCount}")
                DetailRow(
                    stringResource(Res.string.label_price_per_kg),
                    parcel.pricePerKg.toString() + " " + stringResource(Res.string.format_euro)
                )

                Spacer(Modifier.height(16.dp))

                // Total Mare
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        parcel.totalSum.toString() + " " + stringResource(Res.string.format_euro),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Buton Livrare
                if (!parcel.showOnlyInfo) {
                    Button(
                        onClick = {
                            scope.launch {
                                val newStatus = !isDelivered.value
                                viewModel.updateParcelStatus(parcel.id, newStatus)
                                isDelivered.value = newStatus // Actualizăm UI local
                                // Nu închidem dialogul ca să vadă utilizatorul că s-a schimbat
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            contentColor = if (isDelivered.value) Color.Gray else Color(0xFF4CAF50)
                        ), modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(
                            text = if (isDelivered.value) stringResource(Res.string.btn_mark_undelivered)
                            else stringResource(Res.string.btn_mark_delivered)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(Res.string.action_close))
                }
            }
        }
    }
}

@Composable
fun ParcelIdDisplay(displayId: Int) {
    // 1. Logica Matematică
    val routePart = displayId / 1000
    val parcelPart = displayId % 1000

    // Formatăm coletul să aibă mereu 3 cifre (ex: 5 -> 005)
    // Nota: padStart e standard Kotlin
    val parcelString = parcelPart.toString().padStart(3, '0')

    Text(
        text = buildAnnotatedString {
            // Partea de Rută (Bold și Colorat)
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF1565C0), // Albastru închis
                    fontSize = 18.sp
                )
            ) {
                append("R$routePart")
            }

            // Separator
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("-")
            }

            // Partea de Colet (Mai mare, Negru)
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 22.sp
                )
            ) {
                append(parcelString)
            }
        })
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, modifier = Modifier.width(100.dp))
        Text(value, fontWeight = FontWeight.Medium)
    }
}