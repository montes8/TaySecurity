package com.tay.taysecurity.usecases

import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.repository.ContactDataBase
import com.tay.taysecurity.usecases.repository.IContactDataBase

class BlockingUseCase {

    private val iUserDataBase : IContactDataBase = ContactDataBase()

    suspend fun login(user : String , pass : String) : ContactModel{
      //  return iUserDataBase.getLogin(user,pass)
        return ContactModel()
    }
}