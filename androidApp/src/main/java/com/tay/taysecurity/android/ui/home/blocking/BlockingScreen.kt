package com.tay.taysecurity.android.ui.home.blocking

import android.app.Activity
import android.app.role.RoleManager
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
import com.tay.taysecurity.android.component.TayCardItemSwitch
import com.tay.taysecurity.android.utils.validateCallPredeterminate
import com.tay.taysecurity.android.utils.validateSmsPredeterminate

@Composable
fun BlockingScreen() {
    val context = LocalContext.current
    val viewModel: BlockingViewModel = hiltViewModel()

    var pendingCallIndex by remember { mutableStateOf<Int?>(null) }
    var pendingSmsIndex by remember { mutableStateOf<Int?>(null) }

    val callRoleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pendingCallIndex?.let { index ->
                viewModel.updateDataSecurity(true, index)
            }
        }
        pendingCallIndex = null
    }

    val smsRoleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pendingSmsIndex?.let { index ->
                viewModel.updateDataSecurity(true, index)
            }
        }
        pendingSmsIndex = null
    }

    fun requestCallRole(index: Int) {
        if (context.validateCallPredeterminate()) {
            viewModel.updateDataSecurity(true, index)
        } else {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager != null && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                pendingCallIndex = index
                callRoleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER))
            } else {
                viewModel.updateDataSecurity(true, index)
            }
        }
    }

    fun requestSmsRole(index: Int) {
        // Si ya es la app predeterminada, simplemente actualizamos el estado al valor que el usuario activó
        if (context.validateSmsPredeterminate()) {
            viewModel.updateDataSecurity(true, index)
        } else {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager != null && !roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                pendingSmsIndex = index
                smsRoleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS))
            } else {
                viewModel.updateDataSecurity(true, index)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_blocking_view),
            color = Color.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight = FontWeight.Bold
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
        TayCardItemSwitch(
            state = viewModel.uiState.security.blockingCallFull,
            text = stringResource(R.string.title_all_call),
            subText = stringResource(R.string.sub_title_all_call)
        ) { isChecked ->
            if (isChecked) requestCallRole(0) else viewModel.updateDataSecurity(false, 0)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(
            state = viewModel.uiState.security.blockingCall,
            text = stringResource(R.string.title_stranger_call),
            subText = stringResource(R.string.sub_title_stranger_call)
        ) { isChecked ->
            if (isChecked) requestCallRole(1) else viewModel.updateDataSecurity(false, 1)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(
            state = viewModel.uiState.security.blockingSmsFull,
            text = stringResource(R.string.title_all_sms),
            subText = stringResource(R.string.sub_title_all_sms)
        ) { isChecked ->
            if (isChecked) requestSmsRole(2) else viewModel.updateDataSecurity(false, 2)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(
            state = viewModel.uiState.security.blockingSms,
            text = stringResource(R.string.title_stranger_sms),
            subText = stringResource(R.string.sub_title_stranger_sms)
        ) { isChecked ->
            if (isChecked) requestSmsRole(3) else viewModel.updateDataSecurity(false, 3)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}