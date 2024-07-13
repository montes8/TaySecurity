package com.tay.taysecurity.android.utils.services

import android.os.Handler
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.uiTayViewCall
import com.tay.taysecurity.android.utils.validNumberBlocking


class TaySureCallService : InCallService() {

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        TaySureCall.taySureCall =  call
        Log.d("TAGTay","uiTayViewCall")
         Handler().postDelayed({
             if (validNumberBlocking(TaySureCall.listContact,TaySureCall.taySureNumber)){
                 Log.d("TAGTay","disconnect")
                 call?.disconnect()
             }else{

                 Log.d("TAGTay","uiTayViewCall")
                 this.uiTayViewCall()
             }
          },1000)
    }
}