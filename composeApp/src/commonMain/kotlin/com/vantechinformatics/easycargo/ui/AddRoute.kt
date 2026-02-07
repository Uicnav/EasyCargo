package com.vantechinformatics.easycargo.ui

// composeApp/src/commonMain/kotlin/screens/AddRouteDialog.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import com.vantechinformatics.easycargo.data.RouteUi
import com.vantechinformatics.easycargo.ui.viewmodel.RouteViewModel
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.action_cancel
import easycargo.composeapp.generated.resources.action_create
import easycargo.composeapp.generated.resources.cd_add_new_route
import easycargo.composeapp.generated.resources.dialog_route_title_hint
import easycargo.composeapp.generated.resources.hint_route_examples
import easycargo.composeapp.generated.resources.label_route_name
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.number
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
    val colors = EasyCargoTheme.colors
    val scope = rememberCoroutineScope()
    val prefix = stringResource(Res.string.dialog_route_title_hint)

    // Generăm automat un nume de sugestie: "Cursa ZZ.LL"
    // Dacă nu ai kotlinx-datetime, poți pune doar "Cursa Nouă"
    val defaultName = remember {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "$prefix ${today.day}.${today.month.number}.${today.year}"
    }

    var routeName by remember { mutableStateOf(defaultName) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.glassBorder, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .background(colors.glassSurface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.cd_add_new_route),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.contentPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text(text = stringResource(Res.string.label_route_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = colors.glassBorder,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textSecondary,
                        focusedTextColor = colors.contentPrimary,
                        unfocusedTextColor = colors.contentPrimary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = stringResource(Res.string.hint_route_examples),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (routeName.isNotBlank() && !isSaving) {
                                isSaving = true
                                scope.launch {
                                    val newId = viewModel.insertRoute(
                                        RouteUi(
                                            name = routeName,
                                            isActive = true,
                                        )
                                    )
                                    onRouteCreated(newId)
                                }
                            }
                        },
                        enabled = routeName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                stringResource(Res.string.action_create),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
