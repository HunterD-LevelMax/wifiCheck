package com.euphoriacode.wificheck.data

data class DataSettings(
    var ip_address: String,
    var sound: Boolean,
    var vibration: Boolean,
    var notice: Boolean,
    var delayPing: String
)
