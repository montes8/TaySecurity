package com.tay.taysecurity.android.utils.services

import android.os.Handler
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.uiTayViewCall
import com.tay.taysecurity.android.utils.validNumberBlocking
import com.tay.taysecurity.usecases.BlockingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TaySureCallService : InCallService() {
    private val blockingUseCase: BlockingUseCase = BlockingUseCase()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        TaySureCall.taySureCall =  call
        Log.d("TAGTay", "disconnect")
         Handler().postDelayed({
             scope.launch {
                 TaySureCall.listContact = blockingUseCase.getContactAll()
                 if (validNumberBlocking(TaySureCall.listContact,TaySureCall.taySureNumber)){
                     call?.disconnect()
                     Log.d("TAGTay", "disconnect")
                 }else{
                     this@TaySureCallService.uiTayViewCall()
                 }
             }

          },1000)
    }
}