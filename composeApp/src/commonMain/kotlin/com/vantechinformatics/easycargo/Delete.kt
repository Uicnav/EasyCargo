package com.vantechinformatics.easycargo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.are_you_sure
import easycargo.composeapp.generated.resources.confirm_delete
import easycargo.composeapp.generated.resources.no
import easycargo.composeapp.generated.resources.yes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@Composable
fun GameCard(
    onDelete: () -> Unit,
    onTap: () -> Unit = {},
    isTable: Boolean = false,
    isSwipe: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
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
                .background(Color.Red).padding(end = 16.dp), contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                modifier = Modifier.width(44.dp)
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
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(stringResource(Res.string.confirm_delete))
    }, text = {
        Text(
            text = if (isHome) stringResource(Res.string.are_you_sure) else stringResource(
                Res.string.are_you_sure
            )
        )
    }, confirmButton = {
        ConfirmYesButton(onConfirm = onConfirm)
    }, dismissButton = {
        Button(onClick = onDismiss) {
            Text(stringResource(Res.string.no))
        }
    })
}

@Composable
fun ConfirmYesButton(onConfirm: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text(
            text = stringResource(Res.string.yes), color = Color.White
        )
    }
}