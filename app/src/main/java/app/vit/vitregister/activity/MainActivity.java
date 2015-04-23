package app.vit.vitregister.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.vitregister.fragment.MainFragment;


public class MainActivity extends ActionBarActivity {

    private MainApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (MainApplication) getApplicationContext();

        setContentView(R.layout.activity_main);

        if (!application.isConnect()) {
            startActivity(new Intent(this, BluetoothActivity.class));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }
}
