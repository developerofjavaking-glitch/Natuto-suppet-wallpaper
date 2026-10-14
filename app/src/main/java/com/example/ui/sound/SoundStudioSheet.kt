package com.example.ui.sound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AnimeSoundEngine
import com.example.data.JutsuSoundType
import com.example.ui.theme.ChakraGold
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.NinjaPrimary
import com.example.wallpaper.WallpaperPreferences

data class SoundItem(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val type: JutsuSoundType?,
    val isSystemSound: Boolean = false,
    val playAction: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundStudioSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val prefs = remember { WallpaperPreferences(context) }

    var soundOnTouch by remember { mutableStateOf(prefs.soundOnTouch) }
    var soundOnCharging by remember { mutableStateOf(prefs.soundOnCharging) }
    var masterVolume by remember { mutableFloatStateOf(AnimeSoundEngine.volume) }
    var currentlyPlaying by remember { mutableStateOf<String?>(null) }

    val soundItems = remember {
        listOf(
            SoundItem(
                title = "Rasengan Whirl",
                description = "High-speed vortex frequency sweep & chakra resonance",
                iconEmoji = "🌀",
                type = JutsuSoundType.RASENGAN,
                playAction = { AnimeSoundEngine.playRasengan() }
            ),
            SoundItem(
                title = "Chidori Thousand Birds",
                description = "High-voltage erratic lightning crackle & micro sparks",
                iconEmoji = "⚡",
                type = JutsuSoundType.CHIDORI,
                playAction = { AnimeSoundEngine.playChidori() }
            ),
            SoundItem(
                title = "Sharingan Awakening",
                description = "Mystical dual-tone chime with eerie lingering phaser",
                iconEmoji = "👁️",
                type = JutsuSoundType.SHARINGAN,
                playAction = { AnimeSoundEngine.playSharingan() }
            ),
            SoundItem(
                title = "Raikiri Lightning Blade",
                description = "Sharp high-frequency lightning slice with electric hiss",
                iconEmoji = "🗡️",
                type = JutsuSoundType.RAIKIRI,
                playAction = { AnimeSoundEngine.playRaikiri() }
            ),
            SoundItem(
                title = "Flying Raijin Teleport",
                description = "Hypersonic space-time sweep & metallic kunai flash",
                iconEmoji = "✨",
                type = JutsuSoundType.FLYING_RAIJIN,
                playAction = { AnimeSoundEngine.playFlyingRaijin() }
            ),
            SoundItem(
                title = "Nine-Tails Chakra Surge",
                description = "Deep resonant sub-bass pulse & expanding sonic wave",
                iconEmoji = "🦊",
                type = JutsuSoundType.CHAKRA_SURGE,
                playAction = { AnimeSoundEngine.playChakraSurge() }
            ),
            SoundItem(
                title = "Charger Plugged-In Chime",
                description = "Ascending 4-tone chakra restoration melody",
                iconEmoji = "🔌",
                type = null,
                isSystemSound = true,
                playAction = { AnimeSoundEngine.playChargingConnected() }
            ),
            SoundItem(
                title = "100% Chakra Full Fanfare",
                description = "Triumphant Hokage 5-note energy peak chord",
                iconEmoji = "🔋",
                type = null,
                isSystemSound = true,
                playAction = { AnimeSoundEngine.playFullyCharged() }
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkNavySurface,
        modifier = Modifier.testTag("sound_studio_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = NinjaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Jutsu Sound Studio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volume Control Slider
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = ChakraGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Master Jutsu Volume", fontSize = 14.sp, color = Color.White)
                        }
                        Text(text = "${(masterVolume * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ChakraGold)
                    }
                    Slider(
                        value = masterVolume,
                        onValueChange = {
                            masterVolume = it
                            AnimeSoundEngine.volume = it
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = ChakraGold,
                            activeTrackColor = ChakraGold
                        ),
                        modifier = Modifier.testTag("slider_volume")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sound Triggers Toggles
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Play Sound on Charger Plug-In", fontSize = 13.sp, color = Color.White)
                        Switch(
                            checked = soundOnCharging,
                            onCheckedChange = {
                                soundOnCharging = it
                                prefs.soundOnCharging = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NinjaPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Play Jutsu Sound on Touch Screen", fontSize = 13.sp, color = Color.White)
                        Switch(
                            checked = soundOnTouch,
                            onCheckedChange = {
                                soundOnTouch = it
                                prefs.soundOnTouch = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NinjaPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AUDITION JUTSU SOUNDS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of Sound Effects
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(soundItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentlyPlaying = item.title
                                item.playAction()
                            }
                            .testTag("sound_item_${item.title.replace(" ", "_").lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = item.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = item.description,
                                        fontSize = 11.sp,
                                        color = Color.LightGray,
                                        maxLines = 1
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    currentlyPlaying = item.title
                                    item.playAction()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(NinjaPrimary.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = NinjaPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
