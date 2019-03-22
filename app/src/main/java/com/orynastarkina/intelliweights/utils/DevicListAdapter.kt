package com.orynastarkina.intelliweights.utils

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Oryna Starkina on 27.02.2019.
 */
class DeviceListAdapter(private val myDataset: ArrayList<DeviceListItem>, private val clickListener: DeviceItemClick):
    RecyclerView.Adapter<DeviceListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = myDataset[position].name
        holder.textView.setOnClickListener {
            clickListener.onDeviceClick(myDataset[position].device)
        }
    }

    override fun getItemCount() = myDataset.size


    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    interface DeviceItemClick {
        fun onDeviceClick(device: BluetoothDevice)
    }
}