package com.vantechinformatics.easycargo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.vantechinformatics.easycargo.data.AppDatabase
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.data.RouteUi
import com.vantechinformatics.easycargo.ui.AddRouteDialog
import com.vantechinformatics.easycargo.ui.EmptyResultMessage
import com.vantechinformatics.easycargo.ui.GameCard
import com.vantechinformatics.easycargo.ui.RouteDetailsScreen
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import com.vantechinformatics.easycargo.ui.theme.THEME_MODE_KEY
import com.vantechinformatics.easycargo.ui.theme.ThemeMode
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.ui.viewmodel.RouteUiState
import com.vantechinformatics.easycargo.ui.viewmodel.RouteViewModel
import com.vantechinformatics.easycargo.utils.LANGUAGE_KEY
import com.vantechinformatics.easycargo.utils.LocalDataStore
import com.vantechinformatics.easycargo.utils.LocalNavHostController
import com.vantechinformatics.easycargo.utils.LocalSnackbarHostState
import com.vantechinformatics.easycargo.utils.SUPPORTED_LANGUAGES
import com.vantechinformatics.easycargo.utils.setAppLocale
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_close
import easycargo.composeapp.generated.resources.app_name
import easycargo.composeapp.generated.resources.cd_toggle_theme
import easycargo.composeapp.generated.resources.background_app
import easycargo.composeapp.generated.resources.cd_add_new_route
import easycargo.composeapp.generated.resources.format_euro
import easycargo.composeapp.generated.resources.label_created_at
import easycargo.composeapp.generated.resources.label_empty_routes
import easycargo.composeapp.generated.resources.language_selector_title
import easycargo.composeapp.generated.resources.language_system_default
import easycargo.composeapp.generated.resources.route_deleted
import easycargo.composeapp.generated.resources.stats_label_delivered
import easycargo.composeapp.generated.resources.stats_label_money
import easycargo.composeapp.generated.resources.stats_label_parcels
import easycargo.composeapp.generated.resources.undo
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
object HomeDest

@Serializable
data class RouteDetailsScreenDest(val idRoute: Long)

@Composable
@Preview
fun App(appDatabase: AppDatabase, dataStore: DataStore<Preferences>) {
    val themeMode by dataStore.data.map { prefs ->
        when (prefs[THEME_MODE_KEY]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            else -> ThemeMode.DARK
        }
    }.collectAsState(initial = ThemeMode.DARK)

    val isDark = themeMode == ThemeMode.DARK

    val currentLanguage by dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY]
    }.collectAsState(initial = null)

    EasyCargoTheme(darkTheme = isDark) {
        val colors = EasyCargoTheme.colors
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()

        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalSnackbarHostState provides snackbarHostState,
            LocalDataStore provides dataStore
        ) {
            // Background image behind everything
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(Res.drawable.background_app),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay for readability
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    colors.overlayGradientTop,
                                    colors.overlayGradientMid,
                                    colors.overlayGradientBottom
                                )
                            )
                        )
                )

                NavHost(
                    navController = navController,
                    startDestination = HomeDest,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<HomeDest> {
                        val viewModel = viewModel { RouteViewModel(appDatabase.routeDao(), appDatabase.parcelDao()) }
                        HomeScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDark,
                            onToggleTheme = {
                                scope.launch {
                                    dataStore.edit { prefs ->
                                        prefs[THEME_MODE_KEY] = if (isDark) ThemeMode.LIGHT.name else ThemeMode.DARK.name
                                    }
                                }
                            },
                            currentLanguage = currentLanguage,
                            onLanguageSelected = { langCode ->
                                scope.launch {
                                    dataStore.edit { prefs ->
                                        if (langCode == null) {
                                            prefs.remove(LANGUAGE_KEY)
                                        } else {
                                            prefs[LANGUAGE_KEY] = langCode
                                        }
                                    }
                                    setAppLocale(langCode ?: "")
                                }
                            }
                        ) {
                            navController.navigate(RouteDetailsScreenDest(it))
                        }
                    }
                    composable<RouteDetailsScreenDest> {
                        val args = it.toRoute<RouteDetailsScreenDest>()
                        val viewModel = ParcelViewModel(appDatabase.parcelDao())
                        RouteDetailsScreen(routeId = args.idRoute, viewModel = viewModel) { }
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RouteViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    currentLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    onNavigateToRoute: (Long) -> Unit
) {
    val colors = EasyCargoTheme.colors
    var showAddDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val routesState = viewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(Res.string.language_selector_title),
                            tint = colors.contentPrimary
                        )
                    }
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(Res.string.cd_toggle_theme),
                            tint = colors.contentPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.cd_add_new_route))
            }
        },
        containerColor = Color.Transparent
    ) { padding ->

        when (val state = routesState.value) {
            is RouteUiState.Loading -> {
                isLoading = true
                if (isLoading) {
                    CenteredCircularProgressIndicator()
                }
            }

            is RouteUiState.Success -> {
                DisposableEffect(Unit) {
                    onDispose { viewModel.deleteRouteToDelete() }
                }
                LazyColumn(modifier = Modifier.padding(padding).padding(horizontal = 12.dp)) {
                    if (state.data.isEmpty()) {
                        item {
                            EmptyResultMessage(text = stringResource(Res.string.label_empty_routes))
                        }
                    } else {
                        items(state.data) { route ->
                            val routeStats = viewModel.getRouteStats(route.routeId)
                                .collectAsState(initial = null)

                            GameCard(onDelete = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                viewModel.prepareDeleteRoute(routeId = route.routeId)
                                scope.launch {
                                    snackbarHostState.undoDeleteRouteSnackbar(
                                        onActionPerformed = { viewModel.undoDeleteRoute(route.routeId) },
                                        onDismissed = { viewModel.deleteRouteComplete(route.routeId) }
                                    )
                                }
                            }) {
                                RouteCard(
                                    route = route,
                                    stats = routeStats.value,
                                    onClick = { onNavigateToRoute(route.routeId) }
                                )
                            }
                        }
                    }
                }
                isLoading = false
            }

            is RouteUiState.Error -> {
                isLoading = false
            }
        }

        if (showAddDialog) {
            AddRouteDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onRouteCreated = { newRouteId ->
                    showAddDialog = false
                    onNavigateToRoute(newRouteId)
                })
        }

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = currentLanguage,
                onLanguageSelected = { langCode ->
                    showLanguageDialog = false
                    onLanguageSelected(langCode)
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = EasyCargoTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(Res.string.language_selector_title),
                color = colors.contentPrimary
            )
        },
        text = {
            Column {
                // System default option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected(null) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguage == null,
                        onClick = { onLanguageSelected(null) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = colors.textSecondary
                        )
                    )
                    Text(
                        stringResource(Res.string.language_system_default),
                        color = colors.contentPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                // Language options
                SUPPORTED_LANGUAGES.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(lang.code) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == lang.code,
                            onClick = { onLanguageSelected(lang.code) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = colors.textSecondary
                            )
                        )
                        Text(
                            lang.displayName,
                            color = colors.contentPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(Res.string.action_close),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = colors.glassSurface.copy(alpha = 1f)
    )
}

