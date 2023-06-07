package com.euphoriacode.wificheck.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.euphoriacode.wificheck.adapter.PingAdapter
import com.euphoriacode.wificheck.databinding.ActivityMainBinding
import com.euphoriacode.wificheck.ping.PingTask
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pingTask: PingTask
    private var job: Job? = null

    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val host = "ya.ru"
        // Инициализация RecyclerView и адаптера

        pingAdapter = PingAdapter(logList)
        binding.logRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.logRecyclerView.adapter = pingAdapter

        pingTask = PingTask(host, object : PingTask.PingListener {
            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    "reply from $host time=$time ms"
                } else {
                    "error ping"
                }
                addLogMessage(logMessage) // Добавляем лог в список
            }
        })

        binding.buttonSettings.setOnClickListener {
            job?.cancel() // Отменяем предыдущую работу корутины (если есть)
            job = CoroutineScope(Dispatchers.Main).launch {
                repeat(10) {
                    pingTask.performPing()
                    delay(2000L) // Задержка в 1 секунду
                }
            }
        }
    }

    private fun addLogMessage(message: String) {
        runOnUiThread {
            logList.add(message)
            pingAdapter.notifyItemInserted(logList.size - 1)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Отменяем работу корутины при уничтожении активности
    }
}
