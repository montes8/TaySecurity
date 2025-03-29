package com.tay.taysecurity.android.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.model.TayLocationModelObserver

private const val TAG = "MapScreen"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapScreen(viewModel: MapViewModel) {

    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-11.99405732, -77.06241231), 15f)
    }
    viewModel.loadDetailRecipe()
    if (viewModel.uiStateMap.loadMap){
        val observerMap = TayLocationModelObserver()
        viewModel.uiStateMap.locationModel.forEach {
            observerMap.listMarker.add(rememberMarkerState(position = LatLng(it.latitude.toDouble(),
                it.longitude.toDouble())))
            observerMap.recipes.add(it)
        }

        Box(Modifier.fillMaxSize().background(Color.White)) {
            GoogleMapView(
                observerMap,
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    isMapLoaded = true
                },
            )
            if (!isMapLoaded) {
                AnimatedVisibility(
                    modifier = Modifier
                        .matchParentSize(),
                    visible = !isMapLoaded,
                    enter = EnterTransition.None,
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}


@Composable
fun GoogleMapView(
    observerMap : TayLocationModelObserver,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val mapVisible by remember { mutableStateOf(true) }

    if (mapVisible) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = onMapLoaded
        ) {
            val markerClick: (Marker) -> Boolean = {
                cameraPositionState.projection?.let { projection ->
                    Log.d(TAG, "The current projection is: $projection")
                }
                false
            }

            observerMap.listMarker.forEachIndexed { index, it ->
                if (index == observerMap.recipes.size -1){
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(observerMap.recipes[index].latitude.toDouble(),
                        observerMap.recipes[index].longitude.toDouble()), 13f)
                }
                MarkerInfoWindowContent(
                    state = it,
                    onClick = markerClick,
                    draggable = true,
                    icon = getBitmapDescriptorFromVector(context,index == observerMap.recipes.size -1 || observerMap.recipes.size == 1)
                ) {
                    Column (modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(
                            text = "Mi Auto",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.primary),
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Localización",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Normal,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.primary),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            content()
        }

    }
}

@SuppressLint("UseCompatLoadingForDrawables")
private fun getBitmapDescriptorFromVector(context: Context,iconPrincipal : Boolean): BitmapDescriptor {
    val vectorDrawable: Drawable = context.getDrawable(if (iconPrincipal)R.drawable.ic_map_car else R.drawable.ic_location_range)!!
    val h = ((if (iconPrincipal)60 else 25) * context.resources.displayMetrics.density).toInt()
    val w = ((if (iconPrincipal)60 else 25) * context.resources.displayMetrics.density).toInt()
    vectorDrawable.setBounds(0, 0, w, h)
    val bm = createBitmap(w, h)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}