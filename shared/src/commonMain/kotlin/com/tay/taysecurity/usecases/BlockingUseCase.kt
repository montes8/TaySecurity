package com.tay.taysecurity.usecases

import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.repository.db.ContactDataBase
import com.tay.taysecurity.usecases.repository.db.IContactDataBase

class BlockingUseCase {

    private val iContactDataBase : IContactDataBase = ContactDataBase()

    suspend fun getContactAll() : List<ContactShared>{
        return iContactDataBase.getContactAll()
    }

    suspend fun insertContactAll(list: List<ContactShared>): Boolean {
        iContactDataBase.deleteContactAll()
        list.forEach { iContactDataBase.insertContact(it)}
        return true
    }


}