package app.vit.vitregister.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.vitregister.bluetooth.BluetoothActivity;

public class DeviceActivity extends AppCompatActivity {

    private MainApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        application = (MainApplication) getApplicationContext();

        if (!application.isConnect()) {
            startActivity(new Intent(this, BluetoothActivity.class));
        }

        setContentView(R.layout.activity_device);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DeviceFragment())
                    .commit();
        }
    }

}
