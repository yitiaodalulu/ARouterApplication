package com.example.arouterapplication

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.tencent.mmkv.MMKV
import win.regin.common.utils.Log
import win.regin.common.utils.Utils

class MyApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        application = this
        application = this
    }
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        MMKV.initialize(this)
        Log.isDebug(BuildConfig.DEBUG)
    }

    companion object{
        const val TAG = "MyApp"
        private lateinit var application: Application
        fun getApp(): MyApp {
            return application as MyApp
        }
    }
}