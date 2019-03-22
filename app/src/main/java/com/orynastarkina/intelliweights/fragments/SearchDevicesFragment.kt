package com.orynastarkina.intelliweights.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orynastarkina.intelliweights.MainActivity
import com.orynastarkina.intelliweights.R
import com.orynastarkina.intelliweights.utils.DeviceListAdapter
import com.orynastarkina.intelliweights.utils.DeviceListItem
import com.orynastarkina.intelliweights.utils.showExplanation
import kotlinx.android.synthetic.main.fragment_search_device.*

/**
 * Created by Oryna Starkina on 27.02.2019.
 */
class SearchDeviceFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private val REQUEST_BLUETOOTH_PERMISSIONS_CODE = 3
    private val REQUEST_ENABLE_BLUETOOTH_CODE = 4

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var deviceListAdapter: RecyclerView.Adapter<*>

    private val devices = ArrayList<DeviceListItem>()

    private val scanResult = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                Log.i(TAG, "scanning: $result")
                if (it.device?.name != null) {

                    if (devices.any { device -> device.name == device.device.name }) {
                        return@let
                    }

                    devices.add(DeviceListItem(it.device.name, it.device))
                    deviceListAdapter.notifyDataSetChanged()

                    if (devicesList.visibility == View.INVISIBLE) {
                        devicesList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search_device, container, false)

        deviceListAdapter = DeviceListAdapter(
            devices,
            object : DeviceListAdapter.DeviceItemClick {
                override fun onDeviceClick(device: BluetoothDevice) {
                    stopDeviceScanning()
                    (activity as MainActivity).onDeviceFound(device)
                }

            })

        view.findViewById<RecyclerView>(R.id.devicesList).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SearchDeviceFragment.context)
            adapter = deviceListAdapter
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!isPermissionsGranted()) {
            startRequestingPermissions()
        } else {
            if (isBluethoothEnabled()) {
                startDeviceScanning()
            }
            requestEnableBluetooth()
        }

        return view
    }

    private fun startDeviceScanning() {
        if (!isPermissionsGranted()) return

        if (bluetoothAdapter != null) {
            val scan = (bluetoothAdapter as BluetoothAdapter).bluetoothLeScanner
            val settings = ScanSettings.Builder()
            settings.setReportDelay(0)
            settings.setScanMode(ScanSettings.SCAN_MODE_BALANCED)

            scan.startScan(null, settings.build(), scanResult)
        }
    }

    private fun stopDeviceScanning() {
        if (bluetoothAdapter != null) {
            val scan = (bluetoothAdapter as BluetoothAdapter).bluetoothLeScanner
            scan.stopScan(scanResult)
        }
    }

    private fun isPermissionsGranted() =
        (ContextCompat.checkSelfPermission(this.activity as Activity, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.activity as Activity, Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.activity as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSIONS_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isEmpty() || grantResults.any { it != PackageManager.PERMISSION_GRANTED })) {
                    showExplanation(
                        this.activity as Activity,
                        "Permissions needed for app work"
                    ) { dialogInterface, _ ->
                        dialogInterface.cancel()
                        startRequestingPermissions()
                    }
                } else {
                    if (isBluethoothEnabled()) {
                        startDeviceScanning()
                    } else {
                        requestEnableBluetooth()
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BLUETOOTH_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    startDeviceScanning()
                } else {
                    showExplanation(
                        this.activity as Activity,
                        "Please, enable bluetooth for app work."
                    ) { dialogInterface, _ ->
                        dialogInterface.cancel()
                        startRequestingPermissions()
                    }
                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startRequestingPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_BLUETOOTH_PERMISSIONS_CODE
        )
    }

    private fun requestEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH_CODE)
    }

    private fun isBluethoothEnabled() = bluetoothAdapter != null && (bluetoothAdapter?.isEnabled == true)
}