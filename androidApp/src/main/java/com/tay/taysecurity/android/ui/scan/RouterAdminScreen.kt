package com.tay.taysecurity.android.ui.scan

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.tay.taysecurity.android.component.TopBarBack

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RouterAdminScreen(
    gatewayIp: String,
    onBack: () -> Unit
) {
    val url = if (gatewayIp.startsWith("http")) gatewayIp else "http://$gatewayIp"

    Scaffold(
        topBar = {
            TopBarBack(
                title = "Panel Router ($gatewayIp)",
                openClick = onBack
            )
        }
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            }
        )
    }
}
