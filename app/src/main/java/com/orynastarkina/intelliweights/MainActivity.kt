package com.orynastarkina.intelliweights

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.orynastarkina.intelliweights.fragments.DeviceProcessFragment
import com.orynastarkina.intelliweights.fragments.SearchDeviceFragment
import com.orynastarkina.intelliweights.utils.DEVICE_ARGUMENT_KEY


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigateToFragment(this,
                SearchDeviceFragment(), addToBackStack = false, isReplace = true)
        }
    }

    fun onDeviceFound(device: BluetoothDevice){
        val bundle = Bundle()
        bundle.putParcelable(DEVICE_ARGUMENT_KEY, device)

        val processDeviceFragment = DeviceProcessFragment()

        processDeviceFragment.arguments = bundle

        navigateToFragment(this, processDeviceFragment, addToBackStack = false, isReplace = true)
    }

    fun repeatSearch() {
        navigateToFragment(this,
            SearchDeviceFragment(), addToBackStack = false, isReplace = true)
    }

    private fun navigateToFragment(
        activity: FragmentActivity,
        fragment: Fragment,
        addToBackStack: Boolean,
        isReplace: Boolean
    ) {
        val manager = activity.supportFragmentManager

        val transaction = manager.beginTransaction()

        if (isReplace) transaction.replace(R.id.container, fragment)
        else transaction.add(R.id.container, fragment)

        if (addToBackStack)
            transaction.addToBackStack(fragment.tag)

        transaction.commit()
    }
}
