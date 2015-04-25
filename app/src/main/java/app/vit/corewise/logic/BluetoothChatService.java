/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.vit.corewise.logic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import app.vit.corewise.utils.DataUtils;

/**
 *
 */
public class BluetoothChatService {
    /**
     * �������ӳɹ�
     */
    public static final int CONNECTION_SUCCESS = 100;
    /**
     * ��������ʧ��
     */
    public static final int CONNECTION_FAIL = 101;
    /**
     * �������Ӷ�ʧ
     */
    public static final int CONNECTION_LOST = 102;
    // ������ָʾ��ǰ������״̬
    public static final int STATE_NONE = 0; // ��ǰû�п��õ�����
    public static final int STATE_LISTEN = 1; // �����������������
    public static final int STATE_CONNECTING = 2; // ���ڿ�ʼ������ϵ
    public static final int STATE_CONNECTED = 3; // �������ӵ�Զ���豸
    // ����
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;
    // ��¼�������������׽���
    private static final String NAME = "BluetoothChat";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static StringBuffer hexString = new StringBuffer();
    public static boolean bRun = true;
    public static boolean bConnect_State = false;
    public static boolean switchRFID;
    // ��������Ա
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private OnConnectListener onConnectListener;

    /**
     * ���캯����
     */
    public BluetoothChatService(OnConnectListener onConnectListener) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        this.onConnectListener = onConnectListener;
    }

    ;

    /**
     * ���ص�ǰ������״̬��
     */
    public synchronized int getState() {
        return mState;
    }

    private synchronized void setState(int state) {
        if (D)
            Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    /**
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connect to: " + device);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connected");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    /**
     * ֹͣ���������������йص��߳�
     */
    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public synchronized void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    /**
     * ��ȡ����
     *
     * @param out
     * @param waittime
     * @return
     */
    public int read(byte[] out, int waittime, int endWaitTimeout) {
        // ������ʱ����
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return 0;
            r = mConnectedThread;
        }
        int length = r.read(out, waittime, endWaitTimeout);
        return length;
    }

    public int readImage(byte[] out, int waittime, int requestLength) {
        // ������ʱ����
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return 0;
            r = mConnectedThread;
        }
        int length = r.readImage(out, waittime, requestLength);
        return length;
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        if (onConnectListener != null) {
            onConnectListener.onConnectFail();
        }
        stop();
        bConnect_State = false;
        switchRFID = false;
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(STATE_LISTEN);
        if (onConnectListener != null) {
            onConnectListener.onConnectLost();
        }
        stop();
        bConnect_State = false;
        switchRFID = false;
    }

    public interface OnConnectListener {
        public void onConnectSuccess();

        public void onConnectFail();

        public void onConnectLost();
    }

    /**
     * ��������ͼʹ������ϵ ���豸������ֱ�������ӣ����� �ɹ���ʧ�ܡ�
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // �õ�һ��bluetoothsocketΪ������
            // ���������豸
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            mAdapter.cancelDiscovery();

            // ʹһ�����ӵ�bluetoothsocket
            try {
                // ����һ���������úͽ�ֻ����һ��
                // �ɹ������ӻ�����
                mmSocket.connect();
                // mHandler.sendEmptyMessage(CONNECTION_SUCCESS);
                if (onConnectListener != null) {
                    onConnectListener.onConnectSuccess();
                }
                bConnect_State = true;
            } catch (IOException e) {
                connectionFailed();
                // �ر����socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }
                BluetoothChatService.this.stop();
                return;
            }

            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // ���������߳�
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                Log.i("whw", "ConnectThread cancel");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * ������������Զ���豸�� ���������д���ʹ����Ĵ��䡣
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] mmRecvbuffer = new byte[1024 * 50];
        int mmbytes = 0;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // ���bluetoothsocket���������
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "û�д�����ʱsockets", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024 * 4];
            int bytes;

            // ������InputStreamͬʱ����
            while (true) {
                try {
                    // ��ȡ������
                    bytes = mmInStream.read(buffer);
                    System.arraycopy(buffer, 0, mmRecvbuffer, mmbytes, bytes);
                    mmbytes += bytes;
                    Log.i("whw", "input stream mmbytes=" + mmbytes
                            + "     current read=" + bytes);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    Log.i("whw", "exception=" + e.getMessage());
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * д��������ӡ�
         *
         * @param buffer ����һ���ֽ���
         */
        public synchronized void write(byte[] buffer) {
            // Log.i("whw", "mmSocket isConnected="+mmSocket.isConnected());
            mmbytes = 0;
            try {
                Log.i("whw", "write hex=" + DataUtils.toHexString(buffer));
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public synchronized int read(byte[] buffer, int waittime,
                                     int endWaitTimeout) {
            int sleepTime = 10;
            int length = waittime / sleepTime;
            boolean shutDown = false;
            int[] readDataLength = new int[3];
            for (int i = 0; i < length; i++) {
                if (mmbytes == 0) {
                    SystemClock.sleep(sleepTime);
                    continue;
                } else {
                    break;
                }
            }

            if (mmbytes > 0) {
                while (!shutDown) {
                    SystemClock.sleep(endWaitTimeout / 3);
                    readDataLength[0] = readDataLength[1];
                    readDataLength[1] = readDataLength[2];
                    readDataLength[2] = mmbytes;
                    Log.i("whw", "read2    mmbytes=" + mmbytes);
                    if (readDataLength[0] == readDataLength[2]) {
                        shutDown = true;
                    }
                }
                if (mmbytes <= buffer.length) {
                    System.arraycopy(mmRecvbuffer, 0, buffer, 0, mmbytes);
                }
            }
            return mmbytes;
        }

        public synchronized int readImage(byte[] buffer, int waittime,
                                          int requestLength) {
            int sleepTime = 10;
            int length = waittime / sleepTime;
            boolean shutDown = false;
            int[] readDataLength = new int[3];
            for (int i = 0; i < length; i++) {
                if (mmbytes == 0) {
                    SystemClock.sleep(sleepTime);
                    continue;
                } else {
                    break;
                }
            }

            if (mmbytes > 0) {
                while (!shutDown) {
                    if (mmbytes == requestLength) {
                        shutDown = true;
                    } else {
                        shutDown = isTimeout(readDataLength);
                    }
                }

                if (mmbytes <= buffer.length) {
                    System.arraycopy(mmRecvbuffer, 0, buffer, 0, mmbytes);
                }
            }
            return mmbytes;
        }

        public boolean isTimeout(int[] data) {
            if (data != null) {
                SystemClock.sleep(200);
                for (int i = 0; i < data.length; i++) {
                    if (i == data.length - 1) {
                        data[i] = mmbytes;
                    } else {
                        data[i] = data[i + 1];
                    }
                }
                if (data[0] == data[data.length - 1]) {
                    return true;
                }
            }
            return false;
        }

        public void cancel() {
            try {
                Log.i("whw", "mConnectedThread cancel");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }

}