@Composable
fun RouteCard(route: RouteUi, stats: RouteStats?, onClick: () -> Unit) {
    val colors = EasyCargoTheme.colors
    if (route.isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(colors.glassCard)
                .clickable { onClick() }
        ) {
            Row {
                // Orange left accent stripe
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (stats != null) 130.dp else 70.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                    // Route name
                    Text(
                        route.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                    // Route ID + date
                    Text(
                        "#${route.routeId} · ${stringResource(Res.string.label_created_at)} ${route.createdAt.getFormattedDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    if (stats != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats chips row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatChip(
                                value = "${stats.deliveredParcels}",
                                label = stringResource(Res.string.stats_label_delivered),
                                color = colors.greenLight,
                                modifier = Modifier.weight(1f)
                            )
                            StatChip(
                                value = "${stats.totalParcels}",
                                label = stringResource(Res.string.stats_label_parcels),
                                color = colors.orangeLight,
                                modifier = Modifier.weight(1f)
                            )
                            StatChip(
                                value = "${stats.totalMoney.format(0)}${stringResource(Res.string.format_euro)}",
                                label = stringResource(Res.string.stats_label_money),
                                color = colors.greenLight,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mini progress bar
                        val progressTarget = if (stats.totalParcels > 0)
                            stats.deliveredParcels.toFloat() / stats.totalParcels.toFloat()
                        else 0f
                        val animatedProgress by animateFloatAsState(
                            targetValue = progressTarget, label = "RouteProgress"
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.weight(1f).height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (progressTarget >= 1f) colors.greenLight else MaterialTheme.colorScheme.primary,
                                trackColor = colors.progressTrack,
                            )
                            Text(
                                text = "${(progressTarget * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
    }
}

suspend fun SnackbarHostState.undoDeleteRouteSnackbar(
    onActionPerformed: () -> Unit, onDismissed: () -> Unit
) {
    val result = this.showSnackbar(
        message = getString(Res.string.route_deleted),
        actionLabel = getString(Res.string.undo),
        withDismissAction = true
    )
    if (result == SnackbarResult.ActionPerformed) {
        onActionPerformed()
    } else {
        onDismissed()
    }
}
