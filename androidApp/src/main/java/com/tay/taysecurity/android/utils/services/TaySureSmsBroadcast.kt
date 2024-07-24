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


class TaySureSmsBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val messageBody = smsMessage.messageBody

                Log.d("TAGTay","$messageBody")
                Log.d("TAGTay","${smsMessage.originatingAddress}")
                Handler().postDelayed({
                    delete(smsMessage.originatingAddress?:"")
                },1000)

            }
            Log.d("TAGTay","messageBody")
        }


    }

    fun delete(thread: String) {
        val c: Cursor = appContextTaySure.contentResolver.query(
            Uri.parse("content://sms/"),
            arrayOf<String>("_id", "thread_id", "address", "person", "date", "body"),
            null,
            null,
            null
        )!!
        Log.d("TAGTay","delete")
        try {
            while (c.moveToNext()) {
                val id = c.getInt(0)
                val address = c.getString(2)
                if (address == thread) {
                    appContextTaySure.contentResolver.delete(
                        Uri.parse("content://sms/$id"), null, null
                    )
                }
            }
            c.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAGTay","Exception")
        }
    }
}