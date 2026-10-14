package com.example.battery

data class BatteryState(
    val level: Int = 85,
    val isCharging: Boolean = true,
    val plugType: String = "Turbo Fast Charge (65W)",
    val voltageMv: Int = 4120,
    val temperatureC: Float = 31.2f,
    val health: String = "Good",
    val technology: String = "Li-ion",
    val estimatedWatts: Float = 45.0f,
    val estimatedTimeToFullMinutes: Int = 18,
    val isSimulated: Boolean = false
) {
    val chakraRank: String
        get() = when {
            level >= 95 -> "Hokage (Max Chakra)"
            level >= 75 -> "Jonin Level"
            level >= 50 -> "Chunin Level"
            level >= 25 -> "Genin Level"
            else -> "Chakra Depleted (Critical)"
        }
}
