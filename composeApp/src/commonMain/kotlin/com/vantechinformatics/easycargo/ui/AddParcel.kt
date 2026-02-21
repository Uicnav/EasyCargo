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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.format
import com.vantechinformatics.easycargo.utils.LocalDataStore
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.utils.CityAutocompleteField
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.btn_generate_ticket
import easycargo.composeapp.generated.resources.btn_save_changes
import easycargo.composeapp.generated.resources.detail_label_total_pay
import easycargo.composeapp.generated.resources.error_validation_fields
import easycargo.composeapp.generated.resources.label_full_name
import easycargo.composeapp.generated.resources.label_package_count
import easycargo.composeapp.generated.resources.label_phone
import easycargo.composeapp.generated.resources.label_price_per_kg
import easycargo.composeapp.generated.resources.label_weight
import easycargo.composeapp.generated.resources.title_add_parcel
import easycargo.composeapp.generated.resources.title_edit_parcel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// Triple(dialCode, iso2, countryName)
private val ALL_COUNTRY_CODES: List<Triple<String, String, String>> = listOf(
    Triple("+93", "AF", "Afghanistan"),
    Triple("+355", "AL", "Albania"),
    Triple("+213", "DZ", "Algeria"),
    Triple("+376", "AD", "Andorra"),
    Triple("+244", "AO", "Angola"),
    Triple("+1268", "AG", "Antigua and Barbuda"),
    Triple("+54", "AR", "Argentina"),
    Triple("+374", "AM", "Armenia"),
    Triple("+61", "AU", "Australia"),
    Triple("+43", "AT", "Austria"),
    Triple("+994", "AZ", "Azerbaijan"),
    Triple("+1242", "BS", "Bahamas"),
    Triple("+973", "BH", "Bahrain"),
    Triple("+880", "BD", "Bangladesh"),
    Triple("+1246", "BB", "Barbados"),
    Triple("+375", "BY", "Belarus"),
    Triple("+32", "BE", "Belgium"),
    Triple("+501", "BZ", "Belize"),
    Triple("+229", "BJ", "Benin"),
    Triple("+975", "BT", "Bhutan"),
    Triple("+591", "BO", "Bolivia"),
    Triple("+387", "BA", "Bosnia and Herzegovina"),
    Triple("+267", "BW", "Botswana"),
    Triple("+55", "BR", "Brazil"),
    Triple("+673", "BN", "Brunei"),
    Triple("+359", "BG", "Bulgaria"),
    Triple("+226", "BF", "Burkina Faso"),
    Triple("+257", "BI", "Burundi"),
    Triple("+855", "KH", "Cambodia"),
    Triple("+237", "CM", "Cameroon"),
    Triple("+1", "CA", "Canada"),
    Triple("+238", "CV", "Cape Verde"),
    Triple("+236", "CF", "Central African Republic"),
    Triple("+235", "TD", "Chad"),
    Triple("+56", "CL", "Chile"),
    Triple("+86", "CN", "China"),
    Triple("+57", "CO", "Colombia"),
    Triple("+269", "KM", "Comoros"),
    Triple("+242", "CG", "Congo"),
    Triple("+243", "CD", "Congo DR"),
    Triple("+506", "CR", "Costa Rica"),
    Triple("+225", "CI", "Ivory Coast"),
    Triple("+385", "HR", "Croatia"),
    Triple("+53", "CU", "Cuba"),
    Triple("+357", "CY", "Cyprus"),
    Triple("+420", "CZ", "Czech Republic"),
    Triple("+45", "DK", "Denmark"),
    Triple("+253", "DJ", "Djibouti"),
    Triple("+1767", "DM", "Dominica"),
    Triple("+1809", "DO", "Dominican Republic"),
    Triple("+593", "EC", "Ecuador"),
    Triple("+20", "EG", "Egypt"),
    Triple("+503", "SV", "El Salvador"),
    Triple("+240", "GQ", "Equatorial Guinea"),
    Triple("+291", "ER", "Eritrea"),
    Triple("+372", "EE", "Estonia"),
    Triple("+268", "SZ", "Eswatini"),
    Triple("+251", "ET", "Ethiopia"),
    Triple("+679", "FJ", "Fiji"),
    Triple("+358", "FI", "Finland"),
    Triple("+33", "FR", "France"),
    Triple("+241", "GA", "Gabon"),
    Triple("+220", "GM", "Gambia"),
    Triple("+995", "GE", "Georgia"),
    Triple("+49", "DE", "Germany"),
    Triple("+233", "GH", "Ghana"),
    Triple("+30", "GR", "Greece"),
    Triple("+1473", "GD", "Grenada"),
    Triple("+502", "GT", "Guatemala"),
    Triple("+224", "GN", "Guinea"),
    Triple("+245", "GW", "Guinea-Bissau"),
    Triple("+592", "GY", "Guyana"),
    Triple("+509", "HT", "Haiti"),
    Triple("+504", "HN", "Honduras"),
    Triple("+852", "HK", "Hong Kong"),
    Triple("+36", "HU", "Hungary"),
    Triple("+354", "IS", "Iceland"),
    Triple("+91", "IN", "India"),
    Triple("+62", "ID", "Indonesia"),
    Triple("+98", "IR", "Iran"),
    Triple("+964", "IQ", "Iraq"),
    Triple("+353", "IE", "Ireland"),
    Triple("+972", "IL", "Israel"),
    Triple("+39", "IT", "Italy"),
    Triple("+1876", "JM", "Jamaica"),
    Triple("+81", "JP", "Japan"),
    Triple("+962", "JO", "Jordan"),
    Triple("+7", "KZ", "Kazakhstan"),
    Triple("+254", "KE", "Kenya"),
    Triple("+686", "KI", "Kiribati"),
    Triple("+82", "KR", "South Korea"),
    Triple("+965", "KW", "Kuwait"),
    Triple("+996", "KG", "Kyrgyzstan"),
    Triple("+856", "LA", "Laos"),
    Triple("+371", "LV", "Latvia"),
    Triple("+961", "LB", "Lebanon"),
    Triple("+266", "LS", "Lesotho"),
    Triple("+231", "LR", "Liberia"),
    Triple("+218", "LY", "Libya"),
    Triple("+423", "LI", "Liechtenstein"),
    Triple("+370", "LT", "Lithuania"),
    Triple("+352", "LU", "Luxembourg"),
    Triple("+853", "MO", "Macau"),
    Triple("+261", "MG", "Madagascar"),
    Triple("+265", "MW", "Malawi"),
    Triple("+60", "MY", "Malaysia"),
    Triple("+960", "MV", "Maldives"),
    Triple("+223", "ML", "Mali"),
    Triple("+356", "MT", "Malta"),
    Triple("+692", "MH", "Marshall Islands"),
    Triple("+222", "MR", "Mauritania"),
    Triple("+230", "MU", "Mauritius"),
    Triple("+52", "MX", "Mexico"),
    Triple("+691", "FM", "Micronesia"),
    Triple("+373", "MD", "Moldova"),
    Triple("+377", "MC", "Monaco"),
    Triple("+976", "MN", "Mongolia"),
    Triple("+382", "ME", "Montenegro"),
    Triple("+212", "MA", "Morocco"),
    Triple("+258", "MZ", "Mozambique"),
    Triple("+95", "MM", "Myanmar"),
    Triple("+264", "NA", "Namibia"),
    Triple("+674", "NR", "Nauru"),
    Triple("+977", "NP", "Nepal"),
    Triple("+31", "NL", "Netherlands"),
    Triple("+64", "NZ", "New Zealand"),
    Triple("+505", "NI", "Nicaragua"),
    Triple("+227", "NE", "Niger"),
    Triple("+234", "NG", "Nigeria"),
    Triple("+389", "MK", "North Macedonia"),
    Triple("+47", "NO", "Norway"),
    Triple("+968", "OM", "Oman"),
    Triple("+92", "PK", "Pakistan"),
    Triple("+680", "PW", "Palau"),
    Triple("+970", "PS", "Palestine"),
    Triple("+507", "PA", "Panama"),
    Triple("+675", "PG", "Papua New Guinea"),
    Triple("+595", "PY", "Paraguay"),
    Triple("+51", "PE", "Peru"),
    Triple("+63", "PH", "Philippines"),
    Triple("+48", "PL", "Poland"),
    Triple("+351", "PT", "Portugal"),
    Triple("+974", "QA", "Qatar"),
    Triple("+40", "RO", "Romania"),
    Triple("+7", "RU", "Russia"),
    Triple("+250", "RW", "Rwanda"),
    Triple("+966", "SA", "Saudi Arabia"),
    Triple("+221", "SN", "Senegal"),
    Triple("+381", "RS", "Serbia"),
    Triple("+248", "SC", "Seychelles"),
    Triple("+232", "SL", "Sierra Leone"),
    Triple("+65", "SG", "Singapore"),
    Triple("+421", "SK", "Slovakia"),
    Triple("+386", "SI", "Slovenia"),
    Triple("+677", "SB", "Solomon Islands"),
    Triple("+252", "SO", "Somalia"),
    Triple("+27", "ZA", "South Africa"),
    Triple("+211", "SS", "South Sudan"),
    Triple("+34", "ES", "Spain"),
    Triple("+94", "LK", "Sri Lanka"),
    Triple("+249", "SD", "Sudan"),
    Triple("+597", "SR", "Suriname"),
    Triple("+46", "SE", "Sweden"),
    Triple("+41", "CH", "Switzerland"),
    Triple("+963", "SY", "Syria"),
    Triple("+886", "TW", "Taiwan"),
    Triple("+992", "TJ", "Tajikistan"),
    Triple("+255", "TZ", "Tanzania"),
    Triple("+66", "TH", "Thailand"),
    Triple("+670", "TL", "Timor-Leste"),
    Triple("+228", "TG", "Togo"),
    Triple("+676", "TO", "Tonga"),
    Triple("+1868", "TT", "Trinidad and Tobago"),
    Triple("+216", "TN", "Tunisia"),
    Triple("+90", "TR", "Turkey"),
    Triple("+993", "TM", "Turkmenistan"),
    Triple("+688", "TV", "Tuvalu"),
    Triple("+256", "UG", "Uganda"),
    Triple("+380", "UA", "Ukraine"),
    Triple("+971", "AE", "United Arab Emirates"),
    Triple("+44", "GB", "United Kingdom"),
    Triple("+1", "US", "United States"),
    Triple("+598", "UY", "Uruguay"),
    Triple("+998", "UZ", "Uzbekistan"),
    Triple("+678", "VU", "Vanuatu"),
    Triple("+58", "VE", "Venezuela"),
    Triple("+84", "VN", "Vietnam"),
    Triple("+967", "YE", "Yemen"),
    Triple("+260", "ZM", "Zambia"),
    Triple("+263", "ZW", "Zimbabwe")
)

@Composable
fun AddParcelDialog(
    routeId: Long,
    viewModel: ParcelViewModel,
    onDismiss: () -> Unit,
    onParcelAdded: (ParcelUi) -> Unit,
    parcelToEdit: ParcelUi? = null
) {
    val isEditMode = parcelToEdit != null
    val colors = EasyCargoTheme.colors
    val scope = rememberCoroutineScope()
    val dataStore = LocalDataStore.current
    val pricePerKgKey = remember { doublePreferencesKey("price_per_kg") }
    val savedPricePerKg by dataStore.data.map { prefs ->
        prefs[pricePerKgKey] ?: 1.5
    }.collectAsState(initial = 1.5)

    // Variabile de stare (Input-uri)
    var firstNameLastName by remember { mutableStateOf(parcelToEdit?.firstNameLastName ?: "") }
    var phone by remember { mutableStateOf(parcelToEdit?.phone ?: "") }

    val countryCodeKey = remember { stringPreferencesKey("country_code") }
    val savedCountryCode by dataStore.data.map { prefs ->
        prefs[countryCodeKey] ?: "+39"
    }.collectAsState(initial = "+39")

    val lastCityKey = remember { stringPreferencesKey("last_city") }
    val savedCity by dataStore.data.map { prefs ->
        prefs[lastCityKey] ?: ""
    }.collectAsState(initial = "")

    var selectedCountryCode by remember(savedCountryCode) {
        val existingPhone = parcelToEdit?.phone ?: ""
        val matchedCode = ALL_COUNTRY_CODES.map { it.first }
            .sortedByDescending { it.length }
            .firstOrNull { existingPhone.startsWith(it) }
        mutableStateOf(matchedCode ?: savedCountryCode)
    }

    var localPhone by remember {
        val existingPhone = parcelToEdit?.phone ?: ""
        val matchedCode = ALL_COUNTRY_CODES.map { it.first }
            .sortedByDescending { it.length }
            .firstOrNull { existingPhone.startsWith(it) }
        mutableStateOf(if (matchedCode != null) existingPhone.removePrefix(matchedCode) else existingPhone)
    }

    var showCountryCodePicker by remember { mutableStateOf(false) }

    var city by remember(savedCity) { mutableStateOf(parcelToEdit?.city ?: savedCity) }

    // Valori numerice
    var weightInput by remember { mutableStateOf(parcelToEdit?.weight?.toString() ?: "") }
    var pricePerKgInput by remember(savedPricePerKg) {
        mutableStateOf(parcelToEdit?.pricePerKg?.toString() ?: savedPricePerKg.toString())
    }
    var piecesInput by remember { mutableStateOf(parcelToEdit?.pieceCount?.toString() ?: "1") }

    val calculatedTotal = remember<Double>(weightInput, pricePerKgInput) {
        val w = weightInput.toDoubleOrNull() ?: 0.0
        val p = pricePerKgInput.toDoubleOrNull() ?: 0.0
        w * p
    }

    // Validare
    var isNameError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isCityError by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = colors.glassBorder,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = colors.textSecondary,
        focusedTextColor = colors.contentPrimary,
        unfocusedTextColor = colors.contentPrimary,
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
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .background(colors.glassSurface)
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
                        stringResource(if (isEditMode) Res.string.title_edit_parcel else Res.string.title_add_parcel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = colors.contentPrimary)
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

                // 2. Phone with country code (unified field)
                val countryIso = ALL_COUNTRY_CODES.firstOrNull { it.first == selectedCountryCode }?.second ?: "IT"
                OutlinedTextField(
                    value = localPhone,
                    onValueChange = {
                        localPhone = it.replace(Regex("[^\\d]"), "")
                        if (isPhoneError && it.isNotBlank()) isPhoneError = false
                    },
                    label = { Text(stringResource(Res.string.label_phone)) },
                    singleLine = true,
                    isError = isPhoneError,
                    prefix = {
                        Row(
                            modifier = Modifier
                                .clickable { showCountryCodePicker = true }
                                .padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(flagEmoji(countryIso), style = MaterialTheme.typography.titleMedium)
                            Text(
                                selectedCountryCode,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.contentPrimary
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = colors.textSecondary
                            )
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(colors.glassBorder)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { cityFocus.requestFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(phoneFocus),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        if (isPhoneError) {
                            Text(
                                text = stringResource(Res.string.error_validation_fields),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
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
                HorizontalDivider(color = colors.glassBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // 4. Kg + Price row
                Row {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text(stringResource(Res.string.label_weight)) },
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
                        label = { Text(stringResource(Res.string.label_price_per_kg)) },
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
                    label = { Text(stringResource(Res.string.label_package_count)) },
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

                // Total card
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.glassBorder, RoundedCornerShape(12.dp))
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
                            color = colors.textSecondary
                        )
                        Text(
                            text = "${calculatedTotal.format(2)} €",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.greenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        val nameValid = firstNameLastName.isNotBlank()
                        val phoneValid = localPhone.isNotBlank()
                        val cityValid = city.isNotBlank()
                        isNameError = !nameValid
                        isPhoneError = !phoneValid
                        isCityError = !cityValid
                        if (nameValid && phoneValid && cityValid) {
                            scope.launch {
                                val priceKg = pricePerKgInput.toDoubleOrNull() ?: 0.0
                                val cleanLocal = localPhone.trimStart('0').replace(Regex("[^\\d]"), "")
                                val combinedPhone = if (cleanLocal.isNotBlank()) "$selectedCountryCode$cleanLocal" else ""
                                if (isEditMode) {
                                    val updated = parcelToEdit!!.copy(
                                        firstNameLastName = firstNameLastName,
                                        phone = combinedPhone,
                                        city = city,
                                        weight = weightInput.toDoubleOrNull() ?: 0.0,
                                        pricePerKg = priceKg,
                                        pieceCount = piecesInput.toIntOrNull() ?: 1
                                    )
                                    viewModel.updateParcel(updated)
                                    dataStore.edit { prefs ->
                                        prefs[pricePerKgKey] = priceKg
                                        prefs[countryCodeKey] = selectedCountryCode
                                        prefs[lastCityKey] = city
                                    }
                                    onDismiss()
                                } else {
                                    val parcel = viewModel.addParcel(
                                        id = routeId,
                                        firstNameLastName = firstNameLastName,
                                        phone = combinedPhone,
                                        weight = weightInput.toDoubleOrNull() ?: 0.0,
                                        priceKg = priceKg,
                                        pieces = piecesInput.toIntOrNull() ?: 1,
                                        city = city
                                    )
                                    dataStore.edit { prefs ->
                                        prefs[pricePerKgKey] = priceKg
                                        prefs[countryCodeKey] = selectedCountryCode
                                        prefs[lastCityKey] = city
                                    }
                                    onParcelAdded(parcel.apply { showOnlyInfo = true })
                                    onDismiss()
                                }
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
                        text = stringResource(if (isEditMode) Res.string.btn_save_changes else Res.string.btn_generate_ticket),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Country code picker dialog
    if (showCountryCodePicker) {
        CountryCodePickerDialog(
            onSelect = { code ->
                selectedCountryCode = code
                showCountryCodePicker = false
            },
            onDismiss = { showCountryCodePicker = false }
        )
    }
}

@Composable
private fun CountryCodePickerDialog(
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = EasyCargoTheme.colors
    var search by remember { mutableStateOf("") }
    val filtered by remember {
        derivedStateOf {
            if (search.isBlank()) ALL_COUNTRY_CODES
            else {
                val q = search.lowercase()
                ALL_COUNTRY_CODES.filter { (code, iso, name) ->
                    name.lowercase().contains(q) || code.contains(q) || iso.lowercase().contains(q)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .background(colors.glassSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Country Code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = colors.contentPrimary)
                    }
                }

                // Search field
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    singleLine = true,
                    placeholder = { Text("Search...", color = colors.textMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = colors.glassBorder,
                        focusedTextColor = colors.contentPrimary,
                        unfocusedTextColor = colors.contentPrimary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // List
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false).heightIn(max = 400.dp)
                ) {
                    items(filtered, key = { "${it.first}_${it.second}" }) { (code, iso, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(code) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(flagEmoji(iso), style = MaterialTheme.typography.titleMedium)
                            Text(
                                name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.contentPrimary,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                code,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                        HorizontalDivider(color = colors.glassBorder.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

private fun flagEmoji(iso: String): String = buildString {
    for (c in iso.uppercase()) {
        val codePoint = 0x1F1E6 + (c - 'A')
        val high = ((codePoint - 0x10000) shr 10) + 0xD800
        val low = ((codePoint - 0x10000) and 0x3FF) + 0xDC00
        append(high.toChar())
        append(low.toChar())
    }
}
