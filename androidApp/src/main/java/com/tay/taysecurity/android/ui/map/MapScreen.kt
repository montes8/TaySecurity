package com.tay.taysecurity.android.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.model.TayLocationModelObserver
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import androidx.core.graphics.createBitmap

private const val TAG = "MapScreen"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapScreen(viewModel: MapViewModel) {

    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-11.99405732, -77.06241231), 12f)
    }

    if (viewModel.uiStateMap.loadMap){
        val observerMap = TayLocationModelObserver()
        viewModel.uiStateMap.locationModel.forEach {
            observerMap.listMarker.add(rememberMarkerState(position = LatLng(it.latitude.toDouble(),
                it.longitude.toDouble())))
            observerMap.recipes.add(it)
        }

        Box(Modifier.fillMaxSize()) {
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
                MarkerInfoWindowContent(
                    state = it,
                    onClick = markerClick,
                    draggable = true,
                    icon = getBitmapDescriptorFromVector(context)
                ) {
                    Column (modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(
                            text = "Auto",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.primary),
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "seguimiento",
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
private fun getBitmapDescriptorFromVector(context: Context): BitmapDescriptor {
    val vectorDrawable: Drawable = context.getDrawable(R.drawable.ic_map_custom)!!
    val h = (60 * context.resources.displayMetrics.density).toInt()
    val w = (60 * context.resources.displayMetrics.density).toInt()
    vectorDrawable.setBounds(0, 0, w, h)
    val bm = createBitmap(w, h)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}