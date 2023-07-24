package com.euphoriacode.wificheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.euphoriacode.wificheck.data.DataSettings
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

    const val urlGoogle = "google.ru"
    const val fileName = "Settings.ini"

    fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        //this.finish()
    }

    fun getInternetStatus(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun Activity.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun getDefaultSettings(context: Context): DataSettings {
        return DataSettings(
            setGoogleUrl = false,
            ipAddress = context.getString(R.string.defaultIp),
            sound = true,
            vibration = true,
            notice = true,
            delayPing = 1000L,
            checkPingPerSec = true
        )
    }

    fun loadData(context: Context): DataSettings {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

        return if (checkFile(fileName, path)) {
            Gson().fromJson(
                readFile("$path/$fileName", StandardCharsets.UTF_8),
                DataSettings::class.java
            )
        } else {
            getDefaultSettings(context)
        }
    }

    fun saveFileData(dataSettings: DataSettings, path: String) {
        val json = Gson().toJson(dataSettings)
        val file = File(path, fileName)
        val output: Writer
        output = BufferedWriter(FileWriter(file))
        output.write(json.toString())
        output.close()
    }

    fun getIpByHostName(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                    return address.hostAddress
                }
            }
        }
        return "connect to Wi-Fi"
    }

    fun checkFile(fileName: String, path: String): Boolean {
        val file =
            File("$path/$fileName")
        return file.exists() && !file.isDirectory
    }

    fun readFile(path: String, encoding: Charset): String {
        return Files.readAllLines(Paths.get(path), encoding)[0]
    }