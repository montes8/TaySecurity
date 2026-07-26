package com.tay.taysecurity.android.ui.home

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tay.taysecurity.android.component.navigation.NavigationHostMain
import com.tay.taysecurity.android.utils.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

    companion object {
        private const val UPDATE_CODE = 10001
        private const val PERMISSION_REQUEST_CODE = 225
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent { MyApplicationTheme{
            NavigationHostMain()
        }}
        validateVersionUpdate()
    }

    private fun initPermission() {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
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
        val appUpdateManager = AppUpdateManagerFactory.create(this)
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
