package com.orynastarkina.intelliweights.utils

import android.bluetooth.BluetoothDevice

/**
 * Created by Oryna Starkina on 27.02.2019.
 */

data class DeviceListItem(
    val name: String,
    val device: BluetoothDevice
)