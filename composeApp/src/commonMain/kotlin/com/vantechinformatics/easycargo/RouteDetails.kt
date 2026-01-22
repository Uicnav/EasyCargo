package com.vantechinformatics.easycargo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vantechinformatics.easycargo.data.AppDao
import com.vantechinformatics.easycargo.data.ParcelEntity
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.utils.LocalNavHostController
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.format_euro
import easycargo.composeapp.generated.resources.msg_empty_search
import easycargo.composeapp.generated.resources.search_placeholder
import easycargo.composeapp.generated.resources.stats_label_delivered
import easycargo.composeapp.generated.resources.stats_label_money
import easycargo.composeapp.generated.resources.stats_label_parcels
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Asigură-te că ai importat extensia .format(2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    routeId: Long, dao: AppDao, onBack: () -> Unit
) {
    // STATE PENTRU CĂUTARE
    var searchQuery by remember { mutableStateOf("") }

    // Lista se actualizează automat când 'searchQuery' se schimbă
    // Dacă searchQuery e gol (""), SQL-ul returnează tot (LIKE '%%')
    val parcelsState = dao.searchParcels(routeId, searchQuery).collectAsState(initial = emptyList())

    // Statistici (rămân neschimbate)
    val statsState = dao.getRouteStats(routeId).collectAsState(
        initial = RouteStats(
            totalParcels = 0, deliveredParcels = 0, totalMoney = 0.0
        )
    )

    var selectedParcel by remember { mutableStateOf<ParcelEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Focus Manager ca să ascundem tastatura
    val focusManager = LocalFocusManager.current
    val navController = LocalNavHostController.current
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("#${routeId}") }, navigationIcon = {
            IconButton(onClick = {
                onBack()
                navController.navigateUp()
            }) {
                Icon(
                    Icons.Default.ArrowBack, null
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
        )
        )
    }, containerColor = Color.Transparent, floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(
                Icons.Default.Add, null
            )
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // --- HEADER STATISTICI (Totaluri) ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.stats_label_delivered),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${statsState.value.deliveredParcels}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(Res.string.stats_label_parcels),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${statsState.value.totalParcels}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1F)
                        ) {
                            Text(
                                stringResource(Res.string.stats_label_money),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${statsState.value.totalMoney.format(2)} ${stringResource(Res.string.format_euro)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text(stringResource(Res.string.search_placeholder)) },
                        modifier = Modifier.background(Color.Transparent).fillMaxWidth()
                            .padding(16.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Șterge")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                    )
                }

            }



            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE COLETE ---
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp) // Spațiu jos pentru butonul flotant
            ) {
                if (parcelsState.value.isEmpty()) {
                    item {
                        EmptyResultMessage(text = stringResource(Res.string.msg_empty_search))
                    }
                } else {
                    items(parcelsState.value) { parcel ->
                        ParcelListItem(
                            parcel = parcel, onClick = { selectedParcel = parcel })
                    }
                }
            }
        }

        // Dialogurile (Adăugare și Detalii) rămân la fel
        if (showAddDialog) {
            // Aici ar trebui să fie AddParcelScreen (dacă e ecran full) sau AddParcelDialog (dacă e pop-up)
            // Pentru consistență cu exemplul anterior, folosim Dialog:
            AddParcelDialog(routeId, dao, onDismiss = {
                showAddDialog = false
            }, onParcelAdded = {
                selectedParcel = it
            })
        }

        selectedParcel?.let { parcel ->
            ParcelDetailsDialog(parcel, dao) { selectedParcel = null }
        }
    }
}

@Composable
fun EmptyResultMessage(text: String) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
    ) {
        Text(
            text =text,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center

        )
    }
}

@Preview
@Composable
fun ParcelListItem(parcel: ParcelEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CERCUL CU ID-ul (#100)
            Surface(
                color = if (parcel.isDelivered) Color.Gray else Color.Yellow,
                shape = CircleShape,
                modifier = Modifier.wrapContentSize()
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                    ParcelIdDisplay(displayId = parcel.displayId)
                }
            }

            Spacer(Modifier.width(16.dp))

            // INFORMAȚII CENTRALE
            Column(modifier = Modifier.weight(1f).background(Color.Transparent)) {
                Text(
                    text = parcel.firstNameLastName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )

                // Afișăm telefonul și suma
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Phone,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = parcel.phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${parcel.totalSum.format(2)} €",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32) // Verde închis pentru bani
                )
            }

            // ICONIȚA DE LIVRAT
            if (parcel.isDelivered) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Livrat",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}