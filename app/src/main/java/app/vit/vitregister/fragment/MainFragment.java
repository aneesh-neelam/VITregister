package app.vit.vitregister.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.vitregister.activity.BluetoothActivity;

public class MainFragment extends Fragment {

    private MainApplication application;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        application = (MainApplication) getActivity().getApplicationContext();

        if (!application.isConnect()) {
            startActivity(new Intent(getActivity(), BluetoothActivity.class));
        }


        return rootView;
    }
}
