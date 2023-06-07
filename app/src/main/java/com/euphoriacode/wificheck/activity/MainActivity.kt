package com.euphoriacode.wificheck.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.euphoriacode.wificheck.databinding.ActivityMainBinding
import com.euphoriacode.wificheck.replaceActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonSettings.setOnClickListener {
            replaceActivity(SettingsActivity())
        }

    }









}