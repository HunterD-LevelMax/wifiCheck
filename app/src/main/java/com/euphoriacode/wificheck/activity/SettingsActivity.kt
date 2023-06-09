package com.euphoriacode.wificheck.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.SeekBar
import com.euphoriacode.wificheck.*
import com.euphoriacode.wificheck.data.DataSettings
import com.euphoriacode.wificheck.databinding.ActivitySettingsBinding
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.nio.charset.StandardCharsets


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var seekBar: SeekBar
    private lateinit var dataSettings: DataSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSettings(loadData())
        seekBar()
        saveButton()
        binding.myIpAddress.text = getIpAddress()
    }

    private fun saveFileData(dataSettings: DataSettings, path: String) {
        val json = Gson().toJson(dataSettings)
        val file = File(path, fileName)
        val output: Writer
        output = BufferedWriter(FileWriter(file))
        output.write(json.toString())
        output.close()
    }

    private fun setSettings(dataSettings: DataSettings) {
        binding.apply {
            editTextIpAddress.setText(dataSettings.ipAddress)
            switchSound.isChecked = dataSettings.sound
            switchVibration.isChecked = dataSettings.vibration
            switchPushNotice.isChecked = dataSettings.notice
            seekBarDelayPing.progress = (dataSettings.delayPing/60000L).toInt()
            checkBoxPingPerSec.isChecked = dataSettings.checkPingPerSec
            updateDelayPingTitle((dataSettings.delayPing/60000L).toInt())
        }
    }

    private fun loadData(): DataSettings {
        val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
        val jsonString: String

        if (checkFile(fileName, path)) {
            jsonString = readFile("$path/$fileName", StandardCharsets.UTF_8)
            dataSettings = Gson().fromJson(
                jsonString,
                DataSettings::class.java
            )
        } else {
            dataSettings = DataSettings(
                ipAddress = getString(R.string.defaultIp),
                sound = true,
                vibration = true,
                notice = true,
                delayPing = 1000L,
                false
            )
        }
        return dataSettings
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
                    if (editTextIpAddress.text.isNotEmpty()) {
                        dataSettings = DataSettings(
                            ipAddress = editTextIpAddress.text.toString(),
                            sound = switchSound.isChecked,
                            vibration = switchVibration.isChecked,
                            notice = switchPushNotice.isChecked,
                            delayPing = seekBarDelayPing.progress.toLong() * 60000L,
                            checkPingPerSec =  checkBoxPingPerSec.isChecked
                        )
                        saveFileData(dataSettings, path)
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
}