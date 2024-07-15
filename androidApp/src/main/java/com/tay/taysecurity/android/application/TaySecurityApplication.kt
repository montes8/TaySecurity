package com.tay.taysecurity.android.application

import android.app.Application
import com.tay.taysecurity.manager.appContext
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TaySecurityApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}