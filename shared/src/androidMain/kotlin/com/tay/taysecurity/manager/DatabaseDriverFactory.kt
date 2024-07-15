package com.tay.taysecurity.manager

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tay.taysecurity.database.TaysecurityDb


lateinit var appContext: Context

internal actual fun databaseDriverFactory(): TaysecurityDb {
    val driver = AndroidSqliteDriver(TaysecurityDb.Schema, appContext, "taysecurity.db")
    return TaysecurityDb(driver)
}