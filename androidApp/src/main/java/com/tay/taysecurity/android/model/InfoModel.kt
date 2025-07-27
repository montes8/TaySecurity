package com.tay.taysecurity.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class InfoModel(
    var title : String = "",
    var message : String = "",
):Parcelable