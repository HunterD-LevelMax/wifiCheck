package com.appeuphoria.wificheck.network

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class PingTask(private val host: String, private val pingListener: PingListener) {

    interface PingListener {
        fun onResult(success: Boolean, time: Long)
    }

    fun performPing() {

        try {
            val socket = Socket()
            val startTime = System.currentTimeMillis()

            socket.connect(
                InetSocketAddress(host, 80),
                5000
            ) // Установка таймаута подключения в миллисекундах

            val endTime = System.currentTimeMillis()
            val elapsedTime = endTime - startTime

            socket.close()
            pingListener.onResult(true, elapsedTime)

        } catch (e: IOException) {
            e.printStackTrace()
            pingListener.onResult(false, 0)
        }

    }
}
