package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AnimeSoundEngine
import com.example.battery.BatteryMonitor
import com.example.data.CharacterRepository
import com.example.data.NarutoCharacter
import com.example.ui.components.ChakraParticleOverlay
import com.example.ui.sound.SoundStudioSheet
import com.example.ui.theme.ChakraGold
import com.example.ui.theme.ChakraGoldBright
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.NinjaBackground
import com.example.ui.theme.NinjaPrimary
import com.example.ui.theme.RasenganBlue
import com.example.ui.wallpaper.WallpaperSettingsSheet
import com.example.wallpaper.WallpaperPreferences
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenChargingShow: (NarutoCharacter) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { WallpaperPreferences(context) }
    var selectedCharacter by remember {
        mutableStateOf(CharacterRepository.getCharacterById(prefs.selectedCharacterId))
    }

    val batteryState by BatteryMonitor.batteryState.collectAsState()

    var showSoundSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var jutsuFlash by remember { mutableStateOf(false) }

    val currentTime = remember {
        SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
    }
    val currentAmPm = remember {
        SimpleDateFormat("a", Locale.getDefault()).format(Date()).uppercase()
    }
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(jutsuFlash) {
        if (jutsuFlash) {
            delay(200)
            jutsuFlash = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NinjaBackground)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.linearGradient(listOf(ChakraGold, RasenganBlue)),
                                CircleShape
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.ic_app_icon_src),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PIKA",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ChakraGoldBright,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "NARUTO",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Super Wallpaper & Charging",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.LightGray
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { showSoundSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkNavySurface, CircleShape)
                            .testTag("btn_open_sound_studio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Sound Studio",
                            tint = ChakraGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkNavySurface, CircleShape)
                            .testTag("btn_open_wallpaper_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Wallpaper Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Interactive Super Wallpaper Live Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
                    .padding(horizontal = 20.dp)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(selectedCharacter.glowColor, Color.Transparent)
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .testTag("live_wallpaper_preview_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Character Artwork
                    Image(
                        painter = painterResource(id = selectedCharacter.drawableRes),
                        contentDescription = selectedCharacter.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Dark gradient scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Dynamic Chakra Particles
                    ChakraParticleOverlay(
                        character = selectedCharacter,
                        isCharging = batteryState.isCharging,
                        intensity = 1.1f
                    )

                    // Center Floating Jutsu Orb (Rasengan / Chidori / Amaterasu / Raikiri)
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (selectedCharacter.id) {
                            "naruto_sixpaths" -> {
                                com.example.ui.components.DetailedRasenganVisualizer(
                                    sizeDp = 150.dp,
                                    batteryLevel = batteryState.level,
                                    isCharging = batteryState.isCharging,
                                    showBatteryText = false
                                )
                            }
                            "sasuke_rinnegan" -> {
                                com.example.ui.components.DetailedChidoriVisualizer(
                                    sizeDp = 150.dp,
                                    batteryLevel = batteryState.level,
                                    isCharging = batteryState.isCharging,
                                    showBatteryText = false
                                )
                            }
                            "itachi_mangekyo" -> {
                                com.example.ui.components.DetailedAmaterasuVisualizer(
                                    sizeDp = 150.dp,
                                    batteryLevel = batteryState.level,
                                    isCharging = batteryState.isCharging,
                                    showBatteryText = false
                                )
                            }
                            "kakashi_raikiri" -> {
                                com.example.ui.components.DetailedKamuiRaikiriVisualizer(
                                    sizeDp = 150.dp,
                                    batteryLevel = batteryState.level,
                                    isCharging = batteryState.isCharging,
                                    showBatteryText = false
                                )
                            }
                        }
                    }

                    // Touch Jutsu Flash
                    if (jutsuFlash) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(selectedCharacter.glowColor.copy(alpha = 0.35f))
                        )
                    }

                    // Clickable touch area to cast Jutsu
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                jutsuFlash = true
                                AnimeSoundEngine.playJutsu(selectedCharacter.soundType)
                            }
                    )

                    // Top Ninja HUD Overlay: Digital Clock & Date
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = currentTime,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentAmPm,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedCharacter.glowColor,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = currentDate,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }

                    // Bottom info: Character Name & Signature Jutsu
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Surface(
                            color = selectedCharacter.primaryColor.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = selectedCharacter.clan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedCharacter.glowColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedCharacter.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = selectedCharacter.signatureJutsu,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Tap preview to release Jutsu sound!",
                                fontSize = 11.sp,
                                color = selectedCharacter.glowColor
                            )

                            // Battery level mini pill
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = selectedCharacter.glowColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${batteryState.level}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Hero Action Buttons: Launch Super Charging Show & Apply Wallpaper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onOpenChargingShow(selectedCharacter) },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("btn_launch_charging_show"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NinjaPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Super Charging",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("btn_set_wallpaper_modal"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = Brush.horizontalGradient(listOf(ChakraGold, RasenganBlue))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        tint = ChakraGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Set Wallpaper",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Character Gallery Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NARUTO CHARACTERS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "4 Super Wallpapers",
                    fontSize = 12.sp,
                    color = ChakraGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(CharacterRepository.characters) { character ->
                    val isSelected = character.id == selectedCharacter.id
                    CharacterCard(
                        character = character,
                        isSelected = isSelected,
                        onSelect = {
                            selectedCharacter = character
                            prefs.selectedCharacterId = character.id
                            AnimeSoundEngine.playJutsu(character.soundType)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Battery Real-Time Telemetry Bar
            Text(
                text = "BATTERY TELEMETRY & CHAKRA STATUS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (batteryState.isCharging) ChakraGold else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = batteryState.plugType,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${batteryState.level}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (batteryState.isCharging) ChakraGold else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Chakra Level: ${batteryState.chakraRank}", fontSize = 12.sp, color = Color.LightGray)
                        Text(text = "Temp: ${batteryState.temperatureC}°C", fontSize = 12.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }

    if (showSoundSheet) {
        SoundStudioSheet(onDismiss = { showSoundSheet = false })
    }

    if (showSettingsSheet) {
        WallpaperSettingsSheet(onDismiss = { showSettingsSheet = false })
    }
}

@Composable
private fun CharacterCard(
    character: NarutoCharacter,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(210.dp)
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) character.glowColor else DarkNavyBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("character_card_${character.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = character.drawableRes),
                contentDescription = character.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            if (isSelected) {
                Surface(
                    color = character.glowColor,
                    shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = character.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = character.chakraNature.substringBefore(" •"),
                    fontSize = 10.sp,
                    color = character.glowColor
                )
            }
        }
    }
}
