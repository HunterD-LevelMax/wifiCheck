package com.appeuphoria.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appeuphoria.wificheck.data.Data
import com.appeuphoria.wificheck.R
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.Writer
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


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

fun getDefaultSettings(context: Context): Data {
    return Data(
        setGoogleUrl = false,
        ipAddress = context.getString(R.string.defaultIp),
        sound = true,
        vibration = true,
        notice = true,
        delayPing = 1000L,
        checkPingPerSec = true
    )
}

fun loadData(context: Context): Data {
    val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

    return if (checkFile(fileName, path)) {
        Gson().fromJson(
            readFile("$path/$fileName", StandardCharsets.UTF_8),
            Data::class.java
        )
    } else {
        getDefaultSettings(context)
    }
}

fun saveFileData(data: Data, path: String) {
    val json = Gson().toJson(data)
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
    return "try connect to Wi-Fi"
}

fun checkFile(fileName: String, path: String): Boolean {
    val file =
        File("$path/$fileName")
    return file.exists() && !file.isDirectory
}

fun readFile(path: String, encoding: Charset): String {
    val file = File(path)
    val inputStream = FileInputStream(file)
    val reader = BufferedReader(InputStreamReader(inputStream, encoding))
    val stringBuilder = StringBuilder()
    var line: String? = reader.readLine()
    while (line != null) {
        stringBuilder.append(line).append("\n")
        line = reader.readLine()
    }
    reader.close()
    return stringBuilder.toString()
}