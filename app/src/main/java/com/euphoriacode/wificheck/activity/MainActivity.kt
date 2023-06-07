package com.euphoriacode.wificheck.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.euphoriacode.wificheck.adapter.PingAdapter
import com.euphoriacode.wificheck.databinding.ActivityMainBinding
import com.euphoriacode.wificheck.host
import com.euphoriacode.wificheck.ping.PingTask
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pingTask: PingTask
    private var jobPing: Job? = null

    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        getPingLog()

        val count = 10
        val delayPing = 1000L // Задержка в 2 секунды

        binding.buttonSettings.setOnClickListener {
            jobPing?.cancel() // Отменяем предыдущую работу корутины (если есть)
            jobPing = CoroutineScope(Dispatchers.Main).launch {
                repeat(count) {// код-во запросов
                    pingTask.performPing()
                    delay(delayPing)// Задержка в 2 секунды
                }
            }
        }
    }


    private fun getPingLog() {
        pingTask = PingTask(host, object : PingTask.PingListener {
            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    "reply from $host time=$time ms"
                } else {
                    "error ping"
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
}
