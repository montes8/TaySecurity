package com.tay.taysecurity.android.utils

import android.content.Context
import android.widget.Toast

fun Context.tayToast(message : String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}