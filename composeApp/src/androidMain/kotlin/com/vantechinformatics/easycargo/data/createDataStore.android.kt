package com.vantechinformatics.easycargo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlin.io.resolve

private lateinit var dataStoreInstance: DataStore<Preferences>

fun createDataStore(context: Context): DataStore<Preferences> {
    if (!::dataStoreInstance.isInitialized) {
        dataStoreInstance = createDataStore {
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
    return dataStoreInstance
}
