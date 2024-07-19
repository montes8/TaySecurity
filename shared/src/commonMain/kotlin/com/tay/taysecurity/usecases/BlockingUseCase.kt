package com.tay.taysecurity.usecases

import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.repository.ContactDataBase
import com.tay.taysecurity.usecases.repository.IContactDataBase

class BlockingUseCase {

    private val iContactDataBase : IContactDataBase = ContactDataBase()

    suspend fun getContactAll() : List<ContactModel>{
        return iContactDataBase.getContactAll()
    }

    suspend fun insertContactAll(list: List<ContactModel>): Boolean {
        iContactDataBase.deleteContactAll()
        list.forEach { iContactDataBase.insertContact(it)}
        return true
    }


}