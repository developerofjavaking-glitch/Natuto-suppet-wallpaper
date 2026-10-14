package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val NinjaDarkColorScheme = darkColorScheme(
    primary = NinjaPrimary,
    onPrimary = NinjaOnPrimary,
    primaryContainer = NinjaPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = ChakraGoldBright,
    secondary = NinjaSecondary,
    onSecondary = NinjaOnPrimary,
    secondaryContainer = NinjaSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = RasenganBlue,
    tertiary = NinjaTertiary,
    onTertiary = NinjaOnPrimary,
    background = NinjaBackground,
    onBackground = NinjaOnBackground,
    surface = NinjaSurface,
    onSurface = NinjaOnSurface,
    surfaceVariant = NinjaSurfaceVariant,
    onSurfaceVariant = NinjaOnSurface.copy(alpha = 0.8f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // prioritize our custom anime styling
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> NinjaDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
