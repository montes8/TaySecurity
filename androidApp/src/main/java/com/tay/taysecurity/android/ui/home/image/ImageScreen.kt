package com.tay.taysecurity.android.ui.home.image

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.utils.UI_TAY_EMPTY
import com.tay.taysecurity.android.utils.uiTayMetaDataImage

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ImageScreen(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val textData = remember { mutableStateOf(UI_TAY_EMPTY) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        imageUri?.let {
            textData.value = it.uiTayMetaDataImage(context).mapperDataImage()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        galleryLauncher.launch("image/*")
    }

    Scaffold(
        topBar = {
            TopBarBack {
                navController.popBackStack()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.Magenta),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                onClick = {
                    // 3. Lógica para pedir el permiso antes de abrir la galería
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_MEDIA_LOCATION
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_MEDIA_LOCATION)
                    }
                }
            ) {
                Text(text = "Seleccione imagen", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            imageUri?.let { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (textData.value.isNotEmpty()) {
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
