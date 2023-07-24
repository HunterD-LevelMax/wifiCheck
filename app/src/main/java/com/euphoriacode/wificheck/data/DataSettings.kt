package com.euphoriacode.wificheck.data

data class DataSettings(
    var setGoogleUrl: Boolean,
    var ipAddress: String,
    var sound: Boolean,
    var vibration: Boolean,
    var notice: Boolean,
    var delayPing: Long,
    var checkPingPerSec: Boolean

)
