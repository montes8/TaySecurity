package com.tay.taysecurity.android.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class DetailActivity : ComponentActivity() {
    companion object{
        fun newInstance(context: Context) = context.startActivity(Intent(context,DetailActivity::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}

