package com.euphoriacode.wificheck.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.euphoriacode.wificheck.*
import com.euphoriacode.wificheck.adapter.PingAdapter
import com.euphoriacode.wificheck.data.DataSettings
import com.euphoriacode.wificheck.databinding.ActivityMainBinding
import com.euphoriacode.wificheck.ping.PingTask
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var jobPing: Job? = null
    private lateinit var dataSettings: DataSettings

    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataSettings = loadData(this)
        initAdapter()
        initButtons()

    }

    private fun initButtons() {
        binding.apply {
            buttonPing.setOnClickListener {
                if (buttonPing.text == getString(R.string.ping)) {
                    ping()
                    buttonPing.text = getString(R.string.stop)
                } else {
                    stopPing()
                }
            }
            buttonSettings.setOnClickListener {
                replaceActivity(SettingsActivity())
            }
        }
    }

    private fun ping() {
        if (dataSettings.setGoogleUrl) {
            startPing(urlGoogle)
        } else {
            startPing(dataSettings.ipAddress)
        }
    }

    private fun stopPing() {
        binding.buttonPing.text = getString(R.string.ping)
        jobPing?.cancel()
    }

    private fun startPing(ip: String) {
        val countPing = 2
        val delayPing = if (dataSettings.checkPingPerSec) 1000L else dataSettings.delayPing

        pingMessage(ip) // инициализация PingTask
        jobPing?.cancel() // Отменяем предыдущую работу корутины (если есть)

        jobPing = CoroutineScope(Dispatchers.Main).launch {
            repeat(countPing) {
                pingMessage(ip).performPing()
                delay(delayPing)
            }

            if (!dataSettings.checkPingPerSec) {
                withContext(Dispatchers.Main) {
                    showToast("Ping every ${dataSettings.delayPing / 60000L} minute")
                }
            }

            stopPing()
        }
    }

    fun getIpByHostName(ip: String): InetAddress {
        kotlin.run {
            return Inet4Address.getByName(ip)
        }
    }

    private fun pingMessage(ip: String): PingTask {
        val pingTask = PingTask(ip, object : PingTask.PingListener {

            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    "PING from ${getIpByHostName(ip)} time=${time}ms"
                } else {
                    "PING $ip error"
                }
                addLogMessage(logMessage)
            }
        })
        return pingTask
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
        showToast("Load settings")
        dataSettings = loadData(this)
    }
}
