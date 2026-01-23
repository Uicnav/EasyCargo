package com.vantechinformatics.easycargo

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.vantechinformatics.easycargo.data.getDatabaseBuilder
import com.vantechinformatics.easycargo.data.getRoomDatabase

fun MainViewController() = ComposeUIViewController {
    val appDatabase = remember {
        getRoomDatabase(getDatabaseBuilder())
    }
    App(appDatabase)
}