package com.vantechinformatics.easycargo.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

internal val LocalNavHostController = compositionLocalOf<NavHostController> { error("Nav host controller not provided") }
internal val LocalSnackbarHostState= compositionLocalOf<SnackbarHostState> { error("SnackbarHostState not provided") }
