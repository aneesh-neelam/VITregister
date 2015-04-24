package app.vit.vitregister.device;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import app.vit.vitregister.R;
import app.vit.vitregister.corewise.utils.ToastUtil;
import app.vit.vitregister.data.Result;
import app.vit.vitregister.data.ServerResponse;
import app.vit.vitregister.data.Student;

public class UploadTask extends AsyncTask<Student, Void, Boolean> {

    private final String LOG_TAG = UploadTask.class.getSimpleName();

    private DeviceFragment deviceFragment;

    public UploadTask(DeviceFragment deviceFragment) {
        this.deviceFragment = deviceFragment;
    }

    @Override
    protected Boolean doInBackground(Student... params) {

        if (params.length == 0) {
            return null;
        }

        final String VITAUTH_BASE_URL = "https://vitauth.herokuapp.com";
        final String VITAUTH_API_SUBURL = "api";
        final String VITAUTH_CENTRAL_SUBURL = "central";
        final String VITAUTH_UPLOADFINGERPRINT_SUBURL = "uploadfingerprint";

        final String USER_AGENT = "Mozilla/5.0";

        Student student = params[0];

        boolean uploadResult;

        HttpsURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            Uri builtUri = Uri.parse(VITAUTH_BASE_URL).buildUpon()
                    .appendPath(VITAUTH_API_SUBURL)
                    .appendPath(VITAUTH_CENTRAL_SUBURL)
                    .appendPath(VITAUTH_UPLOADFINGERPRINT_SUBURL)
                    .build();
            Log.v(LOG_TAG, "VITauth Uri: " + builtUri.toString());

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(25000);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("register_number", student.getRegisterNumber())
                    .appendQueryParameter("fingerprint", student.getFingerprint())
                    .appendQueryParameter("rfid_card_number", student.getRfid());
            String query = builder.build().getEncodedQuery();
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            ServerResponse serverResponse = gson.fromJson(bufferedReader, ServerResponse.class);

            uploadResult = parseResultFromServerResponse(serverResponse);

            Log.v(LOG_TAG, "JSON String: " + gson.toJson(serverResponse));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occured when attempting to upload data", e);
            uploadResult = false;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return uploadResult;
    }

    @Override
    protected void onPreExecute() {
        deviceFragment.showProgressDialog(R.string.uploading);
    }

    @Override
    protected void onPostExecute(Boolean uploadResult) {
        deviceFragment.cancelProgressDialog();
        if (uploadResult) {
            ToastUtil.showToast(deviceFragment.getActivity(), R.string.upload_success);
        } else {
            ToastUtil.showToast(deviceFragment.getActivity(), R.string.upload_fail);
        }
    }

    private boolean parseResultFromServerResponse(ServerResponse serverResponse) {
        Result result = serverResponse.getResult();
        return result.getCode() == 1;
    }
}
