package com.tay.taysecurity.android.component.services

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall


class TaySureCallService : InCallService() {

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        TaySureCall.taySureCall =  call
        Log.d("TAGTay","onCallAdded")
        Log.d("TAGTay", "TaySureCall : ${TaySureCall.taySureNumber}")
       // val i = Intent()
       // i.setClass(this, Call::class.java)
       // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
       // this.startActivity(i)

      /*  Handler().postDelayed({
           call?.disconnect()
            Log.d("TAGTay","disconnect")
        },2000)*/


    }
}