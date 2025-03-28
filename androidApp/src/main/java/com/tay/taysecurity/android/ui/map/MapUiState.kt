package com.tay.taysecurity.android.ui.map

import com.tay.taysecurity.android.model.TayLocationModel

data class MapUiState(
    var locationModel: List<TayLocationModel>  = arrayListOf(),
    val  loadMap : Boolean = false
)