package com.tay.taysecurity.usecases.repository

import com.tay.taysecurity.model.ContactModel

interface IContactDataBase {
    suspend fun insertContact(user: ContactModel) :Boolean
    suspend fun getContactAll(): List<ContactModel>
    suspend fun deleteContactAll()
}