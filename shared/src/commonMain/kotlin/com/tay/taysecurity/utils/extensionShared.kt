package com.tay.taysecurity.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T>uiTayJsonToObjet(json: String): T {
    val jsonData = Gson()
    return jsonData.fromJson(json,object : TypeToken<T>(){}.type)
}
