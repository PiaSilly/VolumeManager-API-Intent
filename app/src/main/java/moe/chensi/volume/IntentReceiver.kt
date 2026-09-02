package moe.chensi.volume

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class IntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "moe.chensi.volume.action.SET_APP_VOLUME") {
            val targetPackage = intent.getStringExtra("moe.chensi.volume.extra.PACKAGE")
            val volumeMultiplier = intent.getFloatExtra("moe.chensi.volume.extra.VOLUME", -1.0f)

            if (targetPackage != null && volumeMultiplier in 0.0f..1.0f) {
                Log.d("VolumeIntent", "Broadcast empfangen für $targetPackage mit Wert $volumeMultiplier")
                
                // Werte im App-Speicher sichern
                val sharedPrefs = context.getSharedPreferences("volume_settings", Context.MODE_PRIVATE)
                sharedPrefs.edit().putFloat(targetPackage, volumeMultiplier).apply()

                // Internes Signal an den laufenden Service schicken, um Mixer zu aktualisieren
                val refreshIntent = Intent(context, Service::class.java).apply {
                    action = "REFRESH_TRACKS"
                    putExtra("pkg", targetPackage)
                    putExtra("vol", volumeMultiplier)
                }
                try {
                    context.startService(refreshIntent)
                } catch (e: Exception) {
                    Log.e("VolumeIntent", "Service konnte nicht gestartet werden: ${e.message}")
                }
            }
        }
    }
}
