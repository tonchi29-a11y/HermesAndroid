package com.hermes.app

import android.app.Application

class HermesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Future: DI setup, crash reporting, etc.
    }
}
