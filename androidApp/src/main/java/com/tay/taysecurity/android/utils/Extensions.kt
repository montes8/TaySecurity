package com.tay.taysecurity.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

fun Context.tayToast(message : String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.uiTayDialedNumber(number : String = "999999999", key : String = "tel:"){
    try {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("$key$number"))
        this.startActivity(intent)
    } catch (e: SecurityException) {
        Log.e("UI_TAY_TAG_ERROR",e.message.toString())
    }
}


fun Context.uiTayViewDialedNumber(number : String = "999999999",key : String = "tel:"){
    val uri = Uri.parse("$key$number")
    val intent = Intent(Intent.ACTION_CALL, uri)
    try {
        this.startActivity(intent)
    } catch (e: SecurityException) {
        Log.e("UI_TAY_TAG_ERROR",e.message.toString())
    }
}