package com.eningqu.aipen.common.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.utils.L;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/26 9:38
 */

public class BlueToothLeClass {

    private final static String TAG = BlueToothLeClass.class.getSimpleName();

    /*** ----------------特征值------------------*/
//    private final static String WRITE_CHARACTERISTIC_UUID = "d44bc439-abfd-45a2-b575-925416129600";
//    private final static String READ_CHARACTERISTIC_UUID = "d44bc439-abfd-45a2-b575-925416129601";
//    private final static String WRITE_CHARACTERISTIC_UUID = "d44bc439-abfd-45a2-b575-925416129580";
//    private final static String READ_CHARACTERISTIC_UUID = "d44bc439-abfd-45a2-b575-925416129581";
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final String WRITE_CHARACTERISTIC_UUID = "69400003-b5a3-f393-e0a9-e50e24dcca99";//0000fff2-0000-1000-8000-00805f9b34fb
    public static final String READ_CHARACTERISTIC_UUID = "69400002-b5a3-f393-e0a9-e50e24dcca99";//0000fff1-0000-1000-8000-00805f9b34fb

    private final static String AIO_CONN_CHARACTERISTIC_UUID = "d44bc439-abfd-45a2-b575-925416129608";
    private final static String AIO_NOTE_CHARACTERISTIC_UUID_1 = "d44bc439-abfd-45a2-b575-925416129607";
    private final static String AIO_NOTE_CHARACTERISTIC_UUID_2 = "d44bc439-abfd-45a2-b575-925416129605";
    private final static String AIO_NOTE_CHARACTERISTIC_UUID_3 = "d44bc439-abfd-45a2-b575-925416129603";

    /*** --------------发送命令----------------------*/
    private final static byte[] KEY_STATUS = {(byte) 0x0F, (byte) 0x0F, (byte) 0x57, (byte) 0x00, (byte) 0x00};
    private final static byte[] SEED_KEY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x57, (byte) 0x02, (byte) 0x06};
    private final static byte[] CHECK_KEY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x57, (byte) 0x03, (byte) 0x06};
    private final static byte[] HAS_HISTORY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x71, (byte) 0x00, (byte) 0x00};
    private final static byte[] START_HISTORY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x61, (byte) 0x01, (byte) 0x01};
    private final static byte[] READ_HISTORY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01};
    public final static byte[] CLEAR_HISTORY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x81, (byte) 0x03, (byte) 0x00, (byte) 0x00};
    private final static byte[] READ_REAL = {(byte) 0x0F, (byte) 0x0F, (byte) 0x61, (byte) 0x01, (byte) 0x00};
    private final static byte[] BATTERY = {(byte) 0x0F, (byte) 0x0F, (byte) 0x57, (byte) 0x19, (byte) 0x00};
    public final static byte[] HISTORY_MODE = {(byte) 0x0F, (byte) 0x0F, (byte) 0x61, (byte) 0x01, (byte) 0x01};

    private final static String BLE_MAC = "BLE_MAC";

    public final static int ACTION_REQUEST_BLUETOOTH_ENABLE = 2001;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String REFRESH_DATA = "REFRESH_DATA";

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    public BluetoothDevice bluetoothDevice;

    private Handler mHandler;

    /*** 蓝牙搜索状态*/
    private boolean scanLeStatus = false;
    /**
     * 是否在连接中
     */
    public boolean isBleConnecting = false;
    /*** 蓝牙连接状态*/
    public boolean connectStatus = false;
    /**
     * 校验key状态
     */
    public boolean isCheckKeyStatus = false;

    private BluetoothGattCharacteristic writeCharacteristic, readCharacteristic, aiowriteCharacteristic, aionoteCharacteristic;
    private BluetoothGattCharacteristic syncCharacteristic;

    public BlueToothLeClass(Context mContext) {
        this.mContext = mContext;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                L.error(TAG, "Unable to initialize BluetoothManager.");
                return;
            }
        }

        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null) {
            L.error(TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }

        mHandler = BlueToothLeClass.BlueToothHander.getHandler();
    }

    /**
     * 扫描蓝牙设备
     * 这是一个耗时的过程， 不要放在UI主线程中
     *
     * @param duartion
     */
    public void startLeScan(int duartion) {

        if (!scanLeStatus) {
            stopLeScan();
        }

        if (mBluetoothAdapter == null) {
            L.error(TAG, "mBluetoothAdapter is null...");
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopLeScan();
                scanLeStatus = false;
                if (mScanDeviceListener != null) {
                    mScanDeviceListener.onSearchStopped();
                }
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                scanLeStatus = true;
                if (mScanDeviceListener != null) {
                    mScanDeviceListener.onSearchStarted();
                }
            }
        }, 100);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
