package com.tay.taysecurity.repository.preferences


import com.google.gson.Gson
import com.tay.taysecurity.manager.TaySPreferenceManager
import com.tay.taysecurity.manager.getString
import com.tay.taysecurity.manager.setString
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.repository.preferences.ITaySPreferences
import com.tay.taysecurity.utils.PREFERENCE_DATA_SECURITY

class TaySPreferences(private val preference: TaySPreferenceManager):
    ITaySPreferences {

    override fun saveDataSecurity(value: SecurityShared){
        val securityShared = Gson().toJson(value)
        preference.setString(PREFERENCE_DATA_SECURITY,securityShared)
    }

    override fun getDataSecurity(): SecurityShared?{
        return retrieveSavedString().toDataSecurity()
    }

    private fun retrieveSavedString() = preference.getString(PREFERENCE_DATA_SECURITY)

    private fun String.toDataSecurity(): SecurityShared? {
        return try {
            if (!this.trim().startsWith("{")) return null
            Gson().fromJson(this, SecurityShared::class.java)
        } catch (e: Exception) {
            null
        }
    }
}