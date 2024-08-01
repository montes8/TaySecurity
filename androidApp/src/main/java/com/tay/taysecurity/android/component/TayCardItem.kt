package com.tay.taysecurity.android.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun TayCardItem(
     state : Boolean,
    text: String,
    subText: String,
    tayClickItem: (Boolean) -> Unit
) {
    val checked = remember { mutableStateOf(false) }
    checked.value = state
    Card(shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.Black,elevation = 4.dp) {
        Row{
            Column(modifier = Modifier
                .weight(4.0f)
                .padding(16.dp)) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding( top = 2.dp),
                    text = subText,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
                    color = Color.White
                )
            }

            CustomSwitch(
                height = 40.dp,
                width = 70.dp,
                circleButtonPadding = 4.dp,
                outerBackgroundOnResource = R.drawable.tay_bg_selected,
                outerBackgroundOffResource = R.drawable.tay_bg_unselected,
                circleBackgroundOnResource = R.drawable.tay_circle_white,
                circleBackgroundOffResource = R.drawable.tay_circle_white,
                stateOn = 1,
                stateOff = 0,
                initialValue = 0,
                onCheckedChanged = {}
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomSwitch(
    height: Dp,
    width: Dp,
    circleButtonPadding: Dp,
    outerBackgroundOnResource: Int,
    outerBackgroundOffResource: Int,
    circleBackgroundOnResource: Int,
    circleBackgroundOffResource: Int,
    stateOn: Int,
    stateOff: Int,
    initialValue: Int,
    onCheckedChanged: (checked: Boolean) -> Unit
) {

    val swipeableState = rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = { newState ->
            if (newState == stateOff) {
                onCheckedChanged(false)
            } else {
                onCheckedChanged(true)
            }
            true
        }
    )

    val sizePx = with(LocalDensity.current) { (width - height).toPx() }
    val anchors = mapOf(0f to stateOff, sizePx to stateOn) // Maps anchor points (in px) to states

    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .height(height)
            .width(width)
            .clip(RoundedCornerShape(height))
            .border(1.dp, Color.DarkGray, CircleShape)
            .background(Color.Transparent)
            .then(
                if (swipeableState.currentValue == stateOff) Modifier.paint(
                    painterResource(id = outerBackgroundOffResource),
                    contentScale = ContentScale.FillBounds
                ) else Modifier.paint(
                    painterResource(id = outerBackgroundOnResource),
                    contentScale = ContentScale.FillBounds
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
                .size(height)
                .padding(circleButtonPadding)
                .clip(RoundedCornerShape(50))
                .then(
                    if (swipeableState.currentValue == stateOff) Modifier.paint(
                        painterResource(id = circleBackgroundOffResource),
                        contentScale = ContentScale.FillBounds
                    ) else Modifier.paint(
                        painterResource(id = circleBackgroundOnResource),
                        contentScale = ContentScale.FillBounds
                    )
                )
                .clickable {
                    scope.launch {

                        if (swipeableState.currentValue == stateOff) {
                            swipeableState.animateTo(stateOn)
                        } else {
                            swipeableState.animateTo(stateOff)
                        }

                    }


                }
        )
    }


}