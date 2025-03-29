package com.tay.taysecurity.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


actual typealias TaySPreferenceManager = Application

actual fun TaySPreferenceManager.getInt(key: String ) : Int{
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    return prefs.getInt(key, -1)
}


actual fun TaySPreferenceManager.setInt(key: String, value: Int) {
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putInt(key,value)
    editor.apply()
}

actual fun TaySPreferenceManager.getString(key: String) : String{
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    return prefs.getString(key, "")?:""
}


actual fun TaySPreferenceManager.setString(key: String, value: String) {
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putString(key,value)
    editor.apply()
}

actual fun TaySPreferenceManager.getBoolean(key: String ) : Boolean{
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    return prefs.getBoolean(key, false)
}


@SuppressLint("UseKtx")
actual fun TaySPreferenceManager.setBoolean(key: String, value: Boolean) {
    val prefs: SharedPreferences = this.getSharedPreferences("shareTaySure", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putBoolean(key,value)
    editor.apply()
}
