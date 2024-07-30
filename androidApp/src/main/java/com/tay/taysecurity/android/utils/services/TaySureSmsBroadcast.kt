package com.tay.taysecurity.android.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import android.util.Log
import com.tay.taysecurity.android.application.TaySecurityApplication.Companion.appContextTaySure
import com.tay.taysecurity.android.utils.uiTayDeleteSMS
import com.tay.taysecurity.usecases.TaySureUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class TaySureSmsBroadcast: BroadcastReceiver() {


    private var taySureUseCase: TaySureUseCase? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val messageBody = smsMessage.messageBody
                 taySureUseCase = TaySureUseCase(appContextTaySure)
                val dataShared = taySureUseCase?.getDataSecurity()
                Log.d("TAGTay","$messageBody")
                Log.d("TAGTay","${smsMessage.originatingAddress}")
                Handler().postDelayed({
               if (dataShared?.blockingSmsFull== true || dataShared?.blockingSms== true ){
                   appContextTaySure.uiTayDeleteSMS(
                       dataShared.blockingSmsFull,
                       smsMessage.originatingAddress?:"")
                }
            },1000)

            }
            Log.d("TAGTay","messageBody")
        }
    }
}