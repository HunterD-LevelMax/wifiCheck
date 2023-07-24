package com.euphoriacode.wificheck.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.euphoriacode.wificheck.*
import com.euphoriacode.wificheck.data.DataSettings
import com.euphoriacode.wificheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var seekBar: SeekBar
    private lateinit var dataSettings: DataSettings

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSettings()
        seekBar()
        saveButton()

        binding.myIpAddress.text = getIpByHostName()
    }

    override fun onResume() {
        super.onResume()
        setSettings()
    }

    private fun setSettings() {

        dataSettings = loadData(this)

        binding.apply {
            checkBoxGoogleIp.isChecked = dataSettings.setGoogleUrl
            editTextIpAddress.setText(dataSettings.ipAddress)
            switchSound.isChecked = dataSettings.sound
            switchVibration.isChecked = dataSettings.vibration
            switchPushNotice.isChecked = dataSettings.notice
            seekBarDelayPing.progress = (dataSettings.delayPing / 60000L).toInt()
            checkBoxPingPerSec.isChecked = dataSettings.checkPingPerSec
            updateDelayPingTitle((dataSettings.delayPing / 60000L).toInt())
        }
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
    }

    private fun saveButton() {
        val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

        binding.apply {
            buttonSave.setOnClickListener {
                try {
                    binding.apply {
                        dataSettings = DataSettings(
                            setGoogleUrl = checkBoxGoogleIp.isChecked,
                            ipAddress = editTextIpAddress.text.toString(),
                            sound = switchSound.isChecked,
                            vibration = switchVibration.isChecked,
                            notice = switchPushNotice.isChecked,
                            delayPing = seekBarDelayPing.progress.toLong() * 60000L,
                            checkPingPerSec = checkBoxPingPerSec.isChecked
                        )
                    }
                    saveFileData(dataSettings, path)
                    showToast(getString(R.string.save_success))
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(getString(R.string.error))
                }
            }
        }
    }
}