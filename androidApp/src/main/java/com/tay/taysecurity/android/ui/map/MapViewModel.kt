package com.tay.taysecurity.android.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tay.taysecurity.android.model.TayLocationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    //private val dataDBUseCase: DataDBUseCase, @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var uiStateMap by mutableStateOf(MapUiState())

    init {
        viewModelScope.launch {
            delay(1000)
            loadDetailRecipe()
        }
    }

    private fun loadDetailRecipe() {
        viewModelScope.launch(ioDispatcher) {
          //  val response = dataDBUseCase.loadRecipes()
            uiStateMap = uiStateMap.copy(locationModel = arrayListOf(),loadMap = true)
        }
    }
}