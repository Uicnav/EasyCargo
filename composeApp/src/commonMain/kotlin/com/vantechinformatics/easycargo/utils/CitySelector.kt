package com.vantechinformatics.easycargo.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- PARTEA 1: LOGICA DE REȚEA (Ktor + Regex Simplu) ---

// Inițializăm clientul de internet o singură dată
val httpClient = HttpClient(CIO)

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
    onCitySelected: (String) -> Unit
) {
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
                value = query,
                onValueChange = { newText ->
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
                },
                label = { Text("Localitate / Oraș") },
                leadingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            onCitySelected("")
                            expanded = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Șterge")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (expanded) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(top = 4.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn {
                        items(suggestions) { city ->
                            ListItem(
                                headlineContent = { Text(city) },
                                modifier = Modifier
                                    .clickable {
                                        query = city
                                        onCitySelected(city)
                                        expanded = false
                                        focusManager.clearFocus()
                                    }
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}