package ru.unidevidlib.adg

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityVM : ViewModel() {
    val uuid = MutableLiveData("")
}