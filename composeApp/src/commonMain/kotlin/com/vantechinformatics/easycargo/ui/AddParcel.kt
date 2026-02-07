package com.vantechinformatics.easycargo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.format
import com.vantechinformatics.easycargo.utils.LocalDataStore
import com.vantechinformatics.easycargo.ui.theme.GlassBorder
import com.vantechinformatics.easycargo.ui.theme.GlassSurface
import com.vantechinformatics.easycargo.ui.theme.GreenLight
import com.vantechinformatics.easycargo.ui.theme.TextSecondary
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.utils.CityAutocompleteField
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.btn_generate_ticket
import easycargo.composeapp.generated.resources.detail_label_total_pay
import easycargo.composeapp.generated.resources.error_validation_fields
import easycargo.composeapp.generated.resources.label_full_name
import easycargo.composeapp.generated.resources.label_phone
import easycargo.composeapp.generated.resources.title_add_parcel
import kotlinx.coroutines.flow.map
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
    val dataStore = LocalDataStore.current
    val pricePerKgKey = remember { doublePreferencesKey("price_per_kg") }
    val savedPricePerKg by dataStore.data.map { prefs ->
        prefs[pricePerKgKey] ?: 1.5
    }.collectAsState(initial = 1.5)

    // Variabile de stare (Input-uri)
    var firstNameLastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    // Valori numerice
    var weightInput by remember { mutableStateOf("") }
    var pricePerKgInput by remember(savedPricePerKg) { mutableStateOf(savedPricePerKg.toString()) }
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
    var isCityError by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = GlassBorder,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = TextSecondary,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    val focusManager = LocalFocusManager.current
    val phoneFocus = remember { FocusRequester() }
    val cityFocus = remember { FocusRequester() }
    val weightFocus = remember { FocusRequester() }
    val priceFocus = remember { FocusRequester() }
    val piecesFocus = remember { FocusRequester() }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .background(GlassSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.title_add_parcel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Name
                OutlinedTextField(
                    value = firstNameLastName,
                    onValueChange = {
                        firstNameLastName = it
                        if (isNameError && it.isNotBlank()) isNameError = false
                    },
                    label = { Text(stringResource(Res.string.label_full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isNameError,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { phoneFocus.requestFocus() }),
                    supportingText = {
                        if (isNameError) {
                            Text(
                                text = stringResource(Res.string.error_validation_fields),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // 2. Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(Res.string.label_phone)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { cityFocus.requestFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(phoneFocus),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. City autocomplete (mandatory)
                CityAutocompleteField(
                    selectedCity = city,
                    onCitySelected = { newCity ->
                        city = newCity
                        if (isCityError && newCity.isNotBlank()) isCityError = false
                    },
                    isError = isCityError,
                    focusRequester = cityFocus,
                    onNext = { weightFocus.requestFocus() }
                )
                if (isCityError) {
                    Text(
                        text = stringResource(Res.string.error_validation_fields),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = GlassBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // 4. Kg + Price row
                Row {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Kg") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { priceFocus.requestFocus() }),
                        modifier = Modifier.weight(1f).focusRequester(weightFocus),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = pricePerKgInput,
                        onValueChange = { pricePerKgInput = it },
                        label = { Text("Preț/Kg (€)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { piecesFocus.requestFocus() }),
                        modifier = Modifier.weight(1f).focusRequester(priceFocus),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 5. Pieces
                OutlinedTextField(
                    value = piecesInput,
                    onValueChange = { piecesInput = it },
                    label = { Text("Nr. Pachete (Bucăți)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(piecesFocus),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Total card - glass tinted
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(Res.string.detail_label_total_pay),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${calculatedTotal.format(2)} €",
                            style = MaterialTheme.typography.titleMedium,
                            color = GreenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save button - orange
                Button(
                    onClick = {
                        val nameValid = firstNameLastName.isNotBlank()
                        val cityValid = city.isNotBlank()
                        isNameError = !nameValid
                        isCityError = !cityValid
                        if (nameValid && cityValid) {
                            scope.launch {
                                val priceKg = pricePerKgInput.toDoubleOrNull() ?: 0.0
                                val parcel = viewModel.addParcel(
                                    id = routeId,
                                    firstNameLastName = firstNameLastName,
                                    phone = phone,
                                    weight = weightInput.toDoubleOrNull() ?: 0.0,
                                    priceKg = priceKg,
                                    pieces = piecesInput.toIntOrNull() ?: 1,
                                    city = city
                                )
                                dataStore.edit { prefs -> prefs[pricePerKgKey] = priceKg }
                                onParcelAdded(parcel.apply { showOnlyInfo = true })
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.btn_generate_ticket),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}