//                scanLeStatus = true;
//                if (mScanDeviceListener != null) {
//                    mScanDeviceListener.onSearchStarted();
//                }
//            }
//        }).start();
    }

    /**
     * 停止扫描设备
     */
    public void stopLeScan() {
        if (mBluetoothAdapter == null) {
            L.error(TAG, "mBluetoothAdapter is null...");
            return;
        }
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    /**
     * 蓝牙连接
     *
     * @param address
     * @return
     */
    public synchronized boolean connect(@NonNull final String address) {
        if (mBluetoothAdapter == null) {
            L.warn(TAG, "---------------BluetoothAdapter not initialized");
            return false;
        }

        if (address == null && "".equals(address)) {
            L.warn(TAG, "---------------unspecified address");
            return false;
        }

        if (connectStatus) {
            L.error(TAG, "---------------bluetooth is connected...");
            return false;
        }

        if (!connectStatus) {

            bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            if (bluetoothDevice == null) {
                L.warn(TAG, "bluetoothDevice not found.  Unable to connect.");
                return false;
            }

            mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);

            String tips = mContext.getResources().getString(R.string.blue_connecting).replace("X", bluetoothDevice.getName());
            ToastUtils.showLong(tips);

            isBleConnecting = true;
            return true;
        }
        return false;
    }


    /**
     * 断开连接
     *
     * @param i
     */
    public void disconnect(int i) {
        switch (i) {
            //其他
            case 0: {
                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    L.warn(TAG, "BluetoothAdapter not initialized");
                    return;
                }
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                }
                resetStatus();
                break;
            }
            //退出登录断开连接
            case 1: {
                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    L.warn(TAG, "BluetoothAdapter not initialized");
                    return;
                }
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                }
                connectStatus = false;
                scanLeStatus = false;
                isCheckKeyStatus = false;
                break;
            }
        }
    }

    /**
     * 写入特征值
     *
     * @param characteristic
     * @return
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * BLE 蓝牙 系统5.1及以上版本可提高数据传输数率
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enhanceDataRate() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);
        }
    }


    /**
     * 获取key状态
     */
    public void getKeyStatus() {
        sendCommand(KEY_STATUS);
    }

    /**
     * 设置key 重新给笔种KEY
     *
     * @param data
     */
    public void sendSetKey(@NonNull final byte[] data) {
        if (data == null) {
            return;
        }
        int size = SEED_KEY.length + data.length;
        byte[] keyCommand = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i < SEED_KEY.length) {
                keyCommand[i] = SEED_KEY[i];
            } else {
                keyCommand[i] = data[i - SEED_KEY.length];
            }
        }
        sendCommand(keyCommand);
    }

    /**
     * 校验key
     *
     * @param data
     */
    public void sendCheckKey(@NonNull final byte[] data) {
        if (data == null) {
            return;
        }
        int size = CHECK_KEY.length + data.length;
        byte[] keyCommand = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i < CHECK_KEY.length) {
                keyCommand[i] = CHECK_KEY[i];
            } else {
                keyCommand[i] = data[i - CHECK_KEY.length];
            }
        }
        sendCommand(keyCommand);
    }

    /**
     * 是否存在历史数据
     */
    public void hasHistoryData() {
        sendCommand(HAS_HISTORY);
    }

    /**
     * 开启历史数据接收
     */
    public void startHistoryData() {

        if (!connectStatus) {
            L.error(TAG, "-------------blutooth is not connected");
            return;
        }

        if (this.writeCharacteristic == null) {
            L.info(TAG, "-------------writeCharacteristic is not null");
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlueToothLeClass.this.writeCharacteristic.setValue(START_HISTORY);
                writeCharacteristic(BlueToothLeClass.this.writeCharacteristic);
            }
        }, 200);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlueToothLeClass.this.writeCharacteristic.setValue(READ_HISTORY);
                writeCharacteristic(BlueToothLeClass.this.writeCharacteristic);
