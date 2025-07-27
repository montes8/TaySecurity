package com.tay.taysecurity.android.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tay.taysecurity.android.model.InfoModel
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.utils.SECURITY_EMPTY

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

fun Application.uiTayDeleteSMS(all: Boolean = false, utNumber: String = SECURITY_EMPTY) {
    this.contentResolver.query(
        Uri.parse("content://sms/"),
        arrayOf("_id", "thread_id", "address", "person", "date", "body"),
        null,
        null,
        null
    )?.let { c ->
        try {
            while (c.moveToNext()) {
                val id = c.getInt(0)
                val address = c.getString(2)
                if (all) {
                    this.contentResolver.delete(
                        Uri.parse("content://sms/$id"), null, null
                    )
                } else {
                    if (address == utNumber) {
                        this.contentResolver.delete(
                            Uri.parse("content://sms/$id"), null, null
                        )
                    } } }
            c.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    }

fun parseStringGsonList(jsonString: String): List<InfoModel> {
    val jsonData = Gson()
    return jsonData.fromJson(jsonString, object : TypeToken<List<InfoModel>>() {}.type)
}

inline fun <reified T> parseFromObjet( value: String): T {
    val jsonData = Gson()
    return jsonData.fromJson(Uri.decode(value), object : TypeToken<T>() {}.type)
}

inline fun <reified T> parseFromString( value: T): String {
    val jsonData = Gson()
    return Uri.encode(jsonData.toJson(value))
}