package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.battery.BatteryMonitor
import com.example.data.CharacterRepository
import com.example.data.NarutoCharacter
import com.example.ui.charging.SuperChargingShowScreen
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.wallpaper.WallpaperPreferences

sealed interface AppScreen {
    data object Home : AppScreen
    data class ChargingShow(val character: NarutoCharacter) : AppScreen
}

class MainActivity : ComponentActivity() {

    private val launchChargingShowState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        BatteryMonitor.startListening(this)

        if (intent?.getBooleanExtra("AUTO_LAUNCH_CHARGING_SHOW", false) == true) {
            launchChargingShowState.value = true
        }

        setContent {
            MyApplicationTheme {
                val prefs = remember { WallpaperPreferences(this) }
                val defaultChar = remember {
                    CharacterRepository.getCharacterById(prefs.selectedCharacterId)
                }

                var currentScreen by remember {
                    mutableStateOf<AppScreen>(
                        if (launchChargingShowState.value) AppScreen.ChargingShow(defaultChar)
                        else AppScreen.Home
                    )
                }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        is AppScreen.Home -> {
                            HomeScreen(
                                onOpenChargingShow = { character ->
                                    currentScreen = AppScreen.ChargingShow(character)
                                }
                            )
                        }
                        is AppScreen.ChargingShow -> {
                            SuperChargingShowScreen(
                                initialCharacter = screen.character,
                                onClose = { currentScreen = AppScreen.Home }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("AUTO_LAUNCH_CHARGING_SHOW", false)) {
            val prefs = WallpaperPreferences(this)
            val char = CharacterRepository.getCharacterById(prefs.selectedCharacterId)
            launchChargingShowState.value = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BatteryMonitor.stopListening(this)
    }
}
