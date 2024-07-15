package com.tay.taysecurity.repository

import com.tay.taysecurity.manager.databaseDriverFactory
import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.usecases.repository.IContactDataBase
import database.ContactSureQueries


class ContactDataBase(private val queries:ContactSureQueries = databaseDriverFactory().contactSureQueries): IContactDataBase {

    override suspend fun insertContact(user: ContactModel): Boolean {
    return true
    }

    override suspend fun getContactAll(): List<ContactModel> {
        return arrayListOf()
    }

    override suspend fun deleteContactAll() {

    }
}