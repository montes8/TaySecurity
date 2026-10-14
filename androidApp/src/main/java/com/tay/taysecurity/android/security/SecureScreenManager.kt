package com.tay.taysecurity.android.security

import android.app.Activity
import android.util.Log
import android.view.WindowManager
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.tay.taysecurity.android.component.navigation.DestinationRoot
import com.tay.taysecurity.android.component.navigation.DestinationsMain
import kotlin.reflect.KClass

class SecureScreenManager(
    private val extraSecureClasses: Set<KClass<*>> = setOf(
        DestinationsMain.StealthLockScreen::class,
        DestinationRoot.SecurityScreen::class
    )
) {
    companion object {
        private const val TAG = "SecureScreenController"
    }

    var isSecure: Boolean = false
        private set

    fun applySecurity(activity: Activity, destination: NavDestination) {
        val requiresSecurity = isDestinationSecure(destination)
        isSecure = requiresSecurity

        if (requiresSecurity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            Log.d(TAG, "FLAG_SECURE activado para: ${destination.route}")
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            Log.d(TAG, "FLAG_SECURE desactivado para: ${destination.route}")
        }
    }

    fun reapply(activity: Activity) {
        if (isSecure) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun clearSecurity(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        isSecure = false
    }

    private fun isDestinationSecure(destination: NavDestination): Boolean {
        if (extraSecureClasses.any { kClass -> destination.hasRoute(kClass) }) {
            return true
        }
        val rawRoute = destination.route ?: return false
        val className = rawRoute.substringBefore("?").substringBefore("/")

        val possibleClassNames = listOf(
            className,
            className.replaceLastDotWithDollar()
        )

        for (name in possibleClassNames) {
            try {
                val clazz = Class.forName(name)
                if (SecureDestination::class.java.isAssignableFrom(clazz)) {
                    return true
                }
            } catch (_: Exception) {
                // Probar el siguiente formato de nombre
            }
        }

        return false
    }

    private fun String.replaceLastDotWithDollar(): String {
        val lastDot = this.lastIndexOf('.')
        if (lastDot == -1) return this
        return this.substring(0, lastDot) + "$" + this.substring(lastDot + 1)
    }
}

interface SecureDestination
