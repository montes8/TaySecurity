package com.tay.taysecurity.android.security

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tay.taysecurity.android.R

object CustomViewBlockToast {

    private const val TAG_BANNER = "SECURITY_BANNER_TAG"
    private const val DEBOUNCE_MS = 3000L
    private var lastToastElapsedMs = 0L

    private fun Activity.hasSecureWindowFlag(): Boolean =
        (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0

    fun checkAndShow(activity: Activity) {
        if (!activity.hasSecureWindowFlag()) return
        showSecurityBanner(activity)
    }

    fun registerScreenCaptureListener(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                activity.registerScreenCaptureCallback(activity.mainExecutor) {
                    if (activity.hasSecureWindowFlag()) {
                        showSecurityBanner(activity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showSecurityBanner(activity: Activity) {
        Log.d("SureSecurity", "Muestra Banner de Seguridad en pantalla protegida")
        val now = SystemClock.elapsedRealtime()
        if (now - lastToastElapsedMs < DEBOUNCE_MS) return
        lastToastElapsedMs = now

        activity.runOnUiThread {
            try {
                val decorView = activity.window.decorView as? ViewGroup ?: return@runOnUiThread

                // Remover banner anterior si existe
                decorView.findViewWithTag<View>(TAG_BANNER)?.let {
                    decorView.removeView(it)
                }

                val d = activity.resources.displayMetrics.density
                val message = activity.getString(R.string.msg_screen_recording_security_policy)

                // Layout del Banner Flotante
                val container = LinearLayout(activity).apply {
                    tag = TAG_BANNER
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL

                    // Fondo oscuro redondeado con borde sutil
                    val shape = GradientDrawable().apply {
                        setColor(0xEE1A1A1A.toInt())
                        cornerRadius = 20f * d
                        setStroke((1.5f * d).toInt(), 0x44FFFFFF.toInt())
                    }
                    background = shape

                    val padH = (16 * d).toInt()
                    val padV = (10 * d).toInt()
                    setPadding(padH, padV, padH, padV)

                    // Logo de la App
                    val logo = ImageView(activity).apply {
                        setImageResource(R.drawable.ic_logo_sure)
                        val iconSize = (28 * d).toInt()
                        layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                            marginEnd = (12 * d).toInt()
                        }
                    }
                    addView(logo)

                    // Texto del aviso
                    val text = TextView(activity).apply {
                        text = message
                        setTextColor(Color.WHITE)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                        typeface = Typeface.DEFAULT_BOLD
                    }
                    addView(text)

                    // Animación de entrada
                    alpha = 0f
                    translationY = -60f * d
                    animate().alpha(1f).translationY(0f).setDuration(350).start()
                }

                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    topMargin = (50 * d).toInt()
                }

                decorView.addView(container, params)

                // Ocultar con animación después de 3.5 segundos
                container.postDelayed({
                    container.animate()
                        .alpha(0f)
                        .translationY(-60f * d)
                        .setDuration(350)
                        .withEndAction {
                            decorView.removeView(container)
                        }
                        .start()
                }, 3500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
