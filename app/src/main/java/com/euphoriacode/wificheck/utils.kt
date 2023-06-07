package com.euphoriacode.wificheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

val googleAddres = "https://www.google.ru"
val localIp = "192.168.88.1"

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    //this.finish()
}


