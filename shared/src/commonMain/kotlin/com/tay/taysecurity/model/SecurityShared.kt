package com.tay.taysecurity.model

data class SecurityShared  (
    var blockingCallFull :Boolean = false,
    var blockingCall :Boolean = false,
    var blockingSmsFull :Boolean = false,
    var blockingSms :Boolean = false
){
    fun uiTayBlockinCall() = blockingCallFull || blockingCall

    fun uiTayBlockinSms() = blockingSmsFull || blockingSms
}