package app.vit.vitregister.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.vitregister.corewise.utils.ToastUtil;

public class MainFragment extends Fragment {

    private MainApplication application;
    private View rootView;
    private String registerNumber;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initData();
        return rootView;
    }

    private void initData() {
        application = (MainApplication) getActivity().getApplicationContext();

        Button button = (Button) rootView.findViewById(R.id.register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText registerNoEditText = (EditText) rootView.findViewById(R.id.register_no);
                registerNumber = registerNoEditText.getText().toString().toUpperCase();

                ToastUtil.showToast(getActivity(), "Registering: " + registerNumber);
                // Intent intent = new Intent(getActivity(), );
            }
        });
    }
}
