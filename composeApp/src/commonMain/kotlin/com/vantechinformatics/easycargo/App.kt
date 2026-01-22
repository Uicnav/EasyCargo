package com.vantechinformatics.easycargo

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vantechinformatics.easycargo.data.AppDao
import com.vantechinformatics.easycargo.data.RouteEntity
import com.vantechinformatics.easycargo.utils.LocalNavHostController
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.app_name
import easycargo.composeapp.generated.resources.background_app
import easycargo.composeapp.generated.resources.cd_add_new_route
import easycargo.composeapp.generated.resources.label_created_at
import easycargo.composeapp.generated.resources.label_empty_routes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
object HomeDest


@Serializable
data class RouteDetailsScreenDest(val idRoute: Long)

@Composable
@Preview
fun App(dao: AppDao) {
    MaterialTheme {
        remember { SnackbarHostState() }
        val navController = rememberNavController()

        CompositionLocalProvider(
            LocalNavHostController provides navController
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
                        HomeScreen(dao) {
                            navController.navigate(RouteDetailsScreenDest(it))
                        }
                    }
                    composable<RouteDetailsScreenDest> {
                        val args = it.toRoute<RouteDetailsScreenDest>()
                        RouteDetailsScreen(routeId = args.idRoute, dao = dao) {

                        }
                    }
                }


            }
        }
    }
}


// composeApp/src/commonMain/kotlin/screens/HomeScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dao: AppDao, onNavigateToRoute: (Long) -> Unit // Callback de navigare
) {
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    // Aici îți iei lista de rute din DB (Flow)
    val routesState = dao.getAllRoutes().collectAsState(initial = emptyList())

    Scaffold(topBar = {
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
        // Lista Rutelor Existente
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (routesState.value.isEmpty()) {
                item {
                    EmptyResultMessage(text = stringResource(Res.string.label_empty_routes))
                }
            } else {
                items(routesState.value) { route ->
                    GameCard(onDelete = {
                        scope.launch {
                            dao.deleteRouteComplete(routeId = route.routeId)
                        }
                    }) {
                        RouteCard(
                            route = route, onClick = { onNavigateToRoute(route.routeId) })
                    }

                }
            }

        }

        // --- AICI ESTE MAGIA ---
        if (showAddDialog) {
            AddRouteDialog(
                dao = dao,
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
fun RouteCard(route: RouteEntity, onClick: () -> Unit) {
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

