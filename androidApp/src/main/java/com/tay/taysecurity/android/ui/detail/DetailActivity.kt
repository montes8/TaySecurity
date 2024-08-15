package com.tay.taysecurity.android.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.tay.taysecurity.android.utils.MyApplicationTheme

class DetailActivity : ComponentActivity() {
    companion object{
        fun newIntance(context: Context) = context.startActivity(Intent(context,DetailActivity::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApplicationTheme{
            ScreemTest()
        }
        }

    }
}

@Composable
fun ScreemTest(){
    Column {
        Text (text = "TAYSEGUIDAD")
    }
}