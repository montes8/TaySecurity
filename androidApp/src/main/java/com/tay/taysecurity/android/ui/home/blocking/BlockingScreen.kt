package com.tay.taysecurity.android.ui.home.blocking

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.DialogSure
import com.tay.taysecurity.android.component.TayCardItemSwitch
import com.tay.taysecurity.android.utils.validateCallPredeterminate
import com.tay.taysecurity.android.utils.validateSmsPredeterminate

@Composable
fun BlockingScreen(
) {
    val context = LocalContext.current
    val viewModel : BlockingViewModel = hiltViewModel()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { isGranted ->
        }
    )
    var openDialog by remember { mutableStateOf(false) }

    DialogSure(showDialog = openDialog, dismissDialog = {
        openDialog = false
        if(it){
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            launcher.launch(intent)
        }
    })

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
            if (it){
                if(context.validateCallPredeterminate()){
                    viewModel.updateDataSecurity(true,0)
                }else{
                    openDialog = true
                }
            }else{
                viewModel.updateDataSecurity(false,0)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingCall,
            text = stringResource(R.string.title_stranger_call),
            subText = stringResource(R.string.sub_title_stranger_call)){
            if (it){
                if(context.validateCallPredeterminate()){
                    viewModel.updateDataSecurity(true,1)
                }else{
                    openDialog = true
                }
            }else{
                viewModel.updateDataSecurity(false,1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSmsFull,
            text = stringResource(R.string.title_all_sms),
            subText = stringResource(R.string.sub_title_all_sms)){
            if (it){
                if(context.validateSmsPredeterminate()){
                    viewModel.updateDataSecurity(true,2)
                }else{
                    openDialog = true
                }
            }else{
                viewModel.updateDataSecurity(false,2)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSms,
            text = stringResource(R.string.title_stranger_sms),
            subText = stringResource(R.string.sub_title_stranger_sms)){
            if (it){
                if(context.validateSmsPredeterminate()){
                    viewModel.updateDataSecurity(true,3)
                }else{
                    openDialog = true
                }
            }else{
                viewModel.updateDataSecurity(false,3)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
