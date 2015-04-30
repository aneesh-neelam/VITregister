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

import app.vit.corewise.asynctask.AsyncFingerprint;
import app.vit.corewise.asynctask.AsyncFingerprint.OnGenCharListener;
import app.vit.corewise.asynctask.AsyncFingerprint.OnGetImageListener;
import app.vit.corewise.asynctask.AsyncFingerprint.OnLoadCharListener;
import app.vit.corewise.asynctask.AsyncFingerprint.OnMatchListener;
import app.vit.corewise.asynctask.AsyncFingerprint.OnRegModelListener;
import app.vit.corewise.asynctask.AsyncFingerprint.OnStoreCharListener;
import app.vit.corewise.asynctask.AsyncM1Card;
import app.vit.corewise.asynctask.AsyncM1Card.OnWriteAtPositionListener;
import app.vit.corewise.logic.M1CardAPI;
import app.vit.corewise.utils.ToastUtil;
import app.vit.data.Student;
import app.vit.vitregister.MainApplication;
import app.vit.vitregister.R;

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
        student.setFingerprintPageId(generatePageIdFromRegisterNumber(student.getRegisterNumber()));

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
                Log.v(LOG_TAG, "onGetImageSuccess");
            }

            @Override
            public void onGetImageFail() {
                registerFingerprint.PS_GetImage();
                Log.v(LOG_TAG, "onGetImageFail");
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
                Log.v(LOG_TAG, "onGenCharSuccess");
            }

            @Override
            public void onGenCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
                Log.v(LOG_TAG, "onGenCharFail");
            }
        });

        registerFingerprint.setOnRegModelListener(new OnRegModelListener() {

            @Override
            public void onRegModelSuccess() {
                registerFingerprint.PS_StoreChar(1, student.getFingerprintPageId());
                Log.v(LOG_TAG, "onRegModelSuccess");
            }

            @Override
            public void onRegModelFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
                Log.v(LOG_TAG, "onRegModelFail");
            }
        });

        registerFingerprint.setOnStoreCharListener(new OnStoreCharListener() {
            @Override
            public void onStoreCharSuccess() {
                cancelProgressDialog();

                ToastUtil.showToast(getActivity(), R.string.fingerprint_register_success);

                fingerprintDone = true;
                verifyFingerprintButtonCheck();
                uploadButtonCheck();
                Log.v(LOG_TAG, "onStoreCharSuccess");
            }

            @Override
            public void onStoreCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
                Log.v(LOG_TAG, "onStoreCharFail");
            }
        });

        verifyFingerprint = new AsyncFingerprint(application.getHandlerThread().getLooper(), application.getChatService());

        verifyFingerprint.setOnGetImageListener(new OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                cancelProgressDialog();
                showProgressDialog(R.string.fingerprint_processing);
                verifyFingerprint.PS_GenChar(1);
                Log.v(LOG_TAG, "onGetImageSuccess");
            }

            @Override
            public void onGetImageFail() {
                verifyFingerprint.PS_GetImage();
                Log.v(LOG_TAG, "onGetImageFail");
            }
        });

        verifyFingerprint.setOnGenCharListener(new OnGenCharListener() {
            @Override
            public void onGenCharSuccess(int bufferId) {
                verifyFingerprint.PS_LoadChar(2, student.getFingerprintPageId());
                Log.v(LOG_TAG, "onGenCharSuccess");
            }

            @Override
            public void onGenCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
                Log.v(LOG_TAG, "onGenCharFail");
            }
        });

        verifyFingerprint.setOnLoadCharListener(new OnLoadCharListener() {
            @Override
            public void onLoadCharSuccess() {
                verifyFingerprint.PS_Match();
                Log.v(LOG_TAG, "onLoadCharSuccess");
            }

            @Override
            public void onLoadCharFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_processing_fail);
                Log.v(LOG_TAG, "onLoadCharFail");
            }
        });

        verifyFingerprint.setOnMatchListener(new OnMatchListener() {
            @Override
            public void onMatchSuccess() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_verify_success);
                Log.v(LOG_TAG, "onMatchSuccess");
            }

            @Override
            public void onMatchFail() {
                cancelProgressDialog();
                ToastUtil.showToast(getActivity(), R.string.fingerprint_verify_fail);
                Log.v(LOG_TAG, "onMatchFail");
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

    private int generatePageIdFromRegisterNumber(String RegNo) {
        return Integer.parseInt(RegNo.substring(RegNo.length() - 3));
    }

}
