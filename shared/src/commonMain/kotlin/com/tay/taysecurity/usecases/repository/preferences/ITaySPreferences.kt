package com.tay.taysecurity.usecases.repository.preferences

import com.tay.taysecurity.model.SecurityShared

interface ITaySPreferences {

     fun saveDataSecurity(value : String )

     fun getDataSecurity(): SecurityShared?
}