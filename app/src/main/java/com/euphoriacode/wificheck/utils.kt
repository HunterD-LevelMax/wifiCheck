package com.euphoriacode.wificheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

const val urlGoogle = "google.ru"
const val fileName = "Settings.ini"

const val host = "192.168.1.1"

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

fun getIpAddress(): String? {
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