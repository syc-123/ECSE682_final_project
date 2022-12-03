package com.example.mapwithmarker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BLEservice extends Service {

    Intent intent;
    private final Handler handler = new Handler();

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic Data;
    private BluetoothGattCharacteristic Request;
    private BluetoothGattCharacteristic Ready;

    public static final String ACTION_BROADCAST = "com.example.mapwithmarker.BROADCAST";

    private String address = null;

    private UUID service_UUID =
            UUID.fromString("8b85189a-69d4-11ed-a1eb-0242ac120002");
    private  UUID data_UUID =
            UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private UUID request_UUID =
            UUID.fromString("335f99aa-71a5-11ed-a1eb-0242ac120002");
    private UUID ready_UUID =
            UUID.fromString("4ee61168-71a5-11ed-a1eb-0242ac120002");

    private boolean connected = false;

    class MyBinder extends Binder {
        public BLEservice getService(){
            return BLEservice.this;
        }
    }

    private MyBinder mybinder = new MyBinder();

    public BLEservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("debug","Connection established, try discover services");
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("debug","Disconnected from device");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattService = bluetoothGatt.getService(service_UUID);
                Log.d("debug", "Service found");
                Data = bluetoothGattService.getCharacteristic(data_UUID);
                Request = bluetoothGattService.getCharacteristic(request_UUID);
                Ready = bluetoothGattService.getCharacteristic(ready_UUID);
                Log.d("debug","Characteristics found");
                connected = true;
            } else {
                Log.d("debug", "No service found");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcast(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic,
                                           int status){
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("debug", "write success");
            }
            else{
                Log.d("debug", "write failed");
            }
        }
    };

    private Runnable updateBroadcastData = new Runnable() {
        @SuppressLint("MissingPermission")
        public void run() {
            if(connected) {
                bluetoothGatt.readCharacteristic(Data);
            }
            handler.postDelayed(this, 500);
        }
    };

    public void broadcast(BluetoothGattCharacteristic characteristic){
        byte[] data_content = characteristic.getValue();
        Log.d("debug", String.format("data content: %s", new String(data_content, StandardCharsets.UTF_8)));
        intent.putExtra("data_content",new String(data_content, StandardCharsets.UTF_8));
        sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    public void onCreate() {
        super.onCreate();
        intent = new Intent(ACTION_BROADCAST);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressLint("MissingPermission")
    public int onStartCommand(Intent intent, int flag, int startId){
        address = intent.getStringExtra("device_address");
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.d("debug", "device of this address does not exist");
        }else {
            bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback);
            Log.d("debug", "Trying to create a connection.");
        }

        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);

        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    public void onDestroy(){
        bluetoothGatt.disconnect();
        handler.removeCallbacks(updateBroadcastData);
        super.onDestroy();
    }

}