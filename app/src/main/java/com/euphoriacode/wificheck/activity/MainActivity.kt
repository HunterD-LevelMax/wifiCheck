package com.euphoriacode.wificheck.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.euphoriacode.wificheck.*
import com.euphoriacode.wificheck.adapter.PingAdapter
import com.euphoriacode.wificheck.data.DataSettings
import com.euphoriacode.wificheck.databinding.ActivityMainBinding
import com.euphoriacode.wificheck.ping.PingTask
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pingTask: PingTask
    private var jobPing: Job? = null
    private lateinit var dataSettings: DataSettings

    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initAdapter()
        initPing()


        binding.buttonPing.setOnClickListener {
            ping()
        }

        binding.buttonStopPing.setOnClickListener {
            replaceActivity(SettingsActivity())
        }
    }

    private fun ping() {
        val countPing = 10
        val delayPing = 1000L // Задержка в 1 секунды

        jobPing?.cancel() // Отменяем предыдущую работу корутины (если есть)

        jobPing = CoroutineScope(Dispatchers.Main).launch {
            when (dataSettings.checkPingPerSec) {
                true -> {
                    repeat(countPing) {
                        pingTask.performPing()
                        delay(delayPing)
                    }
                }
                false -> {
                    repeat(countPing) {
                        pingTask.performPing()
                        delay(dataSettings.delayPing)
                    }
                    runOnUiThread() {
                        showToast("Ping every ${dataSettings.delayPing / 60000L} minute")
                    }
                }
            }
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
                checkPingPerSec = false
            )
        }
        return dataSettings
    }

    private fun initPing() {
        pingTask = PingTask(dataSettings.ipAddress, object : PingTask.PingListener {
            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    "PING from ${dataSettings.ipAddress} time=$time ms"
                } else {
                    "PING ${dataSettings.ipAddress} error"
                }
                addLogMessage(logMessage)
            }
        })
    }

    private fun initAdapter() {
        pingAdapter = PingAdapter(logList)
        binding.logRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.logRecyclerView.adapter = pingAdapter
    }

    private fun addLogMessage(message: String) {
        runOnUiThread {
            logList.add(message)
            pingAdapter.notifyItemInserted(logList.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobPing?.cancel() // Отменяем работу корутины при уничтожении активности
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
