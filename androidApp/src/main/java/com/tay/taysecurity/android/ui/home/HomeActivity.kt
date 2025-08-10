package com.tay.taysecurity.android.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tay.taysecurity.android.component.navigation.NavigationHostMain
import com.tay.taysecurity.android.utils.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )  {
            checkSetDefaultDialerResult(it.resultCode)
        }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val permissionCheck = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer los archivos!")
        }
        setContent { MyApplicationTheme{
            NavigationHostMain()

        }}
        checkDefaultDialer()
    }



    private fun checkDefaultDialer() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = this.packageName == telecomManager.defaultDialerPackage
        if (!isAlreadyDefaultDialer) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.packageName)
            requestPermissionLauncher.launch(intent)
        }
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> "El usuario aceptó la solicitud para convertirse en el marcador predeterminado"
            RESULT_CANCELED -> "El usuario rechazó la solicitud para convertirse en el marcador predeterminado"
            else -> "Código de resultado inesperado: $resultCode"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
