package com.tay.taysecurity.manager

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tay.taysecurity.database.TaysecurityDb


internal actual fun databaseDriverFactory(): TaysecurityDb {
    val driver = NativeSqliteDriver(TaysecurityDb.Schema, "taysecurity.db")
    return TaysecurityDb(driver)
}