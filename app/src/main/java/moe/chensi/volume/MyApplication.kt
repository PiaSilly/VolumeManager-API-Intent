package moe.chensi.volume

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuProvider

class MyApplication : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_volumes")

    /**
     * Shared Manager instance used by the UI, accessibility service and Intent API.
     */
    val manager: Manager by lazy { Manager(this, dataStore) }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        ShizukuProvider.enableMultiProcessSupport(true)
        HiddenApiBypass.addHiddenApiExemptions("")
    }
}