//                Intent intent = new Intent(mContext, DrawNqActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//                BluetoothLeService.isHistory = true;
            }
        }, 600);

        isCheckKeyStatus = true;
    }

    /**
     * 清除历史数据
     */
    public void clearHistoryData() {
        sendCommand(CLEAR_HISTORY);
    }

    /**
     * 获取电池电量
     */
    public void getBatteryLevel() {
        sendCommand(BATTERY);
    }

    /**
     * 开启实时数据传输
     */
    public void startRealData() {

        if (!connectStatus) {
            L.error(TAG, "-------------blutooth is not connected");
            return;
        }

        if (this.writeCharacteristic == null) {
            L.info(TAG, "-------------writeCharacteristic is not null");
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlueToothLeClass.this.writeCharacteristic.setValue(READ_REAL);
                writeCharacteristic(BlueToothLeClass.this.writeCharacteristic);
            }
        }, 800);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlueToothLeClass.this.writeCharacteristic.setValue(READ_REAL);
                writeCharacteristic(BlueToothLeClass.this.writeCharacteristic);
            }
        }, 1600);
    }

    /**
     * 发送命令
     */
    public void sendCommand(@NonNull byte[] data) {

        if (!connectStatus) {
            L.error(TAG, "-------------blutooth is not connected");
            return;
        }
        if (data.length == 0) {
            L.error(TAG, "-------------Command can not be empty");
            return;
        }
        if (this.writeCharacteristic == null) {
            L.info(TAG, "-------------writeCharacteristic is not null");
            return;
        }

        if (this.writeCharacteristic != null) {
            this.writeCharacteristic.setValue(data);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeCharacteristic(BlueToothLeClass.this.writeCharacteristic);
                }
            }, 500);
        }
    }

    public void sendNNNCommand(@NonNull byte[] data) {

        if (!connectStatus) {
            L.error(TAG, "-------------blutooth is not connected");
            return;
        }
        if (data.length == 0) {
            L.error(TAG, "-------------Command can not be empty");
            return;
        }
        if (this.syncCharacteristic == null) {
            L.info(TAG, "-------------syncCharacteristic is not null");
            return;
        }

        if (this.syncCharacteristic != null) {
            this.syncCharacteristic.setValue(data);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeCharacteristic(BlueToothLeClass.this.syncCharacteristic);
                }
            }, 500);
        }
    }


    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

        /**
         * 蓝牙连接状态改变
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            L.debug(TAG, "---------------onConnectionStateChange---------------------");
            isBleConnecting = false;
            switch (newState) {
                /**连接成功*/
                case BluetoothProfile.STATE_CONNECTED:
                    try {
                        Thread.sleep(500);
                        //蓝牙连接后 需等待大约600毫秒才能发现设备服务
                        gatt.discoverServices();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    enhanceDataRate();
                    connectStatus = true;
                    if (mConnectStatusListener != null) {
                        mConnectStatusListener.onConnectSuccess(bluetoothDevice);
                    }

                    break;

                /**连接失败*/
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (mConnectStatusListener != null) {
                        if (connectStatus && isCheckKeyStatus) { //检验key失败了
                            mConnectStatusListener.onCheckKeyFail();
                        } else if (connectStatus && !isCheckKeyStatus) {  //连接中断
                            mConnectStatusListener.onConnectInterrupted();
                        } else if (!connectStatus && !isCheckKeyStatus) {  //连接不上
                            mConnectStatusListener.onConnectFail();
                        }
                    }
                    gatt.close();
                    resetStatus();
                    break;
            }
        }

        /**
         * 发现服务，在蓝牙连接后 调用发现服务后回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            L.debug(TAG, "--------------onServicesDiscovered-------------------");
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                obtainCharacteristic(mBluetoothGatt.getServices());
            }
        }

        /**
         * 读取数据
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            L.debug(TAG, "--------------onCharacteristicRead--------------------------------------");
        }

        /**
         * 发送数据后的回调
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            L.debug(TAG, "--------------onCharacteristicWrite-----------------------");
            /*switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    hasWriteStatus = true;
                    L.error(TAG, "--------------------------写入成功");
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    L.debug(TAG, "--------------------------写入失败");
                    break;
                case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                    L.debug(TAG, "---------------------------没有权限");
                    break;
            }
            if (mDataAvailableListener != null) {
                L.error(TAG, "-----------mOnDataAvailableListener-----------");
                mDataAvailableListener.onCharacteristic(gatt, characteristic);
            }*/
        }

        /**
         * Characteristic 改变，数据接收会调用
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            L.debug(TAG, "--------------onCharacteristicChanged-------------------");

            if (mDataAvailableListener != null) {
                mDataAvailableListener.onCharacteristic(gatt, characteristic);
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            L.debug(TAG, "--------------onReliableWriteCompleted-------------------");
            super.onReliableWriteCompleted(gatt, status);
        }

        /**
         * 读Rssi
         * @param gatt
         * @param rssi
         * @param status
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

    };

    /**
     * 匹配可以读或写的特征值
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void obtainCharacteristic(List<BluetoothGattService> services) {

        for (BluetoothGattService bluetoothGattService : services) {
            L.debug(TAG, " ------------ service_uuid ：" + bluetoothGattService.getUuid().toString());
            List<BluetoothGattCharacteristic> gattCharacteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                String characteristic_uuid = gattCharacteristic.getUuid().toString();
                L.debug(TAG, " -------------------------- characteristic_uuid ：" + characteristic_uuid);

                switch (characteristic_uuid) {

                    case WRITE_CHARACTERISTIC_UUID:
                        writeCharacteristic = gattCharacteristic;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getKeyStatus();
                            }
                        }, 100);
                        break;

                    case READ_CHARACTERISTIC_UUID:
                        readCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(readCharacteristic, true);
                        break;

                    case AIO_CONN_CHARACTERISTIC_UUID:
                        aiowriteCharacteristic = gattCharacteristic;
                        break;

                    case AIO_NOTE_CHARACTERISTIC_UUID_1:

                        aionoteCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(aionoteCharacteristic, true);
                        break;

                    case AIO_NOTE_CHARACTERISTIC_UUID_2:
                        aionoteCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(aionoteCharacteristic, true);
                        break;

                    case AIO_NOTE_CHARACTERISTIC_UUID_3:
                        aionoteCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(aionoteCharacteristic, true);
                        break;
                    case "d44bc439-abfd-45a2-b575-925416129582": {
                        syncCharacteristic = gattCharacteristic;
//                        setCharacteristicNotification(aionoteCharacteristic, true);
//                        sendCommand(new byte[]{(byte) 0x0f, (byte) 0x0f, (byte) 0x53, (byte) 0x59, (byte) 0x4e});
                        break;
                    }
//                    case "d44bc439-abfd-45a2-b575-925416129580":{
//                        writeCharacteristic = gattCharacteristic;
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getKeyStatus();
//                            }
//                        }, 100);
//                        aionoteCharacteristic = gattCharacteristic;
//                        setCharacteristicNotification(aionoteCharacteristic, true);
//                        break;
//                    }
//                    case "d44bc439-abfd-45a2-b575-925416129583":{
//                        aionoteCharacteristic = gattCharacteristic;
//                        setCharacteristicNotification(aionoteCharacteristic, true);
//                        break;
//                    }
                }
            }
        }
    }

    private void startServices() {
        mBluetoothGatt.discoverServices();
    }


    /**
     * 特征值订阅通知，否则接收不到数据
     *
     * @param characteristic
     * @param enabled
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            L.debug(TAG, "-------------BluetoothAdapter not initialized");
            return;
        }
        //在通过上面的设置返回为true之后还要进行下面的操作，才能订阅到数据的上传。下面是完整的订阅数据代码！
        if (mBluetoothGatt.setCharacteristicNotification(characteristic, enabled)) {
            for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                if (dp != null) {
                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    }
                    mBluetoothGatt.writeDescriptor(dp);
                }
            }
        }
    }

    /**
     * 广播通知
     *
     * @param action
     */
    private void broadcastNotify(final String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        //intent.setFlags(1);
        mContext.sendBroadcast(intent);
    }

    /**
     * 重置状态
     */
    private void resetStatus() {
        connectStatus = false;
        scanLeStatus = false;
        isCheckKeyStatus = false;

        this.bluetoothDevice = null;
        this.mBluetoothGatt = null;
    }


    /**
     * 扫描蓝牙设备监听回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            final String deviceName = bluetoothDevice.getName();
            if (deviceName != null && deviceName.length() > 0) {
                if (mScanDeviceListener != null) {
//                    if (deviceName.indexOf(devicePrefix) != -1) {
                    mScanDeviceListener.onDeviceFound(bluetoothDevice, rssi, scanRecord);
//                    }
                }
            }

        }

    };

    private ConnectStatusListener mConnectStatusListener;
    private DataAvailableListener mDataAvailableListener;
    private ScanDeviceListener mScanDeviceListener;

    public void setConnectStatusListener(ConnectStatusListener mConnectStatusListener) {
        this.mConnectStatusListener = mConnectStatusListener;
    }

    public void setDataAvailableListener(DataAvailableListener mDataAvailableListener) {
        this.mDataAvailableListener = mDataAvailableListener;
    }

    public void setScanDeviceListener(ScanDeviceListener mScanDeviceListener) {
        this.mScanDeviceListener = mScanDeviceListener;
    }

    /**
     * -------------监听回调事件-------------------
     */

    public interface ConnectStatusListener {
        void onConnectSuccess(BluetoothDevice bluetoothDevice);

        void onConnectFail();

        void onConnectInterrupted();

        void onCheckKeyFail();
    }


    public interface DataAvailableListener {
        void onCharacteristic(BluetoothGatt gatt,
                              BluetoothGattCharacteristic characteristic);
    }

    public interface ScanDeviceListener {
        void onSearchStarted();

        void onDeviceFound(final BluetoothDevice bluetoothDevice, int i, byte[] bytes);

        void onSearchStopped();
    }


    /**
     *
     */
    static class BlueToothHander extends Handler {

        private static BlueToothHander instance;

        public static BlueToothHander getHandler() {
            synchronized (BlueToothHander.class) {
                if (instance == null) {
                    HandlerThread handlerThread = new HandlerThread("BluetoothLe Handler");
                    handlerThread.start();
                    instance = new BlueToothHander(handlerThread.getLooper());
                }
                return instance;
            }
        }

        private BlueToothHander(Looper looper) {
            super(Looper.myLooper());
        }
    }


    /**
     * 是否支持蓝牙
     *
     * @return
     */
    public boolean isSupportBlutooth() {
        return mBluetoothAdapter == null ? false : true;
    }

    /**
     * 当前蓝牙状态
     *
     * @return
     */
    public boolean getBlueToothStatus() {
        assert (mBluetoothAdapter != null);
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 请求打开蓝牙
     *
     * @param activity
     * @param requestCode
     */
    public void openBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void enableBluetooth(boolean enable){
        if(null!=mBluetoothAdapter){
            if(enable){
                mBluetoothAdapter.enable();
            } else {
                mBluetoothAdapter.disable();
            }
        }
    }
}
