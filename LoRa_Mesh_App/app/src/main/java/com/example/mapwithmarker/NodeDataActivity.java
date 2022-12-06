package com.example.mapwithmarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

public class NodeDataActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    ArrayList<String> listItem = new ArrayList<>();
//    ArrayList<BluetoothDevice> devices;
    ArrayAdapter<String> adapter;

    private BLEservice.MyBinder mybinder;
    private BLEservice service;
    private Intent startBLEservice;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic Data;
    private BluetoothGattCharacteristic Request;
    private BluetoothGattCharacteristic Ready;

    private String address = null;
    private String data = null;

    private UUID service_UUID =
            UUID.fromString("8b85189a-69d4-11ed-a1eb-0242ac120002");
    private  UUID data_UUID =
            UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private UUID request_UUID =
            UUID.fromString("335f99aa-71a5-11ed-a1eb-0242ac120002");
    private UUID ready_UUID =
            UUID.fromString("4ee61168-71a5-11ed-a1eb-0242ac120002");

    private boolean connected = false;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mybinder = (BLEservice.MyBinder) binder;
            service = mybinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("debug","received data from service");
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent){
        data = intent.getStringExtra("data_content");
        Log.d("debug", String.format("data to show on view: %s",data));
        if(data.length()<17){
            return;
        }
        int id = (data.charAt(0)-'0')*10+(data.charAt(1)-'0');
        Log.d("debug",String.format("id:%d",id));
        if(0<id && id<5){
            if(data.charAt(2)=='T'){
                int t = (data.charAt(3)-'0')*1000+(data.charAt(4)-'0')*100+(data.charAt(5)-'0')*10+(data.charAt(6)-'0');
                listItem.set(3*(id-1),String.format("Node "+id+" Temperature: %.2f C",(float)t/100));
            }
            if(data.charAt(7)=='P'){
                int p = (data.charAt(8)-'0')*1000+(data.charAt(9)-'0')*100+(data.charAt(10)-'0')*10+(data.charAt(11)-'0');
                listItem.set(3*(id-1)+2,String.format("Node "+id+" Air pressure: %.2f kPa",(float)p/10));
            }
            if(data.charAt(12)=='H'){
                int h = (data.charAt(13)-'0')*1000+(data.charAt(14)-'0')*100+(data.charAt(15)-'0')*10+(data.charAt(16)-'0');
                listItem.set(3*(id-1)+1,String.format("Node "+id+" Humidity: %.2f %%",(float)h/100));
            }
            adapter.notifyDataSetChanged();
            }

//        }

    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_data_alt);

        for(int k=1;k<5;k++){
            listItem.add("Node "+k+" Temperature:");
            listItem.add("Node "+k+" Humidity:");
            listItem.add("Node "+k+" Air pressure:");
        }

        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItem);

        listView.setAdapter(adapter);

        Intent intent = getIntent();
        address = intent.getStringExtra("device_address");
        startBLEservice = new Intent(this, BLEservice.class);
        startBLEservice.putExtra("device_address",address);
        bindService(intent, conn, BIND_AUTO_CREATE);
        startService(startBLEservice);
        registerReceiver(gattUpdateReceiver, new IntentFilter(BLEservice.ACTION_BROADCAST));
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(gattUpdateReceiver);
        unbindService(conn);
        stopService(startBLEservice);
    }
}