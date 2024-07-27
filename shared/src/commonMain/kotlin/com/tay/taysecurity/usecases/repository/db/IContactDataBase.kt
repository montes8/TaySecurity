package com.tay.taysecurity.usecases.repository.db

import com.tay.taysecurity.model.ContactShared

interface IContactDataBase {
    suspend fun insertContact(user: ContactShared) :Boolean
    suspend fun getContactAll(): List<ContactShared>
    suspend fun deleteContactAll()
}