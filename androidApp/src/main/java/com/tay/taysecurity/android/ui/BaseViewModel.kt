package com.tay.taysecurity.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {


    fun execute(func:suspend ()->Unit){
        viewModelScope.launch(Dispatchers.IO){
            try {
                func()
            }catch (ex:Throwable){
                  ex.printStackTrace()
            }
        }
    }
}