package ru.unidevidlib.adg

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ru.unidevid.lib.UdidManager
import ru.unidevidlib.adg.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val vm by viewModels<MainActivityVM>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = vm
    }

    override fun onResume() {
        super.onResume()
        vm.uuid.postValue(UdidManager.getUUID())
    }
}