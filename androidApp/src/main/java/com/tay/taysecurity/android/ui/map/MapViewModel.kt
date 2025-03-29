package com.tay.taysecurity.android.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.usecases.repository.TayDataUseCase
import kotlinx.coroutines.delay


class MapViewModel (
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