@file:Suppress("DEPRECATION")

package com.tay.taysecurity.android.utils

import android.annotation.SuppressLint
import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Telephony
import android.telecom.Call
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tay.taysecurity.android.model.UITayMetaDataImage
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.utils.SECURITY_EMPTY
import java.io.File
import java.io.IOException


fun Context.tayToast(message : String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.uiTayViewCallButton(){
    val contactsIntent = Intent(Intent.ACTION_VIEW).apply {
        data = ContactsContract.Contacts.CONTENT_URI
    }
    this.startActivity(contactsIntent)
}
fun Context.uiTayViewCallNumberButton() {
    val dialIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:123"))

    try {
        startActivity(dialIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.uiTayViewCall(){
    try {
        val intent = Intent()
        intent.setClass(this, Call::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    } catch (e: SecurityException) {
        Log.e("ERROR_CALL",e.message.toString())
    }
}

fun String.goCallNumber(context: Context){
    val callIntent = Intent(Intent.ACTION_CALL)
    callIntent.data = ("tel:$this").toUri()
    context.startActivity(callIntent)
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
        "content://sms/".toUri(),
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
                        "content://sms/$id".toUri(), null, null
                    )
                } else {
                    if (address == utNumber) {
                        this.contentResolver.delete(
                            "content://sms/$id".toUri(), null, null
                        )
                    } } }
            c.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

inline fun <reified T> parseFromObjet( value: String): T {
    val jsonData = Gson()
    return jsonData.fromJson(Uri.decode(value), object : TypeToken<T>() {}.type)
}

inline fun <reified T> parseFromString( value: T): String {
    val jsonData = Gson()
    return Uri.encode(jsonData.toJson(value))
}


fun Uri.uiTayMetaDataImage(context: Context): UITayMetaDataImage {
    val uriToOpen = runCatching {
        if (scheme == "content") MediaStore.setRequireOriginal(this) else this
    }.getOrDefault(this)

    return context.contentResolver.openInputStream(uriToOpen)?.use { stream ->
        val exif = ExifInterface(stream)
        val gps = FloatArray(2).also { exif.getLatLong(it) }

        UITayMetaDataImage(
            length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) ?: exif.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION) ?: UI_TAY_EMPTY,
            width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) ?: exif.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION) ?: UI_TAY_EMPTY,
            dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) ?: exif.getAttribute(ExifInterface.TAG_DATETIME) ?: UI_TAY_EMPTY,
            take = exif.getAttribute(ExifInterface.TAG_MAKE) ?: UI_TAY_EMPTY,
            model = exif.getAttribute(ExifInterface.TAG_MODEL) ?: UI_TAY_EMPTY,
            orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION) ?: UI_TAY_EMPTY,
            whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE) ?: UI_TAY_EMPTY,
            focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH) ?: UI_TAY_EMPTY,
            flash = exif.getAttribute(ExifInterface.TAG_FLASH) ?: UI_TAY_EMPTY,
            gpsDatesTamp = exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP) ?: UI_TAY_EMPTY,
            gpsTimesTamp = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP) ?: UI_TAY_EMPTY,
            gpsLatitude = gps[0].takeIf { it != 0f }?.toString() ?: (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: UI_TAY_EMPTY),
            gpsLatitudeReferential = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) ?: UI_TAY_EMPTY,
            gpsLongitude = gps[1].takeIf { it != 0f }?.toString() ?: (exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: UI_TAY_EMPTY),
            gpsLongitudeReferential = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) ?: UI_TAY_EMPTY,
            gpsProcessingMethod = exif.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD) ?: UI_TAY_EMPTY
        )
    } ?: UITayMetaDataImage()
}

fun Context.modeDeveloperAndMockLocation(): Boolean{
    return Settings.Secure.getInt(this.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0

}

fun Context.validateCallPredeterminate(): Boolean{
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        return this.packageName == telecomManager.defaultDialerPackage
}

@SuppressLint("ObsoleteSdkInt")
fun  Context.validateSmsPredeterminate(): Boolean{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager.isRoleHeld(RoleManager.ROLE_SMS)
    } else {
        Telephony.Sms.getDefaultSmsPackage(this) == this.packageName
    }
}
