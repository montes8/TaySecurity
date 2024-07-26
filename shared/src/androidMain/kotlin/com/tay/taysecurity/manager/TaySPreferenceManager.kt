package com.tay.taysecurity.manager

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.tay.taysecurity.utils.SECURITY_EMPTY


typealias TaySPreferenceManager = Activity

fun TaySPreferenceManager.getInt(key: String) : Int{
 val prefs: SharedPreferences =  this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE
 )
 return prefs.getInt(key, -1)
}
fun TaySPreferenceManager.setInt(key: String, value: Int) {
 val prefs: SharedPreferences = this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE)
 val editor = prefs.edit()
 editor.putInt(key,value)
 editor.apply()
}
fun TaySPreferenceManager.getString(key: String) : String {
 val prefs: SharedPreferences =  this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE
 )
 return prefs.getString(key, SECURITY_EMPTY)?:SECURITY_EMPTY
}
fun TaySPreferenceManager.setString(key: String, value: String) {
 val prefs: SharedPreferences = this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE)
 val editor = prefs.edit()
 editor.putString(key,value)
 editor.apply()
}
fun TaySPreferenceManager.getBoolean(key: String) : Boolean {
 val prefs: SharedPreferences =  this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE
 )
 return prefs.getBoolean(key, false)
}
fun TaySPreferenceManager.setBoolean(key: String, value: Boolean) {
 val prefs: SharedPreferences = this.getSharedPreferences("PreferenceSecurity", MODE_PRIVATE)
 val editor = prefs.edit()
 editor.putBoolean(key,value)
 editor.apply()
}