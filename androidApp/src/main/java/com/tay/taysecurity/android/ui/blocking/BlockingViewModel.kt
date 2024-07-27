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
    init {
        execute {
            delay(500)
            loadSegurity()

        }
    }

    var uiState by mutableStateOf(SegurityUiState())

    fun loadSegurity() {
        execute {
            val data = taySureUseCase.getDataSecurity()?: SecurityShared()
            uiState = uiState.copy(securty = data)

        }
    }
}