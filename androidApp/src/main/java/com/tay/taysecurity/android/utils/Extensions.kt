package com.tay.taysecurity.android.utils

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
import androidx.core.content.ContextCompat.startActivity
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
    val intent = Intent(Intent.ACTION_CALL_BUTTON)
    this.startActivity(intent)
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

inline fun <reified T> parseFromObjet( value: String): T {
    val jsonData = Gson()
    return jsonData.fromJson(Uri.decode(value), object : TypeToken<T>() {}.type)
}

inline fun <reified T> parseFromString( value: T): String {
    val jsonData = Gson()
    return Uri.encode(jsonData.toJson(value))
}


fun Uri.uiTayMetaDataImage(context : Context): UITayMetaDataImage {
    var uiTayData = UITayMetaDataImage()
    var nameImage = this.getRealPathFromURI(context)
    Log.d("metadata",nameImage.toString())
    try {
        var exifInterface : ExifInterface? = null
        val imgFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            nameImage.toString()
        )
        val imgFileTwo = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            nameImage.toString()
        )
        val imgFileFour = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "Camera/$nameImage"
        )
        try {
            exifInterface  = ExifInterface(imgFile.path)
        }catch (e: Exception){
            e.printStackTrace()
        }

        if(exifInterface == null){
            try {
                exifInterface  = ExifInterface(imgFileTwo.path)
            }catch (e: Exception){
           e.printStackTrace()
            }
        }

        if(exifInterface == null){
            try {
                exifInterface  = ExifInterface(imgFileFour.path)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        exifInterface?.let {
            uiTayData =  UITayMetaDataImage(
                length  = it.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?: UI_TAY_EMPTY,
                width = it.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?: UI_TAY_EMPTY,
                dateTime  = it.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?: UI_TAY_EMPTY,
                take  = it.getAttribute(ExifInterface.TAG_MAKE)?: UI_TAY_EMPTY,
                model  = it.getAttribute(ExifInterface.TAG_MODEL)?: UI_TAY_EMPTY,
                orientation  = it.getAttribute(ExifInterface.TAG_ORIENTATION)?: UI_TAY_EMPTY,
                whiteBalance  = it.getAttribute(ExifInterface.TAG_WHITE_BALANCE)?: UI_TAY_EMPTY,
                focalLength  = it.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?: UI_TAY_EMPTY,
                flash  = it.getAttribute(ExifInterface.TAG_FLASH)?: UI_TAY_EMPTY,
                gpsDatesTamp  = it.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)?: UI_TAY_EMPTY,
                gpsTimesTamp  = it.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)?: UI_TAY_EMPTY,
                gpsLatitude  = it.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?: UI_TAY_EMPTY,
                gpsLatitudeReferential  = it.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)?: UI_TAY_EMPTY,
                gpsLongitude  = it.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?: UI_TAY_EMPTY,
                gpsLongitudeReferential  = it.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)?: UI_TAY_EMPTY,
                gpsProcessingMethod  = it.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)?: UI_TAY_EMPTY
            )
        }
      Log.d("metadata",uiTayData.toString())
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return uiTayData
}

fun Uri.getRealPathFromURI(context: Context): String? {
    var thePath: String? = "no-path-found"
    val filePathColumn = arrayOf<String?>(MediaStore.Images.Media.DISPLAY_NAME)
    val cursor: Cursor? = context.contentResolver.query(this, filePathColumn, null, null, null)
    if (cursor?.moveToFirst() == true) {
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        thePath = cursor.getString(columnIndex)
    }
    cursor?.close()
    return thePath
}

fun Context.modeDeveloperAndMockLocation(): Boolean{
    return Settings.Secure.getInt(this.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0

}

fun Context.validateLocationMock(): Boolean {
    return !Settings.Secure.getString(this.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
}

fun Context.validateCallPredeterminate(): Boolean{
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        return this.packageName == telecomManager.defaultDialerPackage
}

fun  Context.validateSmsPredeterminate(): Boolean{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager.isRoleHeld(RoleManager.ROLE_SMS)
    } else {
        Telephony.Sms.getDefaultSmsPackage(this) == this.packageName
    }
}
