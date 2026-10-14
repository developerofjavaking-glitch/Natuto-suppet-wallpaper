package com.example.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.audio.AnimeSoundEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object BatteryMonitor {
    private val _batteryState = MutableStateFlow(BatteryState())
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private var isSimulating = false
    private var simulatedState = BatteryState(
        level = 78,
        isCharging = true,
        plugType = "Super Dart Flash Charge (65W)",
        voltageMv = 4210,
        temperatureC = 30.8f,
        isSimulated = true
    )

    private var previousChargingState: Boolean? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || isSimulating) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 75

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val plugType = when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC Turbo Fast Charge"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB Smart Power"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Qi Dock"
                else -> if (isCharging) "Fast Charger" else "Discharging (Battery)"
            }

            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)
            val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300)
            val tempC = rawTemp / 10.0f
            val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
            val health = when (healthInt) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                else -> "Normal"
            }
            val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

            // Estimate wattage: Fast chargers typically 15W-65W
            val estimatedWatts = if (isCharging) {
                val base = (voltage / 1000f) * 3.5f
                (base * 10).toInt() / 10f
            } else 0f

            val timeToFull = if (isCharging) {
                ((100 - batteryPct) * 0.75f).toInt().coerceAtLeast(1)
            } else 0

            // Detect plug-in or unplug sound triggers
            if (previousChargingState != null) {
                if (previousChargingState == false && isCharging) {
                    AnimeSoundEngine.playChargingConnected()
                } else if (previousChargingState == true && !isCharging) {
                    AnimeSoundEngine.playChargingDisconnected()
                }
            }
            if (batteryPct == 100 && previousChargingState == true && (previousChargingState != null)) {
                AnimeSoundEngine.playFullyCharged()
            }
            previousChargingState = isCharging

            _batteryState.value = BatteryState(
                level = batteryPct,
                isCharging = isCharging,
                plugType = plugType,
                voltageMv = voltage,
                temperatureC = tempC,
                health = health,
                technology = technology,
                estimatedWatts = estimatedWatts,
                estimatedTimeToFullMinutes = timeToFull,
                isSimulated = false
            )
        }
    }

    fun startListening(context: Context) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initialIntent = context.registerReceiver(receiver, filter)
        if (initialIntent != null && !isSimulating) {
            receiver.onReceive(context, initialIntent)
        }
    }

    fun stopListening(context: Context) {
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {}
    }

    fun setSimulationMode(enabled: Boolean) {
        isSimulating = enabled
        if (enabled) {
            _batteryState.value = simulatedState
        }
    }

    fun updateSimulatedState(
        level: Int = simulatedState.level,
        isCharging: Boolean = simulatedState.isCharging
    ) {
        val wasCharging = simulatedState.isCharging
        simulatedState = simulatedState.copy(
            level = level.coerceIn(1, 100),
            isCharging = isCharging,
            plugType = if (isCharging) "Six Paths Chakra Supercharge (120W)" else "Discharging (Battery)",
            estimatedWatts = if (isCharging) 65.0f else 0.0f,
            estimatedTimeToFullMinutes = if (isCharging) ((100 - level) * 0.4f).toInt().coerceAtLeast(1) else 0,
            isSimulated = true
        )
        _batteryState.value = simulatedState

        if (!wasCharging && isCharging) {
            AnimeSoundEngine.playChargingConnected()
        } else if (wasCharging && !isCharging) {
            AnimeSoundEngine.playChargingDisconnected()
        }
        if (level == 100 && isCharging) {
            AnimeSoundEngine.playFullyCharged()
        }
    }
}
