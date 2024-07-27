package com.tay.taysecurity.repository.db

import com.tay.taysecurity.manager.databaseDriverFactory
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.usecases.repository.db.IContactDataBase
import database.ContactEntity
import database.ContactSureQueries


class ContactDataBase(private val queries:ContactSureQueries = databaseDriverFactory().contactSureQueries)
    : IContactDataBase {

    override suspend fun insertContact(user: ContactShared): Boolean {
        queries.insertContact(user.name, user.phoneNumber)
        return true
    }

    override suspend fun getContactAll(): List<ContactShared> {
        return queries.selectAllContact().executeAsList().toListContact()
    }

    override suspend fun deleteContactAll() {
        queries.deleteContactAll()
    }
}

fun List<ContactEntity>.toListContact(): List<ContactShared> {
    return this.map {
        ContactShared(it.id,it.name,it.number)
    }
}