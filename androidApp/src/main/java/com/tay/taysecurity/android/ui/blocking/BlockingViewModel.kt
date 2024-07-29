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
    var update = SecurityShared()
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
        when(type){
            0->{
                update.blockingCallFull = value
                update.blockingCall = value
            }
            1->{
                update.blockingCall = value
            }
            2->{
                update.blockingSmsFull = value
                update.blockingSms = value
            }
            else->{
                update.blockingSms = value
            }
        }
        val updateBLocking = SecurityShared(blockingCallFull = update.blockingCallFull,
            blockingCall = update.blockingCall,
            blockingSmsFull = update.blockingSmsFull,
            blockingSms = update.blockingSms)
        uiState = uiState.copy(securty = updateBLocking)

    }
}