package com.tay.taysecurity.android.utils.manager

import android.telecom.Call
import com.tay.taysecurity.model.ContactModel

object TaySureCall {

    var taySureCall : Call? = null
    var listContact : List<ContactModel> = ArrayList()
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