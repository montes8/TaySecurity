package com.tay.taysecurity.android.ui.home

import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.model.ContactModel
import com.tay.taysecurity.usecases.BlockingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel()  {

    private val blockingUseCase: BlockingUseCase = BlockingUseCase()

    fun insertContactAll(list : List<ContactModel>){
        execute {
             blockingUseCase.insertContactAll(list)
        }
    }
}