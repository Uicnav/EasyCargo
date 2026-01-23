package com.vantechinformatics.easycargo.ui

// composeApp/src/commonMain/kotlin/screens/AddRouteDialog.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vantechinformatics.easycargo.data.RouteEntity
import com.vantechinformatics.easycargo.ui.viewmodel.RouteViewModel
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_cancel
import easycargo.composeapp.generated.resources.action_create
import easycargo.composeapp.generated.resources.cd_add_new_route
import easycargo.composeapp.generated.resources.dialog_route_title_hint
import easycargo.composeapp.generated.resources.hint_route_examples
import easycargo.composeapp.generated.resources.label_route_name
import kotlinx.coroutines.launch
import kotlinx.datetime.* // Necesită 'kotlinx-datetime' în libs.versions.toml
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun AddRouteDialog(
    viewModel: RouteViewModel,
    onDismiss: () -> Unit,
    onRouteCreated: (Long) -> Unit // Returnăm ID-ul ca să navigăm direct la ea
) {
    val scope = rememberCoroutineScope()
    val prefix = stringResource(Res.string.dialog_route_title_hint)

    // Generăm automat un nume de sugestie: "Cursa ZZ.LL"
    // Dacă nu ai kotlinx-datetime, poți pune doar "Cursa Nouă"
    val defaultName = remember {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "$prefix ${today.dayOfMonth}.${today.monthNumber}.${today.year}"
    }

    var routeName by remember { mutableStateOf(defaultName) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.cd_add_new_route),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text(text = stringResource(Res.string.label_route_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(Res.string.hint_route_examples),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.action_cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (routeName.isNotBlank() && !isSaving) {
                                isSaving = true
                                // Lansăm coroutina pentru salvare
                                scope.launch {
                                    val newId = viewModel.insertRoute(
                                        RouteEntity(
                                            name = routeName,
                                            isActive = true
                                        )
                                    )
                                    onRouteCreated(newId)
                                }
                            }
                        },
                        enabled = routeName.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(Res.string.action_create))
                        }
                    }
                }
            }
        }
    }
}