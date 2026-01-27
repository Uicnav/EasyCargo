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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.format
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.utils.CityAutocompleteField // Asigură-te că importul e corect
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.btn_generate_ticket
import easycargo.composeapp.generated.resources.detail_label_total_pay
import easycargo.composeapp.generated.resources.error_validation_fields
import easycargo.composeapp.generated.resources.label_full_name
import easycargo.composeapp.generated.resources.label_phone
import easycargo.composeapp.generated.resources.title_add_parcel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddParcelDialog(
    routeId: Long,
    viewModel: ParcelViewModel,
    onDismiss: () -> Unit,
    onParcelAdded: (ParcelUi) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Variabile de stare (Input-uri)
    var firstNameLastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") } // <--- NOU: Variabila pentru Oraș

    // Valori numerice
    var weightInput by remember { mutableStateOf("") }
    var pricePerKgInput by remember { mutableStateOf("1.5") } // Default 1.5€/kg
    var piecesInput by remember { mutableStateOf("1") }

    // --- LOGICA DE CALCUL AUTOMAT ---
    // Calculăm suma doar pentru afișare (read-only)
    val calculatedTotal = remember<Double>(weightInput, pricePerKgInput) {
        val w = weightInput.toDoubleOrNull() ?: 0.0
        val p = pricePerKgInput.toDoubleOrNull() ?: 0.0
        w * p
    }

    // Validare
    var isNameError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(Res.string.title_add_parcel),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Nume
                OutlinedTextField(
                    value = firstNameLastName,
                    onValueChange = {
                        firstNameLastName = it
                        if (isNameError && it.isNotBlank()) isNameError = false
                    },
                    label = { Text(stringResource(Res.string.label_full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text(
                                text = stringResource(Res.string.error_validation_fields),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // 2. Telefon
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(Res.string.label_phone)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Localitate (Autocomplete Offline/Online)
                CityAutocompleteField(
                    selectedCity = city,
                    onCitySelected = { newCity -> city = newCity }
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // 4. Kg și Preț (Pe același rând)
                Row {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = pricePerKgInput,
                        onValueChange = { pricePerKgInput = it },
                        label = { Text("Preț/Kg (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // 5. Nr Bucăți
                OutlinedTextField(
                    value = piecesInput,
                    onValueChange = { piecesInput = it },
                    label = { Text("Nr. Pachete (Bucăți)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- AFISARE SUMĂ CALCULATĂ ---
                Card {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(Res.string.detail_label_total_pay),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${calculatedTotal.format(2)} €",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1976D2), // Albastru
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buton Salvare
                Button(
                    onClick = {
                        if (firstNameLastName.isNotBlank()) {
                            isNameError = false
                            scope.launch {
                                // Apelăm ViewModel-ul cu noul parametru CITY
                                val parcel = viewModel.addParcel(
                                    id = routeId,
                                    firstNameLastName = firstNameLastName,
                                    phone = phone,
                                    weight = weightInput.toDoubleOrNull() ?: 0.0,
                                    priceKg = pricePerKgInput.toDoubleOrNull() ?: 0.0,
                                    pieces = piecesInput.toIntOrNull() ?: 1,
                                    city = city // <--- Trimitem orașul selectat
                                )
                                onParcelAdded(parcel.apply { showOnlyInfo = true })
                                onDismiss()
                            }
                        } else {
                            isNameError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = stringResource(Res.string.btn_generate_ticket))
                }
            }
        }
    }
}