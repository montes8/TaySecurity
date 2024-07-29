package com.tay.taysecurity.android.ui.blocking


import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.android.ui.home.blocking.SegurityUiState
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.TaySureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class BlockingViewModel @Inject constructor(private val context: Application): BaseViewModel()  {

    private var taySureUseCase: TaySureUseCase = TaySureUseCase(context)
    var uiState by mutableStateOf(SegurityUiState())
    init {
        execute {
            delay(500)
            loadSegurity()

        }
    }



    fun loadSegurity() {
        execute {
            val data = taySureUseCase.getDataSecurity()?: SecurityShared()
            uiState = uiState.copy(securty = SecurityShared())

        }
    }

    fun updateDataSecurity(value: Boolean,type:Int = 0){
        val updateState = uiState.securty
        when(type){
            0->{
                updateState.blockingCallFull = value
                updateState.blockingCall = value
            }
            1->{
                updateState.blockingCall = value
            }
            2->{
                updateState.blockingSmsFull = value
                updateState.blockingSms = value
            }
            else->{
                updateState.blockingSms = value
            }
        }
        uiState = uiState.copy(securty = updateState)
    }
}