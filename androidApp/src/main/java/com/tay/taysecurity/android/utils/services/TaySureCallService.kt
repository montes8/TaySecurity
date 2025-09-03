package com.tay.taysecurity.android.utils.services

import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.uiTayViewCall
import com.tay.taysecurity.android.utils.validNumberBlocking
import com.tay.taysecurity.usecases.BlockingUseCase
import com.tay.taysecurity.usecases.TaySureUseCase
import com.tay.taysecurity.utils.SECURITY_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TaySureCallService : InCallService() {
    private val blockingUseCase: BlockingUseCase = BlockingUseCase()
    private var taySureUseCase: TaySureUseCase? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        taySureUseCase = TaySureUseCase(application)
    }
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        TaySureCall.taySureCall =  call
         Handler(Looper.getMainLooper()).postDelayed({
             scope.launch {
                 val dataShared = taySureUseCase?.getDataSecurity()
                 val listContact = blockingUseCase.getContactAll()
                 Log.d(SECURITY_TAG,TaySureCall.taySureNumber)
                 val validNumber = validNumberBlocking(listContact,TaySureCall.taySureNumber)
                 Log.d(SECURITY_TAG,validNumber.toString())
                 if (dataShared?.blockingCallFull==true || dataShared?.blockingCall==true
                     && validNumber){
                     call?.disconnect()
                 }else{ this@TaySureCallService.uiTayViewCall()}
             }
          },1000)
    }
}