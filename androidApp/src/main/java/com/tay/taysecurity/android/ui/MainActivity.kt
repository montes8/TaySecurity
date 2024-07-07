package com.tay.taysecurity.android.ui

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tay.taysecurity.android.component.Navigation
import com.tay.taysecurity.android.utils.MyApplicationTheme
import com.tay.taysecurity.android.utils.tayToast

class MainActivity : ComponentActivity() {


    private val broadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val telephonyManager: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;


            telephonyManager.listen(object: PhoneStateListener(){
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    super.onCallStateChanged(state, phoneNumber)
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> {
                            Log.d("TAGTay","not llamada")
                            tayToast("not llamada")
                        }
                        TelephonyManager.CALL_STATE_RINGING -> {
                            Log.d("TAGTay", "entrada")
                            tayToast(" entrada")
                        }
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            tayToast(" en la llamada")
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Navigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
       // registerReceiver(broadcast, filter)
    }

    override fun onPause() {
        super.onPause()
       // unregisterReceiver(broadcast)
    }
}
