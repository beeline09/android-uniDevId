package ru.unidevidlib.adg

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import ru.unidevid.lib.UdidManager


class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        UdidManager.init(this)
    }
}