package com.tay.taysecurity.usecases

import com.tay.taysecurity.manager.TaySPreferenceManager
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.repository.preferences.TaySPreferences
import com.tay.taysecurity.usecases.repository.preferences.ITaySPreferences

class TaySureUseCase (private val context: TaySPreferenceManager) {

    private val iTaySPreferences : ITaySPreferences = TaySPreferences(context)

    fun getDataSecurity() : SecurityShared? {
        return iTaySPreferences.getDataSecurity()
    }

    fun saveDataSecurity(value: SecurityShared){
        iTaySPreferences.saveDataSecurity(value)
    }


}