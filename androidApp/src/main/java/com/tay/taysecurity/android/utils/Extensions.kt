package com.tay.taysecurity.android.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import com.tay.taysecurity.model.ContactShared

fun Context.tayToast(message : String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.uiTayDialedNumber(number : String = "935815994", key : String = "tel:"){
    try {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("$key$number"))
        this.startActivity(intent)
    } catch (e: SecurityException) {
        Log.e("UI_TAY_TAG_ERROR",e.message.toString())
    }
}
fun Context.uiTayViewCallButton(){
    val intent = Intent(Intent.ACTION_CALL_BUTTON)
    this.startActivity(intent)
}

fun Context.uiTayViewCall(){
    try {
        val intent = Intent()
        intent.setClass(this, Call::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    } catch (e: SecurityException) {
        Log.e("ERROR_CALL",e.message.toString())
    }
}

fun Application.loadContactUser(): List<ContactShared>{
    val contacts : ArrayList<ContactShared> = ArrayList()
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
    )

    val cursor = this.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        null,
        null,
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC")

    if(cursor?.moveToFirst() == true)
        do {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim()
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim()

            if(phone.isNotEmpty()){
                contacts.add(
                    ContactShared( name = name, phoneNumber = phone)
                )
            }
        } while (cursor.moveToNext())

    cursor?.close()
    return contacts
}


fun validNumberBlocking(list :List<ContactShared>, numberCall:String):Boolean{
    val incomingCall = numberCall.filter{it.isDigit()}.trim()
    val incomingLengthCall = incomingCall.length
    list.forEach { contact ->
      val currentNumber = contact.phoneNumber.filter {it.isDigit()}.trim()
        val currentLengthNumber = currentNumber.length
        if (currentLengthNumber >= incomingLengthCall){
            val numberBlocking = currentNumber.substring(currentLengthNumber - incomingLengthCall,currentLengthNumber) == incomingCall.substring(0,incomingLengthCall)
            if (numberBlocking) return false
        }
    }
    return true
}
