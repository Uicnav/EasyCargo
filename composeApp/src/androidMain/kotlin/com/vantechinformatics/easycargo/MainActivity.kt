package com.vantechinformatics.easycargo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vantechinformatics.easycargo.data.getDatabaseBuilder
import com.vantechinformatics.easycargo.data.getRoomDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val appDatabase = getRoomDatabase(getDatabaseBuilder(applicationContext))
        setContent {
            App(appDatabase.appDao())
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {

    //App()
}