package com.tay.taysecurity.android.security

import android.app.Activity
import android.util.Log
import androidx.navigation.NavDestination
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationSecurityObserver @Inject constructor(
    private val manager: SecureScreenManager
) {
    companion object {
        private const val TAG = "SureObserver"
    }

    private var activityRef: WeakReference<Activity>? = null
    private var lastProcessedRoute: String? = null

    fun attachActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun detachActivity() {
        activityRef?.get()?.let { activity ->
            manager.clearSecurity(activity)
        }
        activityRef = null
        lastProcessedRoute = null
    }

    fun onDestinationChanged(destination: NavDestination) {
        val currentRoute = destination.route ?: return
        if (currentRoute == lastProcessedRoute) return
        lastProcessedRoute = currentRoute

        activityRef?.get()?.let { activity ->
            manager.applySecurity(activity, destination)
        } ?: run {
            Log.w(TAG, "Activity no disponible para aplicar seguridad en: $currentRoute")
        }
    }

    fun reapplySecurity() {
        activityRef?.get()?.let { activity ->
            manager.reapply(activity)
            Log.d(TAG, "Seguridad reaplicada (estado efectivo)")
        }
    }
}
