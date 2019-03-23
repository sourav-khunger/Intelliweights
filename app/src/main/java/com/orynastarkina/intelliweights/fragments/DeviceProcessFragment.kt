package com.orynastarkina.intelliweights.fragments

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orynastarkina.intelliweights.MainActivity
import java.lang.Exception
import com.orynastarkina.intelliweights.R
import com.orynastarkina.intelliweights.utils.*
import kotlinx.android.synthetic.main.fragment_device_process.*
import java.lang.StringBuilder
import java.nio.charset.Charset


/**
 * Created by Oryna Starkina on 27.02.2019.
 */
class DeviceProcessFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var gatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == STATE_CONNECTED) {
                gatt?.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            val characteristic = gatt!!.getService(BLE_SERVICE_UUID)
                .getCharacteristic(BLE_CHARACTERISTIC_UUID_RX)
            gatt.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(BLE_CHARACTERISTIC_NOTIFICATION_MODE_DESCRIPTOR)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            val characteristic = gatt!!.getService(BLE_SERVICE_UUID)
                .getCharacteristic(BLE_CHARACTERISTIC_UUID_RX)
            characteristic.value = byteArrayOf(1, 1)
            gatt.writeCharacteristic(characteristic)

            activity?.runOnUiThread {
                waitDeviceProgressbar.visibility = View.INVISIBLE
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            if (characteristic?.uuid == BLE_CHARACTERISTIC_UUID_RX) {


                val data = characteristic?.value

//                activity?.runOnUiThread {
//                    deviceDataView.text = ""
//
//                    if (data != null) {
//                        for (char in data) {
//                            deviceDataView.append(String.format("%02x", char))
//                        }
//                    }
//                }


                // send data to Java
                data?.run {
                    val dataBuilder = StringBuilder()
                    forEach {
                        dataBuilder.append(String.format("%02x", it))
                    }

                    BLEDataBridge.onBLEData(dataBuilder.toString())

                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_device_process, container, false)

    override fun onResume() {

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            showExplanation(
                this.activity as Activity,
                "Bluetooth is disable, we need reconnect to device"
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
                (activity as MainActivity).repeatSearch()
            }
        }

        if (arguments == null) {
            throw Exception("$TAG: arguments must not be null and contains BluetoothDevice")
        }

        gatt = arguments?.getParcelable<BluetoothDevice>(DEVICE_ARGUMENT_KEY)?.connectGatt(
            this.activity,
            true,
            gattCallback
        )


        activity?.runOnUiThread {
            waitDeviceProgressbar.visibility = View.VISIBLE
        }

        super.onResume()
    }

    override fun onPause() {

        gatt?.disconnect()

        super.onPause()
    }
}