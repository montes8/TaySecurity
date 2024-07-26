package com.tay.taysecurity.repository.db

import com.tay.taysecurity.manager.databaseDriverFactory
import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.usecases.repository.IContactDataBase
import database.ContactEntity
import database.ContactSureQueries


class ContactDataBase(private val queries:ContactSureQueries = databaseDriverFactory().contactSureQueries)
    : IContactDataBase {

    override suspend fun insertContact(user: ContactModel): Boolean {
        queries.insertContact(user.name, user.phoneNumber)
        return true
    }

    override suspend fun getContactAll(): List<ContactModel> {
        return queries.selectAllContact().executeAsList().toListContact()
    }

    override suspend fun deleteContactAll() {
        queries.deleteContactAll()
    }
}

fun List<ContactEntity>.toListContact(): List<ContactModel> {
    return this.map {
        ContactModel(it.id,it.name,it.number)
    }
}