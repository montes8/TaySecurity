package com.tay.taysecurity.model

import com.tay.taysecurity.utils.SECURITY_EMPTY

data class ContactShared  (
    var id :Long = 0,
    var name :String = SECURITY_EMPTY,
    var phoneNumber :String = SECURITY_EMPTY
)