package com.tay.taysecurity.manager

import android.preference.PreferenceManager


expect class PreferenceManager

expect fun PreferenceManager.getInt(key: String) : Int
expect fun PreferenceManager.setInt(key: String, value: Int)
expect fun PreferenceManager.getString(key: String) : String
expect fun PreferenceManager.setString(key: String, value: String)
expect fun PreferenceManager.getBoolean(key: String) : Boolean
expect fun PreferenceManager.setBoolean(key: String, value: Boolean)