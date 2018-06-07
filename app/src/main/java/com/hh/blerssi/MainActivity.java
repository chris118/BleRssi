package com.hh.blerssi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 0x001;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private RecyclerView mRecycleView;
    private EditText mEtA;
    private EditText mEtN;
    private EditText mEtFilter;


    private ArrayList<NSDevice> mDeviceList = new ArrayList<>();
    private HashMap<String, NSDevice> mDeviceMap = new HashMap<>();
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        askBle();
        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView(){
        mRecycleView = this.findViewById(R.id.id_recyclerview);
        mEtA = this.findViewById(R.id.et_a);
        mEtN = this.findViewById(R.id.et_n);
        mEtFilter = this.findViewById(R.id.et_filter);

        mEtA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0){
                    RssiUtil.A_Value = Integer.parseInt(editable.toString());
                }
            }
        });

        mEtN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 2){
                    RssiUtil.n_Value = Float.parseFloat(editable.toString());
                }
            }
        });

        initRecycleView();
    }

    private void initRecycleView() {
        mAdapter = new MainAdapter(this.getApplicationContext());
        mAdapter.setOnItemClickLitener(new MainAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback); //停止搜索
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        //layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
    }

    public void refreshDeviceList(){
        if(mAdapter == null){
            return;
        }

        mDeviceList.clear();
        Iterator iter = mDeviceMap.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            NSDevice val = (NSDevice) entry.getValue();

//            //测试标签
//            if(val.getAddress().contains("FF:FF:FF:01:87:AE")
//                    || val.getAddress().contains("FF:FF:FF:01:87:AF")) {
//                mDeviceList.add(val);
//            }
            mDeviceList.add(val);
        }

//        Collections.sort(mDeviceList, (arg0, arg1) -> arg1.getRssi().compareTo(arg0.getRssi()));

        mAdapter.setData(mDeviceList);
        mAdapter.notifyDataSetChanged();
    }


    private void askBle() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            finish();
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.ACCESS_COARSE_LOCATION
                )
                .onGranted(permissions -> {
                })
                .onDenied(permissions -> {
                })
                .start();
    }

    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean start) {
        if (start) {
            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            }
        } else {
            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            }
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(() -> {
                String address = device.getAddress(); //获取蓝牙设备mac地址
                String name = device.getName();  //获取蓝牙设备名字

                NSDevice deviceEntity = new NSDevice();
                deviceEntity.setAddress(address);
                deviceEntity.setName(name);
                deviceEntity.setRssi(rssi);

                double distance = RssiUtil.getDistance(rssi);
                Log.d(TAG, "distance: " + String.valueOf(distance));

                if(distance > 3){
                    mDeviceMap.remove(address);
                }else {
                    mDeviceMap.put(deviceEntity.getAddress(), deviceEntity);
                }

                refreshDeviceList();
            });
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String address = result.getDevice().getAddress(); //获取蓝牙设备mac地址
            String name = result.getDevice().getName();  //获取蓝牙设备名字

            NSDevice deviceEntity = new NSDevice();
            deviceEntity.setAddress(address);
            deviceEntity.setName(name);
            deviceEntity.setRssi(result.getRssi());

            double distance = RssiUtil.getDistance(result.getRssi());
            Log.d(TAG, "distance: " + String.valueOf(distance));

            if(distance > 3){
                mDeviceMap.remove(address);
            }else {
                mDeviceMap.put(deviceEntity.getAddress(), deviceEntity);
            }
            refreshDeviceList();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
}
