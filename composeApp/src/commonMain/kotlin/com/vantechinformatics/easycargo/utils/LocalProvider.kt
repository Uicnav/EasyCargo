package com.vantechinformatics.easycargo.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

internal val LocalNavHostController = compositionLocalOf<NavHostController> { error("Nav host controller not provided") }
