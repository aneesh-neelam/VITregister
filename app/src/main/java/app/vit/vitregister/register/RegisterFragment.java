package app.vit.vitregister.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import app.vit.vitregister.R;
import app.vit.data.Student;
import app.vit.vitregister.device.DeviceActivity;

public class RegisterFragment extends Fragment {

    private View rootView;
    private Student student;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_register, container, false);

        initData();

        return rootView;
    }

    private void initData() {

        Button button = (Button) rootView.findViewById(R.id.register_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText registerNoEditText = (EditText) rootView.findViewById(R.id.register_no);
                String registerNumber = registerNoEditText.getText().toString().toUpperCase();

                student = new Student(registerNumber);
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
                intent.putExtra("student", student);

                startActivity(intent);
            }
        });
    }
}
