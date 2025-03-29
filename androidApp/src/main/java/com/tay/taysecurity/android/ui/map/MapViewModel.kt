package com.tay.taysecurity.android.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.usecases.repository.TayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
) : BaseViewModel() {

    val tayDataUseCase: TayDataUseCase = TayDataUseCase()
    var uiStateMap by mutableStateOf(MapUiState())

    init {
        execute {
            delay(500)
            loadDetailRecipe()
        }
    }

    private fun loadDetailRecipe() {
        execute {
              val response = tayDataUseCase.loadLocations()
            uiStateMap = uiStateMap.copy(locationModel = response,loadMap = true)
        }
    }
}