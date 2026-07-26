package com.tay.taysecurity.android.model

import com.tay.taysecurity.android.utils.UI_TAY_EMPTY


data class UITayMetaDataImage(
    var length :String = UI_TAY_EMPTY,
    var width :String = UI_TAY_EMPTY,
    var dateTime :String = UI_TAY_EMPTY,
    var take :String = UI_TAY_EMPTY,
    var model :String = UI_TAY_EMPTY,
    var orientation :String = UI_TAY_EMPTY,
    var whiteBalance :String = UI_TAY_EMPTY,
    var focalLength :String = UI_TAY_EMPTY,
    var flash :String = UI_TAY_EMPTY,
    var gpsDatesTamp :String = UI_TAY_EMPTY,
    var gpsTimesTamp :String = UI_TAY_EMPTY,
    var gpsLatitude :String = UI_TAY_EMPTY,
    var gpsLatitudeReferential :String = UI_TAY_EMPTY,
    var gpsLongitude :String = UI_TAY_EMPTY,
    var gpsLongitudeReferential :String = UI_TAY_EMPTY,
    var gpsProcessingMethod :String = UI_TAY_EMPTY
){
    fun mapperDataImage(): String {
        var text = UI_TAY_EMPTY

        if (length.isNotEmpty() && width.isNotEmpty()) {
            text += "Tamaño: $length x $width\n"
        }

        if (take.isNotEmpty()) {
            text += "Móvil: $take $model\n"
        }

        if (dateTime.isNotEmpty()) {
            text += "Fecha: $dateTime\n"
        }

        if (focalLength.isNotEmpty()) {
            text += "Focal Length: $focalLength\n"
        }

        if (gpsLatitude.isNotEmpty()) {
            text += "Latitud: $gpsLatitude $gpsLatitudeReferential\n"
        }

        if (gpsLongitude.isNotEmpty()) {
            text += "Longitud: $gpsLongitude $gpsLongitudeReferential\n"
        }

        if (gpsProcessingMethod.isNotEmpty()) {
            text += "Proceso Medición: $gpsProcessingMethod\n"
        }

        return text.trim()
    }
}