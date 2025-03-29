package com.tay.taysecurity.android.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tay.taysecurity.android.model.TayLocationModel
import com.tay.taysecurity.android.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    //private val dataDBUseCase: DataDBUseCase, @IoDispatcher
   // private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    var uiStateMap by mutableStateOf(MapUiState())

    init {
        execute {
            delay(500)
            loadDetailRecipe()
        }
    }

    private fun loadDetailRecipe() {
        execute {
            //  val response = dataDBUseCase.loadRecipes()
            uiStateMap = uiStateMap.copy(locationModel =
                arrayListOf(TayLocationModel(id = "eewewe", latitude = "-11.99405732", longitude = "-77.06241231")),loadMap = true)

        }
    }
}