package com.tay.taysecurity.manager

import platform.Foundation.NSUserDefaults
import platform.darwin.NSObject

actual typealias PreferenceManager = NSObject

actual fun PreferenceManager.getInt(key: String) : Int {
    return NSUserDefaults.standardUserDefaults.integerForKey(key).toInt()
}

actual fun PreferenceManager.setInt(key: String, value : Int){
    NSUserDefaults.standardUserDefaults.setInteger(value.toLong(),key)
}

actual fun PreferenceManager.getString(key: String) : String {
    return NSUserDefaults.standardUserDefaults.stringForKey(key)?:""
}

actual fun PreferenceManager.setString(key: String, value : String){
    NSUserDefaults.standardUserDefaults.setObject(value,key)
}

actual fun PreferenceManager.getBoolean(key: String) : Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey(key)
}

actual fun PreferenceManager.setBoolean(key: String, value : Boolean){
    NSUserDefaults.standardUserDefaults.setBool(value,key)
}