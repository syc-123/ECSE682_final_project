package com.example.mapwithmarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    ArrayList<String> listItem = new ArrayList<>();
    ArrayList<BluetoothDevice> devices;
    ArrayAdapter<String> adapter;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothLeScanner bluetoothLeScanner = null;


    private boolean scanning = false;
    private Handler mHandler = null;

    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @SuppressLint({"MissingPermission"})
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    String s = result.getDevice().getName();
                    if(s!=null){
                        Log.d("debug", String.format("name: %s", s));
                    }
                }

                @SuppressLint("MissingPermission")
                @Override
                public void onBatchScanResults (List<ScanResult> results) {
                    listItem.clear();
                    devices = new ArrayList<>();
                    for (ScanResult result : results) {
                        devices.add(result.getDevice());
                        listItem.add(result.getDevice().getName());

                        String s = result.getDevice().getName();
                        if(s!=null){
                            Log.d("debug", String.format("name: %s", s));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.d("debug", "error");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.mHandler = new Handler();

        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItem);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String device_name = adapter.getItem(position);
                int idx = listItem.indexOf(device_name);
                String address = devices.get(idx).getAddress();
                Intent intent = new Intent(getApplicationContext(), NodeDataActivity.class);
                intent.putExtra("device_address", address);
                startActivity(intent);
            }
        });

        listView.setAdapter(adapter);
    }

    protected void onDestroy (){
        super.onDestroy();
    }

    public void MapWindow(View v){
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (scanning) {
            scanning = false;
            scanLeDevice(false);
        } else {
            scanning = true;
            Log.d("debug","start scanning");
            scanLeDevice(true);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(scanning){
            scanning = false;
            scanLeDevice(false);
        }
    }


    @SuppressLint({"MissingPermission"})
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            ArrayList<ScanFilter> filters = new ArrayList<>();

            ScanFilter filterName1 = new ScanFilter.Builder().setDeviceName("Thunderboard #45947").build();
            filters.add(filterName1);
            ScanFilter filterName2 = new ScanFilter.Builder().setDeviceName("LoRa Mesh Node 1").build();
            filters.add(filterName2);
            ScanFilter filterName3 = new ScanFilter.Builder().setDeviceName("LoRa Mesh Node 2").build();
            filters.add(filterName3);
            ScanFilter filterName4 = new ScanFilter.Builder().setDeviceName("LoRa Mesh Node 3").build();
            filters.add(filterName4);
            ScanFilter filterName5 = new ScanFilter.Builder().setDeviceName("LoRa Mesh Node 4").build();
            filters.add(filterName5);

            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).setReportDelay(50).build();

            Log.d("debug", "start");

            bluetoothLeScanner.startScan(filters, settings, mLeScanCallback);

        } else {
            Log.d("debug", "stop");
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }
}