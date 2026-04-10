package com.example.nerevian

import android.app.Application
import android.util.Log

class NerevianApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("NerevianAPI", "¡La Aplicación NerevianApp ha despertado!")
    }
}