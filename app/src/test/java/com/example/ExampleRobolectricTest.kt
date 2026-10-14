package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.battery.BatteryMonitor
import com.example.data.CharacterRepository
import com.example.wallpaper.WallpaperPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pika Naruto", appName)
  }

  @Test
  fun `verify character repository`() {
    val characters = CharacterRepository.characters
    assertTrue(characters.size >= 4)
    val naruto = CharacterRepository.getCharacterById("naruto_sixpaths")
    assertNotNull(naruto)
    assertEquals("Naruto Uzumaki", naruto.name)

    val sasuke = CharacterRepository.getCharacterById("sasuke_rinnegan")
    assertNotNull(sasuke)
    assertEquals("Sasuke Uchiha", sasuke.name)
  }

  @Test
  fun `verify battery monitor simulation`() {
    BatteryMonitor.setSimulationMode(true)
    BatteryMonitor.updateSimulatedState(level = 92, isCharging = true)
    val state = BatteryMonitor.batteryState.value
    assertEquals(92, state.level)
    assertEquals(true, state.isCharging)
    assertTrue(state.isSimulated)
  }

  @Test
  fun `verify wallpaper preferences`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = WallpaperPreferences(context)
    prefs.selectedCharacterId = "itachi_mangekyo"
    assertEquals("itachi_mangekyo", prefs.selectedCharacterId)
    prefs.particleDensity = 3
    assertEquals(3, prefs.particleDensity)
  }
}
