package com.tay.taysecurity.android.utils.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.utils.SECURITY_EMPTY
import com.tay.taysecurity.utils.SECURITY_TAG


class BroadCallSecurity : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent!!.getStringExtra(TelephonyManager.EXTRA_STATE)
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.e(SECURITY_TAG,"not call")
            }
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val bundle = intent.extras
                val phoneNr = bundle?.getString("incoming_number")?: SECURITY_EMPTY
                TaySureCall.taySureNumber = phoneNr
                Log.d(SECURITY_TAG,TaySureCall.taySureNumber+"init")
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.e(SECURITY_TAG,"en call")
            }
        }
    }



}