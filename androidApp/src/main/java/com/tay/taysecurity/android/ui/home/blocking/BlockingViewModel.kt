package com.tay.taysecurity.android.ui.home.blocking

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.android.utils.loadContactUser
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.model.SecurityShared
import com.tay.taysecurity.usecases.BlockingUseCase
import com.tay.taysecurity.usecases.TaySureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class BlockingViewModel @Inject constructor(
    private val context: Application
) : BaseViewModel() {

    private var taySureUseCase: TaySureUseCase = TaySureUseCase(context)
    private val blockingUseCase: BlockingUseCase = BlockingUseCase()

    var uiState by mutableStateOf(SecurityUiState())
        private set

    init {
        execute {
            delay(500)
            loadSecurity()
            insertContactAll()
        }
    }

    private fun loadSecurity() {
        execute {
            val data = taySureUseCase.getDataSecurity() ?: SecurityShared()
                uiState = uiState.copy(security = data)
        }
    }

    fun updateDataSecurity(value: Boolean, type: Int = 0) {
        val currentSecurity = uiState.security

        val updatedSecurity = when (type) {
            0 -> currentSecurity.copy(
                blockingCallFull = value,
                blockingCall = value
            )
            1 -> currentSecurity.copy(
                blockingCall = value
            )
            2 -> currentSecurity.copy(
                blockingSmsFull = value,
                blockingSms = value
            )
            3 -> currentSecurity.copy(
                blockingSms = value
            )
            4 -> currentSecurity.copy(
                simulationGps = value
            )
            else -> currentSecurity
        }

        uiState = uiState.copy(security = updatedSecurity)
        taySureUseCase.saveDataSecurity(updatedSecurity)
    }

    private fun insertContactAll() {
        execute {
            try {
                TaySureCall.listContact = context.loadContactUser()
                blockingUseCase.insertContactAll(TaySureCall.listContact)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}