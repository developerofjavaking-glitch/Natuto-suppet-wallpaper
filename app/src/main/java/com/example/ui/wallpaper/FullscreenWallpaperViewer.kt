package com.example.ui.wallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AnimeSoundEngine
import com.example.battery.BatteryMonitor
import com.example.data.CharacterRepository
import com.example.data.NarutoCharacter
import com.example.ui.components.HeroWallpaperSkillOverlay
import com.example.ui.theme.ChakraGold
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.NinjaPrimary
import com.example.ui.theme.RasenganBlue
import com.example.wallpaper.NarutoLiveWallpaperService
import com.example.wallpaper.WallpaperPreferences
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class WallpaperOverlayMode {
    CLEAN,
    LOCK_SCREEN,
    HOME_SCREEN
}

enum class WallpaperFilter {
    VIBRANT,
    DARK_SHINOBI,
    CYBER_NEON,
    MANGA_SEPIA
}

@Composable
fun FullscreenWallpaperViewer(
    initialCharacter: NarutoCharacter,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    val context = LocalContext.current
    val prefs = remember { WallpaperPreferences(context) }
    val batteryState by BatteryMonitor.batteryState.collectAsState()

    var currentCharacter by remember { mutableStateOf(initialCharacter) }
    var overlayMode by remember { mutableStateOf(WallpaperOverlayMode.LOCK_SCREEN) }
    var activeFilter by remember { mutableStateOf(WallpaperFilter.valueOf(prefs.colorFilter)) }
    var showToolbox by remember { mutableStateOf(true) }
    var touchFlash by remember { mutableStateOf(false) }

    // 3D Parallax offset driven by interactive finger drag
    var parallaxOffsetX by remember { mutableFloatStateOf(0f) }
    var parallaxOffsetY by remember { mutableFloatStateOf(0f) }

    val currentTime = remember {
        SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
    }
    val currentAmPm = remember {
        SimpleDateFormat("a", Locale.getDefault()).format(Date()).uppercase()
    }
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(touchFlash) {
        if (touchFlash) {
            delay(180)
            touchFlash = false
        }
    }

    fun applySystemWallpaper() {
        prefs.selectedCharacterId = currentCharacter.id
        prefs.colorFilter = activeFilter.name
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, NarutoLiveWallpaperService::class.java)
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallback = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    "Live Wallpaper updated to ${currentCharacter.name}!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Color filter matrices
    val colorFilter = remember(activeFilter) {
        when (activeFilter) {
            WallpaperFilter.VIBRANT -> null
            WallpaperFilter.DARK_SHINOBI -> {
                // High contrast dark OLED
                ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1.2f, 0f, 0f, 0f, -25f,
                            0f, 1.2f, 0f, 0f, -25f,
                            0f, 0f, 1.2f, 0f, -25f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }
            WallpaperFilter.CYBER_NEON -> {
                // Boost blues & purples
                ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            0.8f, 0f, 0.4f, 0f, 0f,
                            0f, 1.0f, 0.2f, 0f, 0f,
                            0.2f, 0f, 1.4f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }
            WallpaperFilter.MANGA_SEPIA -> {
                // Golden parchment manga look
                ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            0.393f, 0.769f, 0.189f, 0f, 0f,
                            0.349f, 0.686f, 0.168f, 0f, 0f,
                            0.272f, 0.534f, 0.131f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("fullscreen_wallpaper_viewer")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        parallaxOffsetX = 0f
                        parallaxOffsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    parallaxOffsetX = (parallaxOffsetX + dragAmount.x * 0.15f).coerceIn(-35f, 35f)
                    parallaxOffsetY = (parallaxOffsetY + dragAmount.y * 0.15f).coerceIn(-35f, 35f)
                }
            }
    ) {
        // 1. Wallpaper Image with 3D Parallax offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(parallaxOffsetX.roundToInt(), parallaxOffsetY.roundToInt()) }
        ) {
            Image(
                painter = painterResource(id = currentCharacter.drawableRes),
                contentDescription = currentCharacter.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter
            )

            // Animated character-specific skills (Rasengan, Chidori, Amaterasu, Kamui)
            HeroWallpaperSkillOverlay(
                characterId = currentCharacter.id,
                isCharging = batteryState.isCharging
            )

            // Touch Jutsu Flash
            if (touchFlash) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(currentCharacter.glowColor.copy(alpha = 0.35f))
                )
            }
        }

        // Tap on screen to trigger jutsu sound and shockwave
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    touchFlash = true
                    AnimeSoundEngine.playJutsu(currentCharacter.soundType)
                }
        )

        // 2. Simulated Android Screen Overlays
        when (overlayMode) {
            WallpaperOverlayMode.CLEAN -> {
                // Pure art mode, no UI
            }
            WallpaperOverlayMode.LOCK_SCREEN -> {
                // Realistic Lock Screen Simulation with Anime Clock & Ninja Battery Seal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lock icon
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Clock & AM/PM
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = currentTime,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentAmPm,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentCharacter.glowColor,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Text(
                        text = currentDate,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stylized Ninja Battery Badge on Lock Screen
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                listOf(currentCharacter.primaryColor, currentCharacter.secondaryColor)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentCharacter.sealKanji} Chakra: ${batteryState.level}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${batteryState.chakraRank.substringBefore(" ")}",
                                fontSize = 11.sp,
                                color = currentCharacter.glowColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Quote at bottom of lockscreen
                    Text(
                        text = "\"${currentCharacter.quote}\"",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Swipe up to unlock • Tap to release Jutsu",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            WallpaperOverlayMode.HOME_SCREEN -> {
                // Realistic Home Screen App Dock & Search Bar Simulation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Google-style Search Bar pill
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Search apps & shinobi...",
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // App Dock at bottom
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SimulatedAppIcon(icon = Icons.Default.Phone, label = "Phone", tint = Color(0xFF00E676))
                            SimulatedAppIcon(icon = Icons.Default.Chat, label = "Messages", tint = Color(0xFF2979FF))
                            SimulatedAppIcon(icon = Icons.Default.Language, label = "Browser", tint = Color(0xFFFF9100))
                            SimulatedAppIcon(icon = Icons.Default.CameraAlt, label = "Camera", tint = Color(0xFFFF1744))
                            SimulatedAppIcon(icon = Icons.Default.PlayCircle, label = "Media", tint = Color(0xFFE040FB))
                        }
                    }

                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }

        // 3. Floating Wallpaper Toolbox & Navigation Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = showToolbox,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface.copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Character Switcher Carousel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CharacterRepository.characters.forEach { char ->
                                val isSelected = char.id == currentCharacter.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        currentCharacter = char
                                        AnimeSoundEngine.playJutsu(char.soundType)
                                    },
                                    label = { Text(char.name.substringBefore(" ")) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = char.glowColor,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Simulation Overlay Mode Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                WallpaperOverlayMode.CLEAN to "Clean Art",
                                WallpaperOverlayMode.LOCK_SCREEN to "Lock Screen",
                                WallpaperOverlayMode.HOME_SCREEN to "Home Screen"
                            ).forEach { (mode, label) ->
                                val isSelected = overlayMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { overlayMode = mode },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NinjaPrimary,
                                        selectedLabelColor = Color.Black
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Color Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                WallpaperFilter.VIBRANT to "Vibrant Anime",
                                WallpaperFilter.DARK_SHINOBI to "OLED Black",
                                WallpaperFilter.CYBER_NEON to "Cyber Neon",
                                WallpaperFilter.MANGA_SEPIA to "Manga Sepia"
                            ).forEach { (filter, label) ->
                                val isSelected = activeFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        activeFilter = filter
                                        prefs.colorFilter = filter.name
                                    },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = currentCharacter.primaryColor,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Primary Action: Apply as System Wallpaper
                        Button(
                            onClick = { applySystemWallpaper() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_apply_wallpaper_fullscreen"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NinjaPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set as Phone Wallpaper",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Bottom Minimized Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                        .testTag("btn_close_fullscreen_wallpaper")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { showToolbox = !showToolbox }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (showToolbox) Icons.Default.Visibility else Icons.Default.Tune,
                            contentDescription = null,
                            tint = currentCharacter.glowColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showToolbox) "Hide Controls" else "Wallpaper Studio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = { applySystemWallpaper() },
                    modifier = Modifier
                        .size(46.dp)
                        .background(NinjaPrimary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Apply", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun SimulatedAppIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = Color.White)
    }
}
