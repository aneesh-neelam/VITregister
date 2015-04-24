package app.vit.vitregister.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnGenCharListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnGetImageListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnRegModelListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnUpCharListener;
import app.vit.vitregister.corewise.utils.DataUtils;
import app.vit.vitregister.corewise.utils.ToastUtil;
import app.vit.vitregister.data.Student;

public class DeviceFragment extends Fragment {

    private final String LOG_TAG = DeviceFragment.class.getSimpleName();
    private MainApplication application;
    private Student student;
    private int count;
    private View rootView;
    private ProgressDialog progressDialog;
    private AsyncFingerprint registerFingerprint;

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_device, container, false);

        initData();

        return rootView;
    }

    private void initData() {

        application = (MainApplication) getActivity().getApplicationContext();

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("student")) {
            student = intent.getParcelableExtra("student");
        } else {
            student = new Student("15XXX0000");
        }

        TextView detailTextView = (TextView) rootView.findViewById(R.id.register_no);
        detailTextView.setText(student.getRegisterNumber());

        Button button = (Button) rootView.findViewById(R.id.fingerprint_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 1;
                showProgressDialog(R.string.press_finger);
                registerFingerprint.PS_GetImage();
            }
        });

        registerFingerprint = new AsyncFingerprint(application.getHandlerThread().getLooper(), application.getChatService());

        registerFingerprint.setOnGetImageListener(new OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                registerFingerprint.PS_GenChar(count);
            }

            @Override
            public void onGetImageFail() {
                registerFingerprint.PS_GetImage();
            }
        });

        registerFingerprint.setOnGenCharListener(new OnGenCharListener() {
            @Override
            public void onGenCharSuccess(int bufferId) {
                cancelProgressDialog();
                if (bufferId == 1) {
                    count++;
                    showProgressDialog(R.string.press_finger_again);
                    registerFingerprint.PS_GetImage();
                } else if (bufferId == 2) {
                    showProgressDialog(R.string.fingerprint_processing);
                    registerFingerprint.PS_RegModel();
                }
            }

            @Override
            public void onGenCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_fail);
            }
        });

        registerFingerprint.setOnRegModelListener(new OnRegModelListener() {

            @Override
            public void onRegModelSuccess() {
                registerFingerprint.PS_UpChar();
            }

            @Override
            public void onRegModelFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_fail);
            }
        });

        registerFingerprint.setOnUpCharListener(new OnUpCharListener() {

            @Override
            public void onUpCharSuccess(byte[] model) {
                cancelProgressDialog();

                String fingerprintHexStr = DataUtils.toHexString(model);
                student.setFingerprint(fingerprintHexStr);

                ToastUtil.showToast(getActivity(), R.string.fingerprint_success);
                Log.v(LOG_TAG, "FingerprintHexStr for " + student.getRegisterNumber() + ": " + student.getFingerprint());
            }

            @Override
            public void onUpCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_fail);
            }
        });

    }

    private void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }


    @Override
    public void onStop() {
        cancelProgressDialog();
        super.onStop();
    }

}
