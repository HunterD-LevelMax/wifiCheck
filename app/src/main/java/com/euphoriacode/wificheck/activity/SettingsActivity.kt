package com.euphoriacode.wificheck.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.euphoriacode.wificheck.data.DataSettings
import com.euphoriacode.wificheck.databinding.ActivitySettingsBinding
import com.euphoriacode.wificheck.getIpAddress
import com.euphoriacode.wificheck.showToast

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var seekBar: SeekBar
    private lateinit var dataSettings: DataSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seekBar()
        saveDataSettings()
        binding.myIpAddress.text = getIpAddress()
    }

    private fun seekBar() {
        seekBar = binding.seekBarDelayPing

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { updateDelayPingTitle(it.progress) }
            }
        })
    }

    private fun updateDelayPingTitle(progress: Int) {
        binding.titleDelayPing.text = "Check connection every $progress minute"
        showToast(progress.toString())
    }

    private fun saveDataSettings() {
        binding.apply {
            buttonSave.setOnClickListener {
                try {
                    if (editTextIpAddress.text.isNotEmpty()) {
                        dataSettings = DataSettings(
                            editTextIpAddress.text.toString(),
                            switchSound.isChecked,
                            switchVibration.isChecked,
                            switchPushNotice.isChecked,
                            seekBarDelayPing.progress.toString()
                        )
                        showToast("Save success")
                    } else {
                        showToast("Enter ip address")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun loadData(): DataSettings {
        return dataSettings
    }


}