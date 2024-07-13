package com.tay.taysecurity.android.utils.services

import android.os.Handler
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.tayToast


class TaySureCallService : InCallService() {

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        TaySureCall.taySureCall =  call
         /* Handler().postDelayed({
              Log.d("TAGTay","onCallAdded")
              Log.d("TAGTay", "TaySureCall : ${TaySureCall.taySureNumber}")
              if (TaySureCall.taySureNumber == "935815994"){
                  val i = Intent()
                  i.setClass(this, Call::class.java)
                  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  this.startActivity(i)
                  Log.d("TAGTay","taySureNumber")
              }

          },1000)*/


/*
        Handler().postDelayed({
       if (TaySureCall.taySureNumber == "935815994") {
             call?.disconnect()
                Log.d("TAGTay","disconnect")
           this.tayToast("Lamada bloqueada")
          }

        },1000)*/


    }
}