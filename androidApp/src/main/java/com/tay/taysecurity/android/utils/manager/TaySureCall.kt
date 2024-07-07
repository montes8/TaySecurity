package com.tay.taysecurity.android.utils.manager

import android.telecom.Call

object TaySureCall {

    var taySureCall : Call? = null
    var taySureNumber : String = ""

    fun taySureFinalizeCall(){
        taySureCall?.disconnect()
        taySureCall = null
    }

    fun taySureRejectCall(){
        taySureCall?.reject(false,"")
        taySureCall = null
    }

    fun taySureAcceptCall(){
        taySureCall?.let {
            it.answer(it.details.videoState)
            taySureCall = null
        }

    }
}