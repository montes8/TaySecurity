package com.tay.taysecurity.android.ui.stealth

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tay.taysecurity.android.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StealthLockScreen(
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val today = remember { LocalDate.now().dayOfWeek }
    var targetTaps by remember(context, today) {
        mutableIntStateOf(calculateTargetTaps(context, today))
    }

    var tlTaps by remember { mutableIntStateOf(0) }
    var trTaps by remember { mutableIntStateOf(0) }
    var blTaps by remember { mutableIntStateOf(0) }
    var brTaps by remember { mutableIntStateOf(0) }

    fun resetState() {
        tlTaps = 0
        trTaps = 0
        blTaps = 0
        brTaps = 0
        targetTaps = calculateTargetTaps(context, today)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val isLeft = offset.x < size.width / 2f
                    val isTop = offset.y < size.height / 2f

                    if (isLeft && isTop) {
                        tlTaps++
                    } else if (isTop) {
                        trTaps++
                    } else if (isLeft) {
                        blTaps++
                    } else {
                        brTaps++
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_sure),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 15.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            val isValid = validateDayRule(
                                dayOfWeek = today,
                                targetTaps = targetTaps,
                                tl = tlTaps,
                                tr = trTaps,
                                bl = blTaps,
                                br = brTaps
                            )

                            if (isValid) {
                                onUnlockSuccess()
                            } else {
                                resetState()
                            }
                        },
                        onLongPress = {
                            resetState()
                        }
                    )
                }
        )
    }
}

private fun calculateTargetTaps(context: Context, today: DayOfWeek): Int {
    val now = LocalTime.now()
    val is24Hour = DateFormat.is24HourFormat(context)
    val pattern = if (is24Hour) "HHmm" else "hhmm"
    val timeString = now.format(DateTimeFormatter.ofPattern(pattern, Locale.US))
    val initialSum = timeString.sumOf { it.digitToInt() }

    var reducedSum = initialSum
    while (reducedSum >= 10) {
        reducedSum = reducedSum.toString().sumOf { it.digitToInt() }
    }

    val minRequiredForDay = when (today) {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY -> 1
        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY -> 2
        DayOfWeek.SUNDAY -> 4
    }
    return maxOf(reducedSum, minRequiredForDay)
}

private fun validateDayRule(
    dayOfWeek: DayOfWeek,
    targetTaps: Int,
    tl: Int,
    tr: Int,
    bl: Int,
    br: Int
): Boolean {
    val total = tl + tr + bl + br
    if (total != targetTaps) return false

    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> tl == targetTaps && tr == 0 && bl == 0 && br == 0
        DayOfWeek.TUESDAY -> tr == targetTaps && tl == 0 && bl == 0 && br == 0
        DayOfWeek.WEDNESDAY -> bl == targetTaps && tl == 0 && tr == 0 && br == 0
        DayOfWeek.THURSDAY -> br == targetTaps && tl == 0 && tr == 0 && bl == 0
        DayOfWeek.FRIDAY -> tl >= 1 && tr >= 1 && bl == 0 && br == 0
        DayOfWeek.SATURDAY -> bl >= 1 && br >= 1 && tl == 0 && tr == 0
        DayOfWeek.SUNDAY -> tl >= 1 && tr >= 1 && bl >= 1 && br >= 1
    }
}





