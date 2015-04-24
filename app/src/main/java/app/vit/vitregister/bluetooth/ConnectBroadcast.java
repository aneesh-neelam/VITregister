package app.vit.vitregister.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectBroadcast extends BroadcastReceiver {

    private BluetoothActivity bluetoothActivity;

    public ConnectBroadcast(BluetoothActivity bluetoothActivity) {
        this.bluetoothActivity = bluetoothActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("whw", "action=" + action);
        if (BluetoothActivity.CONNECT_RESULT.equals(action)) {
            int result = intent.getIntExtra("result", 0);
            if (result == 1) {
                bluetoothActivity.finish();
            }
            bluetoothActivity.cancelProgressDialog();
        }
    }

}
