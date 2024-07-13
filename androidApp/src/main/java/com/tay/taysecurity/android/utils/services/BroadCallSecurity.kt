package com.tay.taysecurity.android.utils.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.tay.taysecurity.android.ui.home.HomeActivity
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.tayToast


class BroadCallSecurity : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent!!.getStringExtra(TelephonyManager.EXTRA_STATE)
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> {
               if (TaySureCall.tayAppView) context?.let {
                   Log.d("TAGTay","HomeActivity")
                   HomeActivity.newInstance(it)
               }
                Log.d("TAGTay","not llamada")
                context?.tayToast("not llamada")
            }
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val bundle = intent.extras
                val phoneNr = bundle?.getString("incoming_number")?:""
                TaySureCall.taySureNumber = phoneNr
                Log.e("TAGTay", "incoming number : $phoneNr")
                Log.d("TAGTay", "entrada")
                context?.tayToast(" entrada")


            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                context?.tayToast(" en la llamada")
            }
        }
    }



}