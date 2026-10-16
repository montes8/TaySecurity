package com.tay.taysecurity.android.ui.home

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.navigation.NavigationHostMain
import com.tay.taysecurity.android.security.NavigationSecurityObserver
import com.tay.taysecurity.android.security.SecuritySplashManager
import com.tay.taysecurity.android.utils.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var navigationSecurityObserver: NavigationSecurityObserver

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

    private var privacyOverlayView: View? = null

    companion object {
        private const val UPDATE_CODE = 10001
        private const val PERMISSION_REQUEST_CODE = 225
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        navigationSecurityObserver.attachActivity(this)
        SecuritySplashManager.register(this)
        setContent {
            MyApplicationTheme {
                NavigationHostMain(navigationSecurityObserver = navigationSecurityObserver)
            }
        }
        setupPrivacyOverlayView()
        validateVersionUpdate()
    }

    @SuppressLint("SetTextI18n")
    private fun setupPrivacyOverlayView() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(AndroidColor.BLACK)

            val logo = ImageView(this@HomeActivity).apply {
                setImageResource(R.drawable.ic_logo_sure)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            val logoParams = LinearLayout.LayoutParams(140.dpToPx(), 140.dpToPx())
            addView(logo, logoParams)

            val title = TextView(this@HomeActivity).apply {
                text = "TaySecurity"
                setTextColor(AndroidColor.WHITE)
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
            }
            val titleParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16.dpToPx()
            }
            addView(title, titleParams)

            val subtitle = TextView(this@HomeActivity).apply {
                text = "Aplicación Protegida"
                setTextColor(AndroidColor.GRAY)
                textSize = 14f
                gravity = Gravity.CENTER
            }
            val subtitleParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 6.dpToPx()
            }
            addView(subtitle, subtitleParams)

        }

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        (window.decorView as ViewGroup).addView(container, params)
        privacyOverlayView = container
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        privacyOverlayView?.apply {
            bringToFront()
            visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        privacyOverlayView?.visibility = View.GONE
        navigationSecurityObserver.reapplySecurity()
    }

    override fun onDestroy() {
        super.onDestroy()
        SecuritySplashManager.unregister(this)
        navigationSecurityObserver.detachActivity()
    }

    private fun initPermission() {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(READ_EXTERNAL_STORAGE)
        }

        val permissionsNotGranted = permissionsToRequest.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNotGranted.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("HomeActivity", "¡Permiso para leer imágenes de la galería otorgado!")
        }
    }

    private fun validateVersionUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    this,
                    updateOptions,
                    UPDATE_CODE
                )
                finish()
            } else {
                initPermission()
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            initPermission()
        }
    }
}
