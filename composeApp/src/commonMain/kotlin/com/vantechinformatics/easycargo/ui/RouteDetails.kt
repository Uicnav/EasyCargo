package com.vantechinformatics.easycargo.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.format
import com.vantechinformatics.easycargo.ui.theme.GlassBorder
import com.vantechinformatics.easycargo.ui.theme.GlassCard
import com.vantechinformatics.easycargo.ui.theme.GlassSurface
import com.vantechinformatics.easycargo.ui.theme.GreenLight
import com.vantechinformatics.easycargo.ui.theme.Green900
import com.vantechinformatics.easycargo.ui.theme.OrangeLight
import com.vantechinformatics.easycargo.ui.theme.TextMuted
import com.vantechinformatics.easycargo.ui.theme.TextSecondary
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.undoDeleteRouteSnackbar
import com.vantechinformatics.easycargo.utils.LocalNavHostController
import com.vantechinformatics.easycargo.utils.LocalSnackbarHostState
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.btn_mark_delivered
import easycargo.composeapp.generated.resources.format_euro
import easycargo.composeapp.generated.resources.msg_empty_search
import easycargo.composeapp.generated.resources.search_placeholder
import easycargo.composeapp.generated.resources.stats_label_money
import easycargo.composeapp.generated.resources.status_delivery
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    routeId: Long, viewModel: ParcelViewModel, onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val parcelsState =
        viewModel.searchParcels(routeId, searchQuery).collectAsState(initial = emptyList())

    val statsState = viewModel.getRouteStats(routeId).collectAsState(
        initial = RouteStats(totalParcels = 0, deliveredParcels = 0, totalMoney = 0.0)
    )

    var selectedParcel by remember { mutableStateOf<ParcelUi?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val navController = LocalNavHostController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    DisposableEffect(Unit) {
        onDispose { viewModel.deleteParcelToDelete() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("#$routeId", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 12.dp)) {

            Spacer(modifier = Modifier.height(8.dp))

            // --- GLASS STATS HEADER ---
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .background(GlassSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Delivery count + money row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(Res.string.status_delivery),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "${statsState.value.deliveredParcels} / ${statsState.value.totalParcels}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stringResource(Res.string.stats_label_money),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "${statsState.value.totalMoney.format(2)} ${stringResource(Res.string.format_euro)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = GreenLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar
                    DeliveryProgressBar(
                        deliveredCount = statsState.value.deliveredParcels,
                        totalCount = statsState.value.totalParcels
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search field - glass style
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                stringResource(Res.string.search_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = TextSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = TextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- PARCEL LIST ---
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                if (parcelsState.value.isEmpty()) {
                    item {
                        EmptyResultMessage(text = stringResource(Res.string.msg_empty_search))
                    }
                } else {
                    items(parcelsState.value) {parcel ->
                        GameCard(onDelete = {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                viewModel.prepareDeleteParcel(parcel)
                                snackbarHostState.undoDeleteRouteSnackbar(
                                    onActionPerformed = { viewModel.undoDeleteParcel() },
                                    onDismissed = { viewModel.deleteParcelsById(parcel.id) }
                                )
                            }
                        }) {
                            ParcelListItem(parcel = parcel, onClick = { selectedParcel = parcel })
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddParcelDialog(routeId, viewModel = viewModel, onDismiss = {
                showAddDialog = false
            }, onParcelAdded = { selectedParcel = it })
        }

        selectedParcel?.let { parcel ->
            ParcelDetailsDialog(parcel, viewModel) { selectedParcel = null }
        }
    }
}

@Composable
fun DeliveryProgressBar(
    deliveredCount: Int, totalCount: Int, modifier: Modifier = Modifier
) {
    val progressTarget = if (totalCount > 0) {
        deliveredCount.toFloat() / totalCount.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget, label = "ProgressAnimation"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.weight(1f).height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (progressTarget >= 1f) GreenLight else MaterialTheme.colorScheme.primary,
            trackColor = Color.White.copy(alpha = 0.15f),
        )
        Text(
            text = "${(progressTarget * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (progressTarget >= 1f) GreenLight else OrangeLight,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun EmptyResultMessage(text: String) {
    Box(
        modifier = Modifier.padding(16.dp).fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .background(GlassCard)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Preview
@Composable
fun ParcelListItem(parcel: ParcelUi, onClick: () -> Unit) {
    val cardAlpha = if (parcel.isDelivered) 0.65f else 1f

    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .background(GlassCard)
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Parcel ID badge - orange glass
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (parcel.isDelivered)
                                Color.White.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ParcelIdDisplay(
                        displayId = parcel.displayId,
                        isDelivered = parcel.isDelivered
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Central info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = parcel.firstNameLastName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    if (parcel.city.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = OrangeLight
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = parcel.city,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (parcel.phone.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        val uriHandler = LocalUriHandler.current
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { uriHandler.openUri("tel:${parcel.phone}") }
                        ) {
                            Icon(
                                Icons.Default.Phone, null,
                                modifier = Modifier.size(12.dp),
                                tint = OrangeLight
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = parcel.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = OrangeLight
                            )
                        }
                    }
                }

                // Delivered checkmark
                if (parcel.isDelivered) {
                    Surface(
                        color = GreenLight.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GreenLight,
                            modifier = Modifier.size(28.dp).padding(2.dp)
                        )
                    }
                }
            }

            // Divider + price/pieces row
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = GlassBorder
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${parcel.totalSum.format(2)} ${stringResource(Res.string.format_euro)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenLight
                )
                Text(
                    text = "${parcel.pieceCount} ${if (parcel.pieceCount == 1) "colet" else "colete"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
