package com.tay.taysecurity.manager

import platform.Foundation.NSUserDefaults
import platform.darwin.NSObject


actual typealias TaySPreferenceManager = NSObject

actual fun TaySPreferenceManager.getInt(key: String) : Int {
    return NSUserDefaults.standardUserDefaults.integerForKey(key).toInt()
}

actual fun TaySPreferenceManager.setInt(key: String, value : Int){
    NSUserDefaults.standardUserDefaults.setInteger(value.toLong(),key)
}

actual fun TaySPreferenceManager.getString(key: String) : String {
    return NSUserDefaults.standardUserDefaults.stringForKey(key)?:""
}

actual fun TaySPreferenceManager.setString(key: String, value : String){
    NSUserDefaults.standardUserDefaults.setObject(value,key)
}

actual fun TaySPreferenceManager.getBoolean(key: String) : Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey(key)
}

actual fun TaySPreferenceManager.setBoolean(key: String, value : Boolean){
    NSUserDefaults.standardUserDefaults.setBool(value,key)
}