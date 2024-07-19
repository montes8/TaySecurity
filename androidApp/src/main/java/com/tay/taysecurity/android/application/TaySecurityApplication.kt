package com.tay.taysecurity.android.application

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TaySecurityApplication : Application(){

    companion object {
        lateinit  var appContext: Context
    }


    override fun onCreate() {
        super.onCreate()
        appContext = this
        TaySecurityApplication.appContext = applicationContext
    }



}