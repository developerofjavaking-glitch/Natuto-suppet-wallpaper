package com.example.ui.charging

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.components.DetailedAmaterasuVisualizer
import com.example.ui.components.DetailedChidoriVisualizer
import com.example.ui.components.DetailedKamuiRaikiriVisualizer
import com.example.ui.components.DetailedRasenganVisualizer
import com.example.ui.components.RotatingSealingCore
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.NinjaSurface
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ChargingJutsuStyle {
    RASENGAN,
    CHIDORI,
    AMATERASU,
    RAIKIRI,
    SEAL_CORE
}

@Composable
fun SuperChargingShowScreen(
    initialCharacter: NarutoCharacter,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    var currentCharacter by remember { mutableStateOf(initialCharacter) }
    val batteryState by BatteryMonitor.batteryState.collectAsState()
    var isMuted by remember { mutableStateOf(AnimeSoundEngine.isMuted) }
    var showTestControls by remember { mutableStateOf(false) }
    var jutsuFlashTrigger by remember { mutableStateOf(false) }

    // Active charging style (defaults based on character)
    var selectedStyle by remember(currentCharacter.id) {
        mutableStateOf(
            when (currentCharacter.id) {
                "sasuke_rinnegan" -> ChargingJutsuStyle.CHIDORI
                "itachi_mangekyo" -> ChargingJutsuStyle.AMATERASU
                "kakashi_raikiri" -> ChargingJutsuStyle.RAIKIRI
                else -> ChargingJutsuStyle.RASENGAN
            }
        )
    }

    // Dynamic decimal fraction counter (e.g. .42% counting up smoothly like fast charge)
    var decimalFraction by remember { mutableFloatStateOf(0.42f) }
    LaunchedEffect(batteryState.isCharging) {
        if (batteryState.isCharging) {
            while (true) {
                delay(80)
                decimalFraction = (decimalFraction + 0.03f)
                if (decimalFraction >= 1.0f) decimalFraction = 0.05f
            }
        }
    }

    // On character change or style change, play jutsu sound
    LaunchedEffect(selectedStyle) {
        when (selectedStyle) {
            ChargingJutsuStyle.RASENGAN -> AnimeSoundEngine.playRasengan()
            ChargingJutsuStyle.CHIDORI -> AnimeSoundEngine.playChidori()
            ChargingJutsuStyle.AMATERASU -> AnimeSoundEngine.playSharingan()
            ChargingJutsuStyle.RAIKIRI -> AnimeSoundEngine.playRaikiri()
            ChargingJutsuStyle.SEAL_CORE -> AnimeSoundEngine.playChakraSurge()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("super_charging_show_screen")
    ) {
        // 1. Character Fullscreen Background Artwork
        Image(
            painter = painterResource(id = currentCharacter.drawableRes),
            contentDescription = currentCharacter.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Dark Vignette Scrim so Jutsu effects pop with neon brilliance
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.90f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // 3. Dynamic Chakra Particle Overlay
        ChakraParticleOverlay(
            character = currentCharacter,
            isCharging = batteryState.isCharging,
            intensity = 1.4f
        )

        // 4. Screen Jutsu Touch Flash
        if (jutsuFlashTrigger) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentCharacter.glowColor.copy(alpha = 0.35f))
            )
        }

        // Tap on character casts jutsu sound + visual shockwave!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    jutsuFlashTrigger = true
                    when (selectedStyle) {
                        ChargingJutsuStyle.RASENGAN -> AnimeSoundEngine.playRasengan()
                        ChargingJutsuStyle.CHIDORI -> AnimeSoundEngine.playChidori()
                        ChargingJutsuStyle.AMATERASU -> AnimeSoundEngine.playSharingan()
                        ChargingJutsuStyle.RAIKIRI -> AnimeSoundEngine.playRaikiri()
                        ChargingJutsuStyle.SEAL_CORE -> AnimeSoundEngine.playChakraSurge()
                    }
                }
        )

        LaunchedEffect(jutsuFlashTrigger) {
            if (jutsuFlashTrigger) {
                delay(180)
                jutsuFlashTrigger = false
            }
        }

        // 5. Main Charging Content & HUD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("charging_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Ninja Sealing State Tag
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp),
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
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = currentCharacter.glowColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (batteryState.isCharging) "CHAKRA SUPERCHARGE" else "DISCHARGING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Sound Mute Toggle
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        AnimeSoundEngine.isMuted = isMuted
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("charging_mute_button")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Sound",
                        tint = if (isMuted) Color.Gray else currentCharacter.glowColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Jutsu Style Switcher Pills (Rasengan, Chidori, Amaterasu, Raikiri, Seal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    ChargingJutsuStyle.RASENGAN to "🌀 Rasengan Vortex",
                    ChargingJutsuStyle.CHIDORI to "⚡ Chidori Lightning",
                    ChargingJutsuStyle.AMATERASU to "🔥 Amaterasu Eye",
                    ChargingJutsuStyle.RAIKIRI to "🗡️ Kamui Raikiri",
                    ChargingJutsuStyle.SEAL_CORE to "封 Eight Trigrams Seal"
                ).forEach { (style, label) ->
                    val isSelected = selectedStyle == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStyle = style },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = currentCharacter.glowColor,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("chip_jutsu_${style.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // THE CENTERPIECE: Detailed Animated Jutsu Visualizer
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        jutsuFlashTrigger = true
                        when (selectedStyle) {
                            ChargingJutsuStyle.RASENGAN -> AnimeSoundEngine.playRasengan()
                            ChargingJutsuStyle.CHIDORI -> AnimeSoundEngine.playChidori()
                            ChargingJutsuStyle.AMATERASU -> AnimeSoundEngine.playSharingan()
                            ChargingJutsuStyle.RAIKIRI -> AnimeSoundEngine.playRaikiri()
                            ChargingJutsuStyle.SEAL_CORE -> AnimeSoundEngine.playChakraSurge()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (selectedStyle) {
                    ChargingJutsuStyle.RASENGAN -> {
                        DetailedRasenganVisualizer(
                            sizeDp = 280.dp,
                            batteryLevel = batteryState.level,
                            isCharging = batteryState.isCharging,
                            showBatteryText = false,
                            modifier = Modifier.testTag("visualizer_rasengan")
                        )
                    }
                    ChargingJutsuStyle.CHIDORI -> {
                        DetailedChidoriVisualizer(
                            sizeDp = 280.dp,
                            batteryLevel = batteryState.level,
                            isCharging = batteryState.isCharging,
                            showBatteryText = false,
                            modifier = Modifier.testTag("visualizer_chidori")
                        )
                    }
                    ChargingJutsuStyle.AMATERASU -> {
                        DetailedAmaterasuVisualizer(
                            sizeDp = 280.dp,
                            batteryLevel = batteryState.level,
                            isCharging = batteryState.isCharging,
                            showBatteryText = false,
                            modifier = Modifier.testTag("visualizer_amaterasu")
                        )
                    }
                    ChargingJutsuStyle.RAIKIRI -> {
                        DetailedKamuiRaikiriVisualizer(
                            sizeDp = 280.dp,
                            batteryLevel = batteryState.level,
                            isCharging = batteryState.isCharging,
                            showBatteryText = false,
                            modifier = Modifier.testTag("visualizer_raikiri")
                        )
                    }
                    ChargingJutsuStyle.SEAL_CORE -> {
                        RotatingSealingCore(
                            character = currentCharacter,
                            batteryLevel = batteryState.level,
                            isCharging = batteryState.isCharging,
                            sizeDp = 260.dp,
                            modifier = Modifier.testTag("visualizer_seal_core")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Decimal Charging Percent Display (e.g., 85.42%)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${batteryState.level}",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                if (batteryState.isCharging) {
                    val decStr = String.format("%.2f", decimalFraction).substring(1)
                    Text(
                        text = decStr,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentCharacter.glowColor,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = "%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentCharacter.glowColor,
                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                )
            }

            // Wattage & Speed Pill
            Surface(
                color = currentCharacter.primaryColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = currentCharacter.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentCharacter.chargingSpeedTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Character Switcher Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CharacterRepository.characters.forEach { char ->
                    val isSelected = char.id == currentCharacter.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { currentCharacter = char },
                        label = {
                            Text(
                                text = char.name.substringBefore(" "),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = char.glowColor,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("chip_select_${char.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Battery Telemetry HUD Card
            Card(
                colors = CardDefaults.cardColors(containerColor = NinjaSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("charging_telemetry_hud")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelemetryItem(
                        icon = Icons.Default.Speed,
                        label = "Speed",
                        value = "${batteryState.estimatedWatts}W",
                        tint = currentCharacter.primaryColor
                    )
                    TelemetryItem(
                        icon = Icons.Default.Bolt,
                        label = "Voltage",
                        value = "${batteryState.voltageMv / 1000f}V",
                        tint = currentCharacter.secondaryColor
                    )
                    TelemetryItem(
                        icon = Icons.Default.Thermostat,
                        label = "Heat",
                        value = "${batteryState.temperatureC}°C",
                        tint = currentCharacter.glowColor
                    )
                    TelemetryItem(
                        icon = Icons.Default.PlayArrow,
                        label = "Rank",
                        value = batteryState.chakraRank.substringBefore(" "),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simulation Controls Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showTestControls) "Hide Simulation Controls ▲" else "Test Simulation Controls ▼",
                    fontSize = 12.sp,
                    color = currentCharacter.glowColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showTestControls = !showTestControls }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag("toggle_simulation_controls")
                )
            }

            if (showTestControls) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Simulate Charging State",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Switch(
                                checked = batteryState.isCharging,
                                onCheckedChange = { checked ->
                                    BatteryMonitor.setSimulationMode(true)
                                    BatteryMonitor.updateSimulatedState(isCharging = checked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentCharacter.primaryColor,
                                    checkedTrackColor = currentCharacter.primaryColor.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.testTag("switch_simulate_charging")
                            )
                        }

                        Text(
                            text = "Test Battery Level: ${batteryState.level}%",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Slider(
                            value = batteryState.level.toFloat(),
                            onValueChange = { newLevel ->
                                BatteryMonitor.setSimulationMode(true)
                                BatteryMonitor.updateSimulatedState(level = newLevel.toInt())
                            },
                            valueRange = 1f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = currentCharacter.primaryColor,
                                activeTrackColor = currentCharacter.primaryColor
                            ),
                            modifier = Modifier.testTag("slider_battery_level")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TelemetryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 11.sp, color = Color.LightGray)
    }
}
