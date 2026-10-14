package com.example.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.audio.AnimeSoundEngine
import com.example.MainActivity

class BatteryChargingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                AnimeSoundEngine.playChargingConnected()
                // Optionally bring charging show to foreground if desired
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("AUTO_LAUNCH_CHARGING_SHOW", true)
                }
                try {
                    context.startActivity(launchIntent)
                } catch (_: Exception) {}
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                AnimeSoundEngine.playChargingDisconnected()
            }
        }
    }
}
