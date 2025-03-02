package com.appeuphoria.wificheck.activity

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.appeuphoria.utility.getIpByHostName
import com.appeuphoria.utility.loadData
import com.appeuphoria.utility.saveFileData
import com.appeuphoria.utility.showToast
import com.appeuphoria.wificheck.data.Data
import com.appeuphoria.wificheck.R
import com.appeuphoria.wificheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var seekBar: SeekBar
    private lateinit var data: Data

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSettings()
        seekBar()
        saveButton()

        binding.myIpAddress.text = getIpByHostName()

        binding.buttonShowLogs.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        setSettings()
    }

    private fun setSettings() {

        data = loadData(this)

        binding.apply {
            checkBoxGoogleIp.isChecked = data.setGoogleUrl
            editTextIpAddress.setText(data.ipAddress)
            switchSound.isChecked = data.sound
            switchVibration.isChecked = data.vibration
            switchPushNotice.isChecked = data.notice
            seekBarDelayPing.progress = (data.delayPing / 60000L).toInt()
            checkBoxPingPerSec.isChecked = data.checkPingPerSec
            updateDelayPingTitle((data.delayPing / 60000L).toInt())
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

    @SuppressLint("SetTextI18n")
    private fun updateDelayPingTitle(progress: Int) {
        binding.titleDelayPing.text = "Check connection every $progress minute"
    }

    private fun saveButton() {
        val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

        binding.apply {
            buttonSave.setOnClickListener {
                try {
                    binding.apply {
                        data = Data(
                            setGoogleUrl = checkBoxGoogleIp.isChecked,
                            ipAddress = editTextIpAddress.text.toString(),
                            sound = switchSound.isChecked,
                            vibration = switchVibration.isChecked,
                            notice = switchPushNotice.isChecked,
                            delayPing = seekBarDelayPing.progress.toLong() * 60000L,
                            checkPingPerSec = checkBoxPingPerSec.isChecked
                        )
                    }
                    saveFileData(data, path)
                    showToast(getString(R.string.save_success))
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(getString(R.string.error))
                }
            }
        }
    }
}