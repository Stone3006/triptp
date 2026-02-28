package main.triptip

import android.app.Application
import org.maplibre.android.MapLibre

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
    }
}
