package com.euphoriacode.wificheck.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.euphoriacode.wificheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}