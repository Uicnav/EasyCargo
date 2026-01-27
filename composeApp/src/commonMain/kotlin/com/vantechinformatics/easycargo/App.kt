package com.vantechinformatics.easycargo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vantechinformatics.easycargo.data.AppDatabase
import com.vantechinformatics.easycargo.data.RouteUi
import com.vantechinformatics.easycargo.ui.AddRouteDialog
import com.vantechinformatics.easycargo.ui.EmptyResultMessage
import com.vantechinformatics.easycargo.ui.GameCard
import com.vantechinformatics.easycargo.ui.RouteDetailsScreen
import com.vantechinformatics.easycargo.ui.viewmodel.ParcelViewModel
import com.vantechinformatics.easycargo.ui.viewmodel.RouteUiState
import com.vantechinformatics.easycargo.ui.viewmodel.RouteViewModel
import com.vantechinformatics.easycargo.utils.LocalNavHostController
import com.vantechinformatics.easycargo.utils.LocalSnackbarHostState
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.app_name
import easycargo.composeapp.generated.resources.background_app
import easycargo.composeapp.generated.resources.cd_add_new_route
import easycargo.composeapp.generated.resources.label_created_at
import easycargo.composeapp.generated.resources.label_empty_routes
import easycargo.composeapp.generated.resources.route_deleted
import easycargo.composeapp.generated.resources.undo
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
fun App(appDatabase: AppDatabase) {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()

        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalSnackbarHostState provides snackbarHostState
        ) {

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(Res.drawable.background_app),
                    contentDescription = "App background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                NavHost(
                    navController = navController,
                    startDestination = HomeDest,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    composable<HomeDest> {
                        val viewModel = viewModel { RouteViewModel(appDatabase.routeDao()) }

                        HomeScreen(viewModel) {
                            navController.navigate(RouteDetailsScreenDest(it))
                        }
                    }
                    composable<RouteDetailsScreenDest> {
                        val args = it.toRoute<RouteDetailsScreenDest>()
                        val viewModel = ParcelViewModel(appDatabase.parcelDao())
                        RouteDetailsScreen(routeId = args.idRoute, viewModel = viewModel) {

                        }
                    }
                }


            }
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.Transparent).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


// composeApp/src/commonMain/kotlin/screens/HomeScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RouteViewModel, onNavigateToRoute: (Long) -> Unit // Callback de navigare
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val routesState = viewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current

    val scope = rememberCoroutineScope()

    // Aici îți iei lista de rute din DB (Flow)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(
                Icons.Default.Add, contentDescription = stringResource(Res.string.cd_add_new_route)
            )
        }
    }, containerColor = Color.Transparent) { padding ->

        when (val state = routesState.value) {
            is RouteUiState.Loading -> {
                isLoading = true
                if (isLoading) {
                    CenteredCircularProgressIndicator()
                }
            }

            is RouteUiState.Success -> {
                LazyColumn(modifier = Modifier.padding(padding)) {

                    if (state.data.isEmpty()) {
                        item {
                            EmptyResultMessage(text = stringResource(Res.string.label_empty_routes))
                        }
                    } else {
                        items(state.data) { route ->
                            GameCard(onDelete = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                viewModel.prepareDeleteRoute(routeId = route.routeId)
                                scope.launch {
                                    snackbarHostState.undoDeleteRouteSnackbar(onActionPerformed = {
                                        viewModel.undoDeleteRoute(route.routeId)

                                    }, onDismissed = {
                                        viewModel.deleteRouteComplete(route.routeId)

                                    })
                                }
                            }) {
                                RouteCard(
                                    route = route, onClick = { onNavigateToRoute(route.routeId) })
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
        // Lista Rutelor Existente

        // --- AICI ESTE MAGIA ---
        if (showAddDialog) {
            AddRouteDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onRouteCreated = { newRouteId ->
                    showAddDialog = false
                    // Navigăm direct în interiorul rutei noi create!
                    onNavigateToRoute(newRouteId)
                })
        }
    }
}

@Composable
fun RouteCard(route: RouteUi, onClick: () -> Unit) {
    if (route.isVisible) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(route.name, style = MaterialTheme.typography.headlineLarge)
                // Poți formata data frumos aici
                Text(
                    "#${route.routeId} ${stringResource(Res.string.label_created_at)} ${route.createdAt.getFormattedDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red
                )
            }
        }
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

