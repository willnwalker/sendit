package xyz.willnwalker.sendit

import android.app.Application
import android.content.res.Configuration
import android.widget.Toast
import io.radar.sdk.Radar

class SendItApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!

        Radar.initialize("prj_test_pk_c8e0c959f5db981538d205a8748826caffba62bb")
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    override fun onLowMemory() {
        super.onLowMemory()
    }
}