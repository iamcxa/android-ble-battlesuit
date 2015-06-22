package com.geodoer.bluetoothcontroler.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iamcx_000 on 2015/6/22.
 */
public class BleDeviceListAdapder extends BaseAdapter {

    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;

    public BleDeviceListAdapder(LayoutInflater mInflator)
    {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        this.mInflator = mInflator;
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null)
        {
            view = mInflator.inflate(com.geodoer.bluetoothcontroler.R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(com.geodoer.bluetoothcontroler.R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(com.geodoer.bluetoothcontroler.R.id.device_name);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(com.geodoer.bluetoothcontroler.R.string.unknown_device);

        viewHolder.deviceAddress.setText(device.getAddress());
        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public interface whenAction{

    }
}
