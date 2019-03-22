package com.orynastarkina.intelliweights.utils

import java.util.*

/**
 * Created by Oryna Starkina on 27.02.2019.
 */

val BLE_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
val BLE_CHARACTERISTIC_UUID_TX = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
val BLE_CHARACTERISTIC_UUID_RX = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

val BLE_CHARACTERISTIC_NOTIFICATION_MODE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

val DEVICE_ARGUMENT_KEY = "device_to_connect"