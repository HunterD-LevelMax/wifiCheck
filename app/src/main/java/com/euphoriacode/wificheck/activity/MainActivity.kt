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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var jobPing: Job? = null
    private lateinit var dataSettings: DataSettings

    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter

    private var isPingStopped = true

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
                    setIp()
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

    private fun stopPing() {
        binding.buttonPing.text = getString(R.string.ping)
        isPingStopped = false
        jobPing?.cancel()
    }

    private fun setIp() {
        val ipAddress = if (binding.enterIp.text.isNotEmpty()) {
            binding.enterIp.text.toString()
        } else if (dataSettings.setGoogleUrl) {
            urlGoogle
        } else {
            dataSettings.ipAddress
        }

        startPing(ipAddress)
    }

    private fun startPing(ip: String) {
        val delayPing = if (dataSettings.checkPingPerSec) 1000L else dataSettings.delayPing
        isPingStopped = true

        jobPing?.cancel() // Отменяем предыдущую работу корутины (если есть)

        jobPing = CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.Main) {
                if (!dataSettings.checkPingPerSec) {
                    showToast("Ping every ${dataSettings.delayPing / 60000L} minute")
                } else {
                    showToast("Ping every second")
                }
            }

            withContext(Dispatchers.IO){


                    while (isPingStopped) {
                        pingMessage(ip).performPing()
                        delay(delayPing)
                    }



            }
        }
    }

    private fun pingMessage(ip: String): PingTask {
        var ipAddress = ""

        ipAddress = try {
            Inet4Address.getByName(ip).toString()
        }catch (e:Exception){
            "error"
        }

        val pingTask = PingTask(ip, object : PingTask.PingListener {
            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    "PING from $ipAddress time=${time}ms"
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
            binding.logRecyclerView.scrollToPosition(logList.size - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        dataSettings = loadData(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        jobPing?.cancel() // Отменяем работу корутины при уничтожении активности
    }

}
