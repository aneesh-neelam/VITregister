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
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnDownCharListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnGenCharListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnGetImageListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnMatchListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnRegModelListener;
import app.vit.vitregister.corewise.asynctask.AsyncFingerprint.OnUpCharListener;
import app.vit.vitregister.corewise.asynctask.AsyncM1Card;
import app.vit.vitregister.corewise.asynctask.AsyncM1Card.OnWriteAtPositionListener;
import app.vit.vitregister.corewise.logic.M1CardAPI;
import app.vit.vitregister.corewise.utils.DataUtils;
import app.vit.vitregister.corewise.utils.ToastUtil;
import app.vit.vitregister.data.Student;

public class DeviceFragment extends Fragment {

    private final String LOG_TAG = DeviceFragment.class.getSimpleName();

    private final String writePosition = "16";
    private final int writeKeyType = M1CardAPI.KEY_A;

    private MainApplication application;

    private Student student;

    private int fingerprintCount;
    private int rfidCount;

    private boolean fingerprintDone;
    private boolean rfidDone;

    private View rootView;
    private ProgressDialog progressDialog;
    private Button registerFingerprintButton;
    private Button verifyFingerprintButton;
    private Button writeRfidButton;
    private Button uploadDataButton;
    private TextView regNoTextView;

    private AsyncFingerprint registerFingerprint;
    private AsyncFingerprint verifyFingerprint;
    private AsyncM1Card writeRfid;

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_device, container, false);

        initData();

        return rootView;
    }

    @Override
    public void onStop() {
        cancelProgressDialog();
        super.onStop();
    }

    private void initData() {

        application = (MainApplication) getActivity().getApplicationContext();

        fingerprintDone = false;
        rfidDone = false;

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("student")) {
            student = intent.getParcelableExtra("student");
        } else {
            student = new Student("15XXX0000");
        }

        regNoTextView = (TextView) rootView.findViewById(R.id.register_no);
        regNoTextView.setText(student.getRegisterNumber());

        registerFingerprintButton = (Button) rootView.findViewById(R.id.fingerprint_register_button);
        registerFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprintCount = 1;
                showProgressDialog(R.string.press_finger);
                registerFingerprint.PS_GetImage();
            }
        });

        verifyFingerprintButton = (Button) rootView.findViewById(R.id.fingerprint_verify_button);
        verifyFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(R.string.press_finger);
                verifyFingerprint.PS_GetImage();
            }
        });

        writeRfidButton = (Button) rootView.findViewById(R.id.rfid_write_button);
        writeRfidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfidCount = 1;
                showProgressDialog(R.string.rfid_writing);
                changeRfidPassword();
            }
        });

        uploadDataButton = (Button) rootView.findViewById(R.id.upload_button);
        uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadTask(DeviceFragment.this).execute(student);
            }
        });


        registerFingerprint = new AsyncFingerprint(application.getHandlerThread().getLooper(), application.getChatService());

        registerFingerprint.setOnGetImageListener(new OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                registerFingerprint.PS_GenChar(fingerprintCount);
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
                    fingerprintCount++;
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
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
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
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
            }
        });

        registerFingerprint.setOnUpCharListener(new OnUpCharListener() {

            @Override
            public void onUpCharSuccess(byte[] model) {
                cancelProgressDialog();

                String fingerprintHexStr = DataUtils.toHexString(model);
                student.setFingerprint(fingerprintHexStr);

                ToastUtil.showToast(getActivity(), R.string.fingerprint_register_success);
                Log.v(LOG_TAG, "FingerprintHexStr for " + student.getRegisterNumber() + ": " + student.getFingerprint());

                fingerprintDone = true;
                verifyFingerprintButtonCheck();
                uploadButtonCheck();
            }

            @Override
            public void onUpCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_register_fail);
            }
        });

        verifyFingerprint = new AsyncFingerprint(application.getHandlerThread().getLooper(), application.getChatService());

        verifyFingerprint.setOnGetImageListener(new OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                cancelProgressDialog();
                showProgressDialog(R.string.fingerprint_processing);
                verifyFingerprint.PS_GenChar(1);
            }

            @Override
            public void onGetImageFail() {
                verifyFingerprint.PS_GetImage();
            }
        });

        verifyFingerprint.setOnGenCharListener(new OnGenCharListener() {
            @Override
            public void onGenCharSuccess(int bufferId) {
                byte[] model = DataUtils.hexStringTobyte(student.getFingerprint());
                verifyFingerprint.PS_DownChar(model);
            }

            @Override
            public void onGenCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
            }
        });

        verifyFingerprint.setOnDownCharListener(new OnDownCharListener() {
            @Override
            public void onDownCharSuccess() {
                verifyFingerprint.PS_Match();
            }

            @Override
            public void onDownCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
            }
        });

        verifyFingerprint.setOnMatchListener(new OnMatchListener() {
            @Override
            public void onMatchSuccess() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_verify_success);
            }

            @Override
            public void onMatchFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_verify_fail);
            }
        });

        writeRfid = new AsyncM1Card(application.getHandlerThread().getLooper(), application.getChatService());

        writeRfid.setOnWriteAtPositionListener(new OnWriteAtPositionListener() {
            @Override
            public void onWriteAtPositionSuccess(String num) {
                cancelProgressDialog();
                if (rfidCount == 1) {
                    rfidCount++;
                    String writeData = student.getRegisterNumber() + '\0';
                    writeRfid.write(Integer.parseInt(writePosition), writeKeyType, null, writeData.getBytes());
                } else if (rfidCount == 2) {
                    cancelProgressDialog();

                    student.setRfid(num);

                    ToastUtil.showToast(getActivity(), R.string.rfid_write_success);
                    Log.v(LOG_TAG, "RFID CardNum for " + student.getRegisterNumber() + ": " + student.getRfid());

                    rfidDone = true;
                    verifyFingerprintButtonCheck();
                    uploadButtonCheck();
                }
            }

            @Override
            public void onWriteAtPositionFail(int confirmationCode) {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.rfid_write_fail);
            }
        });


    }

    void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.show();
    }

    void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    private void changeRfidPassword() {
        byte[] password = new byte[]{
                //Password A byte (6)
                // 'b', 'b', 'b', 'b', 'b', 'b',
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff,
                //Access control (4 bytes), the need to modify
                (byte) 0xff, 0x07, (byte) 0x80, 0x69,
                //Password (6 B bytes)
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff
                // 'a', 'a', 'a', 'a', 'a','a'
        };
        writeRfid.write(Integer.parseInt(writePosition), writeKeyType, null, password);
    }

    private void uploadButtonCheck() {
        uploadDataButton.setEnabled(rfidDone && fingerprintDone);
    }

    private void verifyFingerprintButtonCheck() {
        verifyFingerprintButton.setEnabled(fingerprintDone);
    }

}
