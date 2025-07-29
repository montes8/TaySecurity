package com.tay.taysecurity.android.ui.home.blocking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItemSwitch

@Composable
fun BlockingScreen(
) {

    val viewModel : BlockingViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth().background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(top= 12.dp, start = 8.dp, end = 8.dp, bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_blocking_view),
            color = Color.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.title_sub_blocking_view),
            color = Color.Magenta,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular))
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingCallFull,
            text = stringResource(R.string.title_all_call),
            subText = stringResource(R.string.sub_title_all_call)){
            viewModel.updateDataSecurity(it,0)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingCall,
            text = stringResource(R.string.title_stranger_call),
            subText = stringResource(R.string.sub_title_stranger_call)){
            viewModel.updateDataSecurity(it,1)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSmsFull,
            text = stringResource(R.string.title_all_sms),
            subText = stringResource(R.string.sub_title_all_sms)){
            viewModel.updateDataSecurity(it,2)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSms,
            text = stringResource(R.string.title_stranger_sms),
            subText = stringResource(R.string.sub_title_stranger_sms)){
            viewModel.updateDataSecurity(it,3)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}