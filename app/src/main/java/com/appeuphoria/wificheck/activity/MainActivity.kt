package com.appeuphoria.wificheck.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appeuphoria.utility.getInternetStatus
import com.appeuphoria.utility.loadData
import com.appeuphoria.utility.replaceActivity
import com.appeuphoria.utility.showToast
import com.appeuphoria.utility.urlGoogle
import com.appeuphoria.wificheck.adapter.PingAdapter
import com.appeuphoria.wificheck.data.Data
import com.appeuphoria.wificheck.network.PingTask
import com.appeuphoria.wificheck.R
import com.appeuphoria.wificheck.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private var coroutineJob: Job? = null
    private lateinit var data: Data
    private val logList: MutableList<String> = mutableListOf()
    private lateinit var pingAdapter: PingAdapter
    private var stoppedPing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        createNotificationChannel()

        data = loadData(this)
        initAdapter()
        initButtons()

        if (!getInternetStatus(this@MainActivity)) {
            showToast("Check your internet connection")
        }

    }

    private fun initButtons() {
        activityMainBinding.apply {
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
        activityMainBinding.buttonPing.text = getString(R.string.ping)
        stoppedPing = false
        coroutineJob?.cancel()
    }

    private fun setIp() {
        val ipAddress = if (activityMainBinding.enterIp.text.isNotEmpty()) {
            activityMainBinding.enterIp.text.toString()
        } else if (data.setGoogleUrl) {
            urlGoogle
        } else {
            data.ipAddress
        }

        startPing(ipAddress)
    }

    private fun startPing(ip: String) {
        val delayPing = if (data.checkPingPerSec) 1000L else data.delayPing
        stoppedPing = true

        coroutineJob?.cancel() // Отменяем предыдущую работу корутины (если есть)

        coroutineJob = CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.Main) {
                if (!data.checkPingPerSec) {
                    showToast("Ping every ${data.delayPing / 60000L} minute")
                } else {
                    showToast("Ping every second")
                }
            }

            withContext(Dispatchers.IO) {

                while (stoppedPing) {
                    pingMessage(ip).performPing()
                    delay(delayPing)
                }

            }
        }
    }

    private var errorNotificationSent = false

    private fun pingMessage(ip: String): PingTask {
        var ipAddress = ""
        ipAddress = try {
            Inet4Address.getByName(ip).toString()
        } catch (e: Exception) {
            if (!errorNotificationSent) {
                if (data.notice) {
                    sendErrorNotification(ip)
                }
                errorNotificationSent = true
            }
            "error"
        }

        val pingTask = PingTask(ip, object : PingTask.PingListener {
            override fun onResult(success: Boolean, time: Long) {
                val logMessage = if (success) {
                    errorNotificationSent = false
                    "PING from $ipAddress time=${time}ms"
                } else {
                    if (!errorNotificationSent) {
                        if (data.notice) {
                            sendErrorNotification(ip)
                        }
                        errorNotificationSent = true
                    }
                    "PING $ip error"
                }
                addLogMessage(logMessage)
            }
        })
        return pingTask
    }


    private fun initAdapter() {
        pingAdapter = PingAdapter(logList)
        activityMainBinding.logRecyclerView.layoutManager = LinearLayoutManager(this)
        activityMainBinding.logRecyclerView.adapter = pingAdapter
    }

    private fun addLogMessage(message: String) {
        runOnUiThread {
            logList.add(message)
            pingAdapter.notifyItemInserted(logList.size - 1)
            activityMainBinding.logRecyclerView.scrollToPosition(logList.size - 1)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ping_error_channel"
            val channelName = "Ping Errors"
            val channelDescription = "Channel for displaying ping errors"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendErrorNotification(ip: String) {
        val channelId = "ping_error_channel"
        val notificationId = 123
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_error)
            .setContentTitle("Ping Error")
            .setContentText("Error ping from $ip")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onResume() {
        super.onResume()
        data = loadData(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineJob?.cancel()
    }

}
