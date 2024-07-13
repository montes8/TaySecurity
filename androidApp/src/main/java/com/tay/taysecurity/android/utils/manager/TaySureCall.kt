package com.tay.taysecurity.android.utils.manager

import android.telecom.Call
import com.tay.taysecurity.android.model.ContactPhone

object TaySureCall {

    var taySureCall : Call? = null
    var listContact : List<ContactPhone> = ArrayList()
    var taySureNumber : String = ""
    var tayAppView : Boolean = false

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