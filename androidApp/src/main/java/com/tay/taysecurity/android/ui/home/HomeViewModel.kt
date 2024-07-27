package com.tay.taysecurity.android.ui.home

import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.usecases.BlockingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel()  {

    private val blockingUseCase: BlockingUseCase = BlockingUseCase()


    fun insertContactAll(list : List<ContactShared>){
        execute {
             blockingUseCase.insertContactAll(list)
        }
    }
}