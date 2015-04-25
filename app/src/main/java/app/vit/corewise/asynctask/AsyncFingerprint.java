package app.vit.corewise.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import app.vit.corewise.logic.BluetoothChatService;
import app.vit.corewise.logic.FingerprintAPI;
import app.vit.corewise.logic.FingerprintAPI.Result;

public class AsyncFingerprint extends Handler {

    private static final int PS_GetImage = 0x01;
    private static final int PS_GenChar = 0x02;
    private static final int PS_Match = 0x03;
    private static final int PS_Search = 0x04;
    private static final int PS_RegModel = 0x05;
    private static final int PS_StoreChar = 0x06;
    private static final int PS_LoadChar = 0x07;
    private static final int PS_UpChar = 0x08;

    private static final int PS_DownChar = 0x09;
    private static final int PS_UpImage = 0x0a;
    private static final int PS_DownImage = 0x0b;
    private static final int PS_DeleteChar = 0x0c;
    private static final int PS_Empty = 0x0d;
    private static final int PS_Enroll = 0x10;
    private static final int PS_Identify = 0x11;
    private Handler mWorkerThreadHandler;


    private FingerprintAPI fingerprint;
    private OnGetImageListener onGetImageListener;
    private OnUpImageListener onUpImageListener;
    private OnGenCharListener onGenCharListener;
    private OnRegModelListener onRegModelListener;
    private OnUpCharListener onUpCharListener;
    private OnDownCharListener onDownCharListener;
    private OnMatchListener onMatchListener;
    private OnStoreCharListener onStoreCharListener;
    private OnLoadCharListener onLoadCharListener;
    private OnSearchListener onSearchListener;
    private OnDeleteCharListener onDeleteCharListener;
    private OnEmptyListener onEmptyListener;
    private OnEnrollListener onEnrollListener;
    private OnIdentifyListener onIdentifyListener;

    public AsyncFingerprint(Looper looper, BluetoothChatService chatSerivce) {
        createHandler(looper);
        fingerprint = new FingerprintAPI(chatSerivce);
    }

    private Handler createHandler(Looper looper) {
        return mWorkerThreadHandler = new WorkerHandler(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case PS_GetImage:
                if (onGetImageListener == null) {
                    return;
                }
                if (msg.arg1 == 0) {
                    onGetImageListener.onGetImageSuccess();
                } else {
                    onGetImageListener.onGetImageFail();
                }
                break;
            case PS_UpImage:
                if (onUpImageListener == null) {
                    return;
                }
                if (msg.obj != null) {
                    onUpImageListener.onUpImageSuccess((byte[]) msg.obj);
                } else {
                    onUpImageListener.onUpImageFail();
                }
                break;
            case PS_GenChar:
                if (onGenCharListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onGenCharListener.onGenCharSuccess(msg.arg2);
                    } else {
                        onGenCharListener.onGenCharFail();
                    }
                }
                break;
            case PS_RegModel:
                if (onRegModelListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onRegModelListener.onRegModelSuccess();
                    } else {
                        onRegModelListener.onRegModelFail();
                    }
                }
                break;
            case PS_UpChar:
                if (onUpCharListener == null) {
                    return;
                } else {
                    if (msg.obj != null) {
                        onUpCharListener.onUpCharSuccess((byte[]) msg.obj);
                    } else {
                        onUpCharListener.onUpCharFail();
                    }
                }
                break;
            case PS_DownChar:
                if (onDownCharListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onDownCharListener.onDownCharSuccess();
                    } else {
                        onDownCharListener.onDownCharFail();
                    }
                }
                break;
            case PS_Match:
                if (onMatchListener == null) {
                    return;
                } else {
                    if ((Boolean) msg.obj) {
                        onMatchListener.onMatchSuccess();
                    } else {
                        onMatchListener.onMatchFail();
                    }
                }
                break;
            case PS_StoreChar:
                if (onStoreCharListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onStoreCharListener.onStoreCharSuccess();
                    } else {
                        onStoreCharListener.onStoreCharFail();
                    }
                }
                break;
            case PS_LoadChar:
                if (onLoadCharListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onLoadCharListener.onLoadCharSuccess();
                    } else {
                        onLoadCharListener.onLoadCharFail();
                    }
                }
                break;
            case PS_Search:
                if (onSearchListener == null) {
                    return;
                } else {
                    Result result = (Result) msg.obj;
                    if (result != null) {
                        if (result.code == 0x00) {
                            int pageId = result.pageId;
                            int matchScore = result.matchScore;
                            onSearchListener.onSearchSuccess(pageId, matchScore);
                            return;
                        }
                    }
                    onSearchListener.onSearchFail();
                }
                break;
            case PS_DeleteChar:
                if (onDeleteCharListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onDeleteCharListener.onDeleteCharSuccess();
                    } else {
                        onDeleteCharListener.onDeleteCharFail();
                    }
                }
                break;
            case PS_Empty:
                if (onEmptyListener == null) {
                    return;
                } else {
                    if (msg.arg1 == 0) {
                        onEmptyListener.onEmptySuccess();
                    } else {
                        onEmptyListener.onEmptyFail();
                    }
                }
                break;
            case PS_Enroll:
                if (onEnrollListener == null) {
                    return;
                } else {
                    Result result = (Result) msg.obj;
                    if (result != null) {
                        if (result.code == 0x00) {
                            int pageId = result.pageId;
                            onEnrollListener.onEnrollSuccess(pageId);
                            return;
                        }
                    }
                    onEnrollListener.onEnrollFail();
                }
                break;
            case PS_Identify:
                if (onIdentifyListener == null) {
                    return;
                } else {
                    Result result = (Result) msg.obj;
                    if (result != null) {
                        if (result.code == 0x00) {
                            int pageId = result.pageId;
                            int matchScore = result.matchScore;
                            onIdentifyListener
                                    .onIdentifySuccess(pageId, matchScore);
                            return;
                        }
                    }
                    onIdentifyListener.onIdentifyFail();
                }
                break;
            default:
                break;
        }
    }

    public void setOnGetImageListener(OnGetImageListener onGetImageListener) {
        this.onGetImageListener = onGetImageListener;
    }

    public void setOnUpImageListener(OnUpImageListener onUpImageListener) {
        this.onUpImageListener = onUpImageListener;
    }

    public void setOnGenCharListener(OnGenCharListener onGenCharListener) {
        this.onGenCharListener = onGenCharListener;
    }

    public void setOnRegModelListener(OnRegModelListener onRegModelListener) {
        this.onRegModelListener = onRegModelListener;
    }

    public void setOnUpCharListener(OnUpCharListener onUpCharListener) {
        this.onUpCharListener = onUpCharListener;
    }

    public void setOnDownCharListener(OnDownCharListener onDownCharListener) {
        this.onDownCharListener = onDownCharListener;
    }

    public void setOnMatchListener(OnMatchListener onMatchListener) {
        this.onMatchListener = onMatchListener;
    }

    public void setOnStoreCharListener(OnStoreCharListener onStoreCharListener) {
        this.onStoreCharListener = onStoreCharListener;
    }

    public void setOnLoadCharListener(OnLoadCharListener onLoadCharListener) {
        this.onLoadCharListener = onLoadCharListener;
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public void setOnDeleteCharListener(
            OnDeleteCharListener onDeleteCharListener) {
        this.onDeleteCharListener = onDeleteCharListener;
    }

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        this.onEmptyListener = onEmptyListener;
    }

    public void setOnEnrollListener(OnEnrollListener onEnrollListener) {
        this.onEnrollListener = onEnrollListener;
    }

    public void setOnIdentifyListener(OnIdentifyListener onIdentifyListener) {
        this.onIdentifyListener = onIdentifyListener;
    }

    public void PS_GetImage() {
        mWorkerThreadHandler.sendEmptyMessage(PS_GetImage);
    }

    public void PS_UpImage() {
        mWorkerThreadHandler.sendEmptyMessage(PS_UpImage);
    }

    public void PS_GenChar(int bufferId) {
        mWorkerThreadHandler.obtainMessage(PS_GenChar, bufferId, -1)
                .sendToTarget();
    }

    public void PS_RegModel() {
        mWorkerThreadHandler.sendEmptyMessage(PS_RegModel);
    }

    public void PS_UpChar() {
        mWorkerThreadHandler.sendEmptyMessage(PS_UpChar);
    }

    public void PS_DownChar(byte[] model) {
        mWorkerThreadHandler.obtainMessage(PS_DownChar, model).sendToTarget();
    }

    public void PS_Match() {
        mWorkerThreadHandler.sendEmptyMessage(PS_Match);
    }

    public void PS_StoreChar(int bufferId, int pageId) {
        mWorkerThreadHandler.obtainMessage(PS_StoreChar, bufferId, pageId)
                .sendToTarget();
    }

    public void PS_LoadChar(int bufferId, int pageId) {
        mWorkerThreadHandler.obtainMessage(PS_LoadChar, bufferId, pageId)
                .sendToTarget();
    }

    public void PS_Search(int bufferId, int startPageId, int pageNum) {
        mWorkerThreadHandler.obtainMessage(PS_Search, bufferId, startPageId,
                pageNum).sendToTarget();
    }

    public void PS_DeleteChar(int pageIDStart, int delNum) {
        mWorkerThreadHandler.obtainMessage(PS_DeleteChar, pageIDStart, delNum)
                .sendToTarget();
    }

    public void PS_Empty() {
        mWorkerThreadHandler.sendEmptyMessage(PS_Empty);
    }

    public void PS_Enroll() {
        mWorkerThreadHandler.sendEmptyMessage(PS_Enroll);
    }

    public void PS_Identify() {
        mWorkerThreadHandler.sendEmptyMessage(PS_Identify);
    }

    public interface OnGetImageListener {
        void onGetImageSuccess();

        void onGetImageFail();
    }

    public interface OnUpImageListener {
        void onUpImageSuccess(byte[] data);

        void onUpImageFail();
    }

    public interface OnGenCharListener {
        void onGenCharSuccess(int bufferId);

        void onGenCharFail();
    }

    public interface OnRegModelListener {
        void onRegModelSuccess();

        void onRegModelFail();
    }

    public interface OnUpCharListener {
        void onUpCharSuccess(byte[] model);

        void onUpCharFail();
    }

    public interface OnDownCharListener {
        void onDownCharSuccess();

        void onDownCharFail();
    }

    public interface OnMatchListener {
        void onMatchSuccess();

        void onMatchFail();
    }

    public interface OnStoreCharListener {
        void onStoreCharSuccess();

        void onStoreCharFail();
    }

    public interface OnLoadCharListener {
        void onLoadCharSuccess();

        void onLoadCharFail();
    }

    public interface OnSearchListener {
        void onSearchSuccess(int pageId, int matchScore);

        void onSearchFail();
    }

    public interface OnDeleteCharListener {
        void onDeleteCharSuccess();

        void onDeleteCharFail();
    }

    public interface OnEmptyListener {
        void onEmptySuccess();

        void onEmptyFail();
    }

    public interface OnEnrollListener {
        void onEnrollSuccess(int pageId);

        void onEnrollFail();
    }

    public interface OnIdentifyListener {
        void onIdentifySuccess(int pageId, int matchScore);

        void onIdentifyFail();
    }

    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PS_GetImage:
                    int valueGetImage = fingerprint.PSGetImage();
                    AsyncFingerprint.this.obtainMessage(PS_GetImage, valueGetImage,
                            -1).sendToTarget();
                    break;
                case PS_UpImage:
                    byte[] imageData = fingerprint.PSUpImage();
                    AsyncFingerprint.this.obtainMessage(PS_UpImage, imageData)
                            .sendToTarget();
                    break;
                case PS_GenChar:
                    int valueGenChar = fingerprint.PSGenChar(msg.arg1);
                    AsyncFingerprint.this.obtainMessage(PS_GenChar, valueGenChar,
                            msg.arg1).sendToTarget();
                    break;
                case PS_RegModel:
                    int valueRegModel = fingerprint.PSRegModel();
                    AsyncFingerprint.this.obtainMessage(PS_RegModel, valueRegModel,
                            -1).sendToTarget();
                    break;
                case PS_UpChar:
                    byte[] charData = fingerprint.PSUpChar();
                    AsyncFingerprint.this.obtainMessage(PS_UpChar, charData)
                            .sendToTarget();
                    break;
                case PS_DownChar:
                    int valueDownChar = fingerprint.PSDownChar((byte[]) msg.obj);
                    AsyncFingerprint.this.obtainMessage(PS_DownChar, valueDownChar,
                            -1).sendToTarget();
                    break;
                case PS_Match:
                    boolean valueMatch = fingerprint.PSMatch();
                    AsyncFingerprint.this.obtainMessage(PS_Match,
                            Boolean.valueOf(valueMatch)).sendToTarget();
                    break;
                case PS_StoreChar:
                    int valueStoreChar = fingerprint.PSStoreChar(msg.arg1, msg.arg2);
                    AsyncFingerprint.this.obtainMessage(PS_StoreChar,
                            valueStoreChar, -1).sendToTarget();
                    break;
                case PS_LoadChar:
                    int valueLoadChar = fingerprint.PSLoadChar(msg.arg1, msg.arg2);
                    AsyncFingerprint.this.obtainMessage(PS_LoadChar, valueLoadChar,
                            -1).sendToTarget();
                    break;
                case PS_Search:
                    Result result = fingerprint.PSSearch(msg.arg1, msg.arg2, (Integer) msg.obj);
                    AsyncFingerprint.this.obtainMessage(PS_Search, result)
                            .sendToTarget();
                    break;
                case PS_DeleteChar:
                    int valueDeleteChar = fingerprint.PSDeleteChar((short) msg.arg1,
                            (short) msg.arg2);
                    AsyncFingerprint.this.obtainMessage(PS_DeleteChar,
                            valueDeleteChar, -1).sendToTarget();
                    break;
                case PS_Empty:
                    int valueEmpty = fingerprint.PSEmpty();
                    AsyncFingerprint.this.obtainMessage(PS_Empty, valueEmpty, -1)
                            .sendToTarget();
                    break;
                case PS_Enroll:
                    Result valueEnroll = fingerprint.PSEnroll();
                    AsyncFingerprint.this.obtainMessage(PS_Enroll, valueEnroll)
                            .sendToTarget();
                    break;
                case PS_Identify:
                    Result valueIdentify = fingerprint.PSIdentify();
                    AsyncFingerprint.this.obtainMessage(PS_Identify, valueIdentify)
                            .sendToTarget();
                    break;
                default:
                    break;
            }
        }
    }


}
