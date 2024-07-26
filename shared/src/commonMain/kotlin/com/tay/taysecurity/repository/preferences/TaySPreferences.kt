package com.tay.taysecurity.repository.preferences

import android.preference.PreferenceManager
import com.tay.taysecurity.manager.getString
import com.tay.taysecurity.manager.setString
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.repository.preferences.ITaySPreferences
import com.tay.taysecurity.utils.PREFERENCE_DATA_SECURITY


class TaySPreferences(private val context: PreferenceManager):
    ITaySPreferences {

    override fun saveDataSecurity(value : String ) = context.setString(PREFERENCE_DATA_SECURITY,value)

    override fun getDataSecurity(): SecurityShared?{
        return retrieveSavedString().toDataSecurity()

    }

    private fun retrieveSavedString() = context.getString(PREFERENCE_DATA_SECURITY)


    private fun String.toDataSecurity(): SecurityShared? {
        return try {
            Gson().fromJson(this, SecurityShared::class.java)
        }catch (e:Exception){
            null
        }

    }
}