package com.tay.taysecurity.android.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MmsReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_DATE_CHANGED -> {
                //not implementation
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                //not implementation
            }
        }
    }
}