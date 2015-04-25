package app.vit.vitregister;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import app.vit.corewise.logic.BluetoothChatService;
import app.vit.corewise.logic.BluetoothChatService.OnConnectListener;
import app.vit.corewise.utils.ToastUtil;
import app.vit.vitregister.bluetooth.BluetoothActivity;

public class MainApplication extends Application implements OnConnectListener {

    private final String LOG_TAG = MainApplication.class.getSimpleName();

    private static final int CONNECT_SUCCESS = 1;
    private static final int CONNECT_FAIL = 2;
    private static final int CONNECT_LOST = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(BluetoothActivity.CONNECT_RESULT);
            switch (msg.what) {
                case CONNECT_SUCCESS:
                    Log.i(LOG_TAG, "CONNECT_SUCCESS");
                    ToastUtil.showToast(MainApplication.this, R.string.connection_success);
                    intent.putExtra("result", 1);
                    sendBroadcast(intent);
                    break;
                case CONNECT_FAIL:
                    Log.i(LOG_TAG, "CONNECT_FAIL");
                    ToastUtil.showToast(MainApplication.this, R.string.connection_fail);
                    intent.putExtra("result", 2);
                    sendBroadcast(intent);
                    break;
                case CONNECT_LOST:
                    Log.i(LOG_TAG, "CONNECT_LOST");
                    ToastUtil.showToast(MainApplication.this, R.string.connection_lost);
                    intent.putExtra("result", 3);
                    sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        }

    };
    private BluetoothChatService chatService = null;
    private boolean isConnect;
    private HandlerThread handlerThread;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
    }

    public HandlerThread getHandlerThread() {
        return handlerThread;
    }

    public BluetoothChatService getChatService() {
        return chatService;
    }

    public void setChatService(BluetoothChatService mChatService) {
        this.chatService = mChatService;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean isConnect) {
        this.isConnect = isConnect;
        Log.i(LOG_TAG, "Connected: " + isConnect);
    }

    @Override
    public void onConnectSuccess() {
        setConnect(true);
        Log.i(LOG_TAG, "Connection Success");
        mHandler.sendEmptyMessage(CONNECT_SUCCESS);
    }

    @Override
    public void onConnectFail() {
        setConnect(false);
        Log.i(LOG_TAG, "Connection Fail");
        mHandler.sendEmptyMessage(CONNECT_FAIL);
    }

    @Override
    public void onConnectLost() {
        setConnect(false);
        Log.i(LOG_TAG, "Connection Lost");
        mHandler.sendEmptyMessage(CONNECT_LOST);
    }
}
