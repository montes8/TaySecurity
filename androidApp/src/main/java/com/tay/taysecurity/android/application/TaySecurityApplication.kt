package com.tay.taysecurity.android.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import com.tay.taysecurity.manager.appContext
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TaySecurityApplication : Application(){

    companion object {
        lateinit  var appContextTaySure: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        appContextTaySure = this
    }
}