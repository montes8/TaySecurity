package com.tay.taysecurity.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.tay.taysecurity.android.utils.MyApplicationTheme
import com.tay.taysecurity.android.utils.loadContactUser
import com.tay.taysecurity.android.utils.manager.TaySureCall
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private val REQUEST_CODE_SET_DEFAULT_DIALER = 123


    companion object {
        fun newInstance(context: Context){
            val i = Intent(context, HomeActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }


    override fun onStart() {
        super.onStart()
        checkDefaultDialer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApplicationTheme{ ScreenHome()}}
        try {
            TaySureCall.listContact = application.loadContactUser()
            viewModel.insertContactAll(TaySureCall.listContact)
        }catch (e:Exception){
            e.printStackTrace()
        }
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
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code: $resultCode"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
