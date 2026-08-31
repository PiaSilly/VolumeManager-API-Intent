package moe.chensi.volume

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Intent API for automation apps such as Tasker/MacroDroid.
 *
 * Action:
 *   moe.chensi.volume.action.SET_APP_VOLUME
 *
 * Extras:
 *   moe.chensi.volume.extra.PACKAGE  -> String package name
 *   moe.chensi.volume.extra.VOLUME   -> Float 0.0..1.0
 *
 * Example:
 *   am broadcast -a moe.chensi.volume.action.SET_APP_VOLUME \
 *       -e moe.chensi.volume.extra.PACKAGE com.example.app \
 *       --ef moe.chensi.volume.extra.VOLUME 0.5
 */
class IntentReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "VolumeManager.Intent"

        const val ACTION_SET_APP_VOLUME =
            "moe.chensi.volume.action.SET_APP_VOLUME"

        const val EXTRA_PACKAGE =
            "moe.chensi.volume.extra.PACKAGE"

        const val EXTRA_VOLUME =
            "moe.chensi.volume.extra.VOLUME"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SET_APP_VOLUME) {
            return
        }

        val packageName = intent.getStringExtra(EXTRA_PACKAGE)
        if (packageName.isNullOrBlank()) {
            Log.w(TAG, "Missing $EXTRA_PACKAGE")
            return
        }

        if (!intent.hasExtra(EXTRA_VOLUME)) {
            Log.w(TAG, "Missing $EXTRA_VOLUME")
            return
        }

        val volume = intent.getFloatExtra(EXTRA_VOLUME, Float.NaN)
        if (volume.isNaN() || volume.isInfinite()) {
            Log.w(TAG, "Invalid volume: $volume")
            return
        }

        val manager = (context.applicationContext as MyApplication).manager
        if (!manager.setAppVolume(packageName, volume)) {
            Log.w(TAG, "Unknown package or invalid request: $packageName")
        } else {
            Log.i(TAG, "Set $packageName volume to ${volume.coerceIn(0f, 1f)}")
        }
    }
}
