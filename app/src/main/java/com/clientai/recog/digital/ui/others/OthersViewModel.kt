package com.clientai.recog.digital.ui.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OthersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "这是一个端智能手写数字识别的案例Demo。\n\n欢迎学习《端智能技术演进与实践》！"
    }
    val text: LiveData<String> = _text
}