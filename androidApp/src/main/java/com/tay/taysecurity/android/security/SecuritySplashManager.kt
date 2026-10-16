package com.tay.taysecurity.android.security

import android.app.Activity
import android.database.ContentObserver
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tay.taysecurity.android.R

object SecuritySplashManager {

    private const val TAG_SPLASH_OVERLAY = "SECURITY_SPLASH_OVERLAY_TAG"
    private const val DEBOUNCE_MS = 2000L
    private var lastShowElapsedMs = 0L
    private val contentObserverMap = mutableMapOf<Activity, ContentObserver>()

    fun Activity.hasSecureWindowFlag(): Boolean =
        (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0

    fun register(activity: Activity) {
        // 1. Android 14+ (API 34+) Callback oficial
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                activity.registerScreenCaptureCallback(activity.mainExecutor) {
                    Log.d("SecuritySplashManager", "Captura detectada por registerScreenCaptureCallback")
                    handleScreenshotDetected(activity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. ContentObserver para MediaStore (Todas las versiones de Android / OEMs)
        try {
            if (contentObserverMap.containsKey(activity)) return

            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    Log.d("SecuritySplashManager", "Captura detectada por ContentObserver (selfChange)")
                    handleScreenshotDetected(activity)
                }

                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                    Log.d("SecuritySplashManager", "Captura detectada por ContentObserver (uri: $uri)")
                    handleScreenshotDetected(activity)
                }
            }

            activity.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
            activity.contentResolver.registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                true,
                observer
            )
            contentObserverMap[activity] = observer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregister(activity: Activity) {
        contentObserverMap.remove(activity)?.let { observer ->
            try {
                activity.contentResolver.unregisterContentObserver(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleScreenshotDetected(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        showSplashOverlay(activity)
    }

    fun showSplashOverlay(activity: Activity) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastShowElapsedMs < DEBOUNCE_MS) return
        lastShowElapsedMs = now

        activity.runOnUiThread {
            try {
                val decorView = activity.window.decorView as? ViewGroup ?: return@runOnUiThread

                // Remover splash anterior si ya está presente
                decorView.findViewWithTag<View>(TAG_SPLASH_OVERLAY)?.let {
                    decorView.removeView(it)
                }

                val d = activity.resources.displayMetrics.density
                val subtitleMessage = activity.getString(R.string.msg_screen_recording_security_policy)

                val container = LinearLayout(activity).apply {
                    tag = TAG_SPLASH_OVERLAY
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(Color.BLACK)

                    val logo = ImageView(activity).apply {
                        setImageResource(R.drawable.ic_logo_sure)
                        scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                    val logoSize = (140 * d).toInt()
                    addView(logo, LinearLayout.LayoutParams(logoSize, logoSize))

                    val title = TextView(activity).apply {
                        text = "TaySecurity"
                        setTextColor(Color.WHITE)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                        typeface = Typeface.DEFAULT_BOLD
                        gravity = Gravity.CENTER
                    }
                    val titleParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (16 * d).toInt()
                    }
                    addView(title, titleParams)

                    val subtitle = TextView(activity).apply {
                        text = subtitleMessage
                        setTextColor(Color.GRAY)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        gravity = Gravity.CENTER
                    }
                    val subtitleParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (8 * d).toInt()
                        marginStart = (24 * d).toInt()
                        marginEnd = (24 * d).toInt()
                    }
                    addView(subtitle, subtitleParams)

                    alpha = 0f
                    animate().alpha(1f).setDuration(250).start()
                }

                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                decorView.addView(container, params)
                container.bringToFront()

                // Ocultar suavemente después de 2.5 segundos
                container.postDelayed({
                    container.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            decorView.removeView(container)
                        }
                        .start()
                }, 2500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
