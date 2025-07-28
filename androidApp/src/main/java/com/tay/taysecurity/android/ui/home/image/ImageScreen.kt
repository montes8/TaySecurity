package com.tay.taysecurity.android.ui.home.image

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.utils.UI_TAY_EMPTY
import com.tay.taysecurity.android.utils.uiTayMetaDataImage
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ImageScreen(navController: NavHostController){
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val textData = remember { mutableStateOf(UI_TAY_EMPTY) }

    PermissionManager.CheckFilePermission(context) { isGranted ->
       //not implement
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        textData.value = imageUri?.uiTayMetaDataImage(context).toString()
    }

    Scaffold(
        topBar = {
            TopBarBack {
                navController.popBackStack()
            }
        }
    ){

        Column (modifier = Modifier.background(Color.Black)
            .fillMaxSize().padding(16.dp),
            horizontalAlignment =Alignment.CenterHorizontally){

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .height(56.dp).fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.Magenta),
                colors = ButtonDefaults.buttonColors(backgroundColor =
                    Color.Black)

                ,onClick = {
                    launcher.launch("image/*")
                }) {
                Text(text = "seleccione imagen", color = Color.White)
            }
            AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(16.dp).padding(top = 20.dp, start = 24.dp, end = 24.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
            if (textData.value.isNotEmpty()){
                Text(
                    text = textData.value,
                    color = Color.Magenta,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                )
            }

        }

    }
}


object PermissionManager {

    @Composable
    fun CheckFilePermission(context: Context,onClick: (Boolean) -> Unit){
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { isGranted ->
                if (
                    isGranted[Manifest.permission.READ_EXTERNAL_STORAGE] == true

                ) {
                    onClick.invoke(true)
                }else{
                    onClick.invoke(false)
                }
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri = String.format(
                    Locale.ENGLISH,
                    "package:%s",
                    context.packageName
                ).toUri()
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            } else {
                onClick.invoke(true)

            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                SideEffect {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            } else {
                onClick.invoke(true)
            }
        }
    }
}