package com.tay.taysecurity.android.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tay.taysecurity.android.utils.MyApplicationTheme

class MapActivity : ComponentActivity() {

    var viewModel : MapViewModel? = null
    companion object{
        fun newInstance(context: Context) = context.startActivity(Intent(context,MapActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MapViewModel()
        setContent {
            MyApplicationTheme {
                viewModel?.let {
                    MapScreen(it)
                }
            }
        }
    }
}

