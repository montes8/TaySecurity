package com.tay.taysecurity.manager



expect class TaySPreferenceManager

expect fun TaySPreferenceManager.getInt(key: String) : Int
expect fun TaySPreferenceManager.setInt(key: String, value: Int)
expect fun TaySPreferenceManager.getString(key: String) : String
expect fun TaySPreferenceManager.setString(key: String, value: String)
expect fun TaySPreferenceManager.getBoolean(key: String) : Boolean
expect fun TaySPreferenceManager.setBoolean(key: String, value: Boolean)