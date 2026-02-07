package com.vantechinformatics.easycargo.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vantechinformatics.easycargo.data.HttpClientEngineFactory
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.city_label
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

val httpClient = HttpClient(HttpClientEngineFactory().getHttpEngine())
suspend fun searchCityOnline(query: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            // 1. Facem cererea la API
            val response: String = httpClient.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "5")
                // Nominatim cere User-Agent
                header("User-Agent", "EasyCargoApp/1.0")
            }.bodyAsText()

            // 2. Parsăm Răspunsul manual (fără org.json)
            // Căutăm tiparul: "display_name":"Ceva, Ceva"
            val results = mutableListOf<String>()

            // Regex simplu care extrage numele orașelor dintre ghilimele după display_name
            val regex = "\"display_name\":\"(.*?)\"".toRegex()

            regex.findAll(response).forEach { matchResult ->
                // matchResult.groupValues[1] este textul din interiorul ghilimelelor
                // Curățăm caracterele escape unicode dacă există (basic)
                val cleanName = matchResult.groupValues[1].replace("\\\"", "\"")
                results.add(cleanName)
            }

            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

// --- PARTEA 2: COMPONENTA VIZUALĂ (UI - Rămâne neschimbată) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityAutocompleteField(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    isError: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onNext: (() -> Unit)? = null
) {
    val colors = EasyCargoTheme.colors
    var query by remember { mutableStateOf(selectedCity) }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Actualizăm query-ul dacă vine o valoare din afară (editare)
    LaunchedEffect(selectedCity) {
        if (query != selectedCity) {
            query = selectedCity
        }
    }

    Box(modifier = Modifier.fillMaxWidth().zIndex(1f)) {
        Column {
            OutlinedTextField(
                value = query, onValueChange = { newText ->
                query = newText
                onCitySelected(newText)

                if (newText.length > 2) {
                    scope.launch {
                        isLoading = true
                        delay(800) // Debounce
                        if (newText == query) {
                            val results = searchCityOnline(newText)
                            suggestions = results
                            expanded = results.isNotEmpty()
                            isLoading = false
                        }
                    }
                } else {
                    expanded = false
                    isLoading = false
                }
            }, label = { Text(stringResource(Res.string.city_label)) }, leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = colors.textSecondary)
                }
            }, trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        query = ""
                        onCitySelected("")
                        expanded = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Șterge", tint = colors.textSecondary)
                    }
                }
            },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                singleLine = true,
                isError = isError,
                keyboardOptions = KeyboardOptions(imeAction = if (onNext != null) ImeAction.Next else ImeAction.Default),
                keyboardActions = KeyboardActions(onNext = { onNext?.invoke() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = colors.glassBorder,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = colors.textSecondary,
                    focusedTextColor = colors.contentPrimary,
                    unfocusedTextColor = colors.contentPrimary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (expanded) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(top = 4.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.glassSurface)
                ) {
                    LazyColumn {
                        items(suggestions) { city ->
                            ListItem(
                                headlineContent = {
                                    Text(city, color = colors.contentPrimary)
                                },
                                modifier = Modifier.clickable {
                                        query = city
                                        onCitySelected(city)
                                        expanded = false
                                        focusManager.clearFocus()
                                    }.background(colors.glassSurface))
                            HorizontalDivider(color = colors.glassBorder)
                        }
                    }
                }
            }
        }
    }
}
