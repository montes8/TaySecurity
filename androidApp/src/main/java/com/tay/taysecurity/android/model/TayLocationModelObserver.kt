package com.tay.taysecurity.android.model

import com.google.maps.android.compose.MarkerState

class TayLocationModelObserver(
    var recipes  : ArrayList<TayLocationModel> = arrayListOf(),
    val listMarker : ArrayList<MarkerState> = arrayListOf()
)