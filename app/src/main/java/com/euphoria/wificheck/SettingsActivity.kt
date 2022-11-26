package com.euphoria.wificheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.euphoria.wificheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding // view binding (обращение к xml элементам без инициализации)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }




}