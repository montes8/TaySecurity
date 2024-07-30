package com.tay.taysecurity.android.ui.home.blocking


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.TaySureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class BlockingViewModel @Inject constructor(private val context: Application): BaseViewModel()  {

    private var taySureUseCase: TaySureUseCase = TaySureUseCase(context)
    var uiState by mutableStateOf(SegurityUiState())
    private var update : SecurityShared? = null
    init {
        execute {
            delay(500)
            loadSecurity()
        }
    }



    private fun loadSecurity() {
        execute {
            val data = taySureUseCase.getDataSecurity()?: SecurityShared()
            update = SecurityShared(blockingCallFull = data.blockingCallFull,
                blockingCall = data.blockingCall,
                blockingSmsFull = data.blockingSmsFull,
                blockingSms = data.blockingSms)
            uiState = uiState.copy(securty = data)

        }
    }

    fun updateDataSecurity(value: Boolean,type:Int = 0){
        when(type){
            0->{
                update?.blockingCallFull = value
                update?.blockingCall = value
            }
            1->{
                update?.blockingCall = value
            }
            2->{
                update?.blockingSmsFull = value
                update?.blockingSms = value
            }
            else->{
                update?.blockingSms = value
            }
        }
        val updateBLocking = SecurityShared(blockingCallFull = update?.blockingCallFull?:false,
            blockingCall = update?.blockingCall?:false,
            blockingSmsFull = update?.blockingSmsFull?:false,
            blockingSms = update?.blockingSms?:false)
        uiState = uiState.copy(securty = updateBLocking)
        taySureUseCase.saveDataSecurity(updateBLocking)
    }
}