package com.tay.taysecurity.android.ui.home

import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tay.taysecurity.android.component.navigation.NavigationHostMain
import com.tay.taysecurity.android.utils.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val REQUEST_CODE_SET_DEFAULT_DIALER = 123

    override fun onStart() {
        super.onStart()
        checkDefaultDialer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApplicationTheme{
           // ScreenHome()
            NavigationHostMain()

        }}
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode)
        }
    }

    private fun checkDefaultDialer() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = this.packageName == telecomManager.defaultDialerPackage
        if (!isAlreadyDefaultDialer) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.packageName)


            
            this.startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
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
