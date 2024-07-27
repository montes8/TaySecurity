package com.tay.taysecurity.usecases.repository.preferences

import com.tay.taysecurity.model.SecurityShared

interface ITaySPreferences {

     fun saveDataSecurity(value : SecurityShared )

     fun getDataSecurity(): SecurityShared?
}