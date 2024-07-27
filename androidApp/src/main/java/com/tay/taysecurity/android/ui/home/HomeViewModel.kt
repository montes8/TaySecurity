package com.tay.taysecurity.android.ui.home

import android.app.Activity
import android.content.Context
import android.util.Log
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.model.ContactShared
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.BlockingUseCase
import com.tay.taysecurity.usecases.TaySureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel()  {

    private val blockingUseCase: BlockingUseCase = BlockingUseCase()
    private var taySureUseCase: TaySureUseCase? = null

    fun insertContactAll(list : List<ContactShared>){
        execute {
             blockingUseCase.insertContactAll(list)
        }
    }


    fun testSure(context: Activity){
        taySureUseCase = TaySureUseCase(context)
        val a = SecurityShared(
             blockingCallFull  = true,
         blockingCall = true,
         blockingSmsFull = true,
         blockingSms  = true
        )
        execute {
            taySureUseCase?.saveDataSecurity(a)
            val data = taySureUseCase?.getDataSecurity()
            Log.d("TAGTay","$data")
        }
    }
}