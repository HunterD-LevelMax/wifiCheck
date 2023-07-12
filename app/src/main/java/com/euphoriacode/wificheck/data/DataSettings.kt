package com.euphoriacode.wificheck.data

data class DataSettings(
    var ipAddress: String,
    var sound: Boolean,
    var vibration: Boolean,
    var notice: Boolean,
    var delayPing: Long,
    var checkPingPerSec: Boolean,
    var googleUrl: Boolean
)
