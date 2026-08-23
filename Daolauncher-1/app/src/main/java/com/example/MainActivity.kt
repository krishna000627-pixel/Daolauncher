package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CultivationRealm
import com.example.ui.screens.MainLauncherScreen
import com.example.ui.theme.CultivationTheme
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profileState by viewModel.profile.collectAsStateWithLifecycle()
            val realm = profileState?.currentRealm ?: CultivationRealm.QI_CONDENSATION

            CultivationTheme(realm = realm) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VoidDark
                ) {
                    MainLauncherScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshInstalledApps()
        viewModel.checkLauncherStatus()
    }
}
