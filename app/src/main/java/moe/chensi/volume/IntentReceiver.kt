package app.vtools.volumemanager // Make sure this matches your project's top-level package

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class VolumeIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "moe.chensi.volume.action.SET_APP_VOLUME") {
            val targetPkg = intent.getStringExtra("moe.chensi.volume.extra.PACKAGE")
            val rawVolume = intent.getFloatExtra("moe.chensi.volume.extra.VOLUME", -1.0f)

            if (targetPkg != null && rawVolume in 0.0f..1.0f) {
                Log.d("VolumeIntentReceiver", "API Intent received: $targetPkg -> $rawVolume")

                // Save to the exact shared preferences instance VolumeManager uses
                val prefs = context.getSharedPreferences("app_volumes", Context.MODE_PRIVATE)
                prefs.edit().putFloat(targetPkg, rawVolume).apply()

                // Notify the active tracking service to immediately process the changes
                try {
                    val intentService = Intent(context, Class.forName("app.vtools.volumemanager.VolumeTrackingService")) // Update string to match your exact Tracking Service path
                    intentService.action = "REFRESH_VOLUME_TRACKS"
                    intentService.putExtra("target_package", targetPkg)
                    context.startService(intentService)
                } catch (e: Exception) {
                    Log.e("VolumeIntentReceiver", "Failed to notify VolumeTrackingService: ${e.message}")
                }
            }
        }
    }
}
