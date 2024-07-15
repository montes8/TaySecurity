package com.tay.taysecurity.android.ui.home

import android.util.Log
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.usecases.BlockingUseCase

class HomeViewModel: BaseViewModel()  {


    private val blockingUseCase: BlockingUseCase = BlockingUseCase()

    fun insertConctacAll(list : List<ContactModel>){
        execute {
            val response = blockingUseCase.inserContactAll(list)
            Log.d("sureAdbTay","${response} guardado")
            getConctacAll()
        }
    }

    fun getConctacAll(){
        execute {
            val response = blockingUseCase.getContactAll()
            Log.d("sureAdbTay","${response}")
        }
    }



}