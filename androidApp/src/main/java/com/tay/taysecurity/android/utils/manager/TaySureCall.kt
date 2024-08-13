package com.tay.taysecurity.android.utils.manager

import android.telecom.Call
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.utils.SECURITY_EMPTY

object TaySureCall {
    var taySureCall : Call? = null
    var listContact : List<ContactShared> = ArrayList()
    var taySureNumber : String = SECURITY_EMPTY
}