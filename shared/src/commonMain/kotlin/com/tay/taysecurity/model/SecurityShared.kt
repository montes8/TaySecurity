package com.tay.taysecurity.model

data class SecurityShared  (
    var blockingCallFull :Boolean = false,
    var blockingCall :Boolean = false,
    var blockingSmsFull :Boolean = false,
    var blockingSms :Boolean = false,
    var simulationGps :Boolean = false
){
    fun uiTayBlockingCall() = blockingCallFull || blockingCall

    fun uiTayBlockingSms() = blockingSmsFull || blockingSms
}