package com.vantechinformatics.easycargo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vantechinformatics.easycargo.ui.theme.EasyCargoTheme
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.are_you_sure
import easycargo.composeapp.generated.resources.confirm_delete
import easycargo.composeapp.generated.resources.ic_delete_white
import easycargo.composeapp.generated.resources.no
import easycargo.composeapp.generated.resources.yes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun GameCard(
    onDelete: () -> Unit,
    onTap: () -> Unit = {},
    isTable: Boolean = false,
    isSwipe: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = EasyCargoTheme.colors
    var showDialog by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val swipeThreshold = -150f
    LaunchedEffect(Unit) {
        if (isSwipe) {
            delay(1000L)
            offsetX.animateTo(-140f, tween(300))
            delay(500L)
            offsetX.animateTo(0f, tween(300))
        }
    }

    Box(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Box(
            modifier = Modifier.matchParentSize().clip(shape = CardDefaults.shape)
                .background(colors.red700).padding(end = 16.dp), contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_delete_white),
                contentDescription = null,
            )
        }

        Card(modifier = Modifier.offset { IntOffset(offsetX.value.toInt(), 0) }.pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                if (offsetX.value <= swipeThreshold) {
                    showDialog = true
                } else {
                    scope.launch {
                        offsetX.animateTo(0f, animationSpec = tween(300))
                    }
                }
            }) { change, dragAmount ->
                change.consume()
                val newOffset = offsetX.value + dragAmount
                scope.launch {
                    offsetX.snapTo(newOffset.coerceIn(-300f, 0f))
                }
            }
        }.pointerInput(Unit) {
            detectTapGestures(onTap = {
                onTap()
            })
        }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(if (isTable) 16.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
                if (isTable) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                }
            }
        }
    }

    if (showDialog) {
        ConfirmDeleteDialog(onConfirm = {
            onDelete()
            showDialog = false
            scope.launch { offsetX.snapTo(0f) }
        }, onDismiss = {
            showDialog = false
            scope.launch { offsetX.snapTo(0f) }
        }, isHome = isTable)
    }

}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, isHome: Boolean) {
    val colors = EasyCargoTheme.colors
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                stringResource(Res.string.confirm_delete),
                fontWeight = FontWeight.Bold,
                color = colors.contentPrimary
            )
        },
        text = {
            Text(
                stringResource(Res.string.are_you_sure),
                color = colors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = colors.red700),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.yes),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    stringResource(Res.string.no),
                    color = colors.textSecondary
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = colors.glassSurface,
        tonalElevation = 0.dp
    )
}
