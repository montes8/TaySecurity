package com.tay.taysecurity.android.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.tay.taysecurity.android.model.ContactPhone

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

fun Application.loadContactUser(): List<ContactPhone>{
    val contacts : ArrayList<ContactPhone> = ArrayList()
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
                    ContactPhone( name = name, phoneNumber = phone,
                        initialLetter =name.substring(0,1) )
                )
            }
        } while (cursor.moveToNext())

    cursor?.close()
    return contacts
}
