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
        setupActivityListener()
    }

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            }
            override fun onActivityStarted(activity: Activity) {
                //not implementation
            }

            override fun onActivityResumed(activity: Activity) {
                //not implementation
            }

            override fun onActivityPaused(activity: Activity) {
                //not implementation
            }

            override fun onActivityStopped(activity: Activity) {
                //not implementation
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //not implementation
            }

            override fun onActivityDestroyed(activity: Activity) {
                //not implementation
            }
        })
    }



}