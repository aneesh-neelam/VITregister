package app.vit.vitregister.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.corewise.logic.BluetoothChatService;
import app.vit.corewise.utils.ToastUtil;
import app.vit.vitregister.register.RegisterActivity;

public class BluetoothActivity extends AppCompatActivity {

    public static final String CONNECT_RESULT = "connect_result";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private MainApplication application;
    private Button connect;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService = null;

    private ProgressDialog progressDialog;

    private ConnectBroadcast broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        connect = (Button) findViewById(R.id.connectBluetooth);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);
                    return;
                }
                Intent serverIntent = new Intent(BluetoothActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });
        initData();
        broadcast = new ConnectBroadcast(this);
        IntentFilter filter = new IntentFilter(CONNECT_RESULT);
        registerReceiver(broadcast, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcast);
        super.onDestroy();
    }


    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtil.showToast(this, R.string.no_bluetooth);
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        application = (MainApplication) this.getApplicationContext();
        mChatService = new BluetoothChatService(application);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    ToastUtil.showToast(this, R.string.enable_bluetooth);
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the Bluetooth Device object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device

                    showProgressDialog(R.string.connecting_bluetooth);
                    mChatService.connect(device);
                    application.setChatService(mChatService);
                    startActivity(new Intent(BluetoothActivity.this, RegisterActivity.class));
                } else if (resultCode == RESULT_CANCELED) {
                    ToastUtil.showToast(this, R.string.disable_bluetooth);
                }
                break;
            default:
                break;
        }
    }


    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void showProgressDialog(int resId) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(getResources().getString(resId));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

}
