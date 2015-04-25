package app.vit.corewise.logic;

import android.os.SystemClock;
import android.util.Log;

import app.vit.corewise.utils.DataUtils;

public class M1CardAPI {
    public static final int KEY_A = 1;
    public static final int KEY_B = 2;

    private static final byte[] SWITCH_COMMAND = "D&C00040104".getBytes();
    // �������ݰ���ǰ׺
    private static final String DATA_PREFIX = "c050605";
    private static final String FIND_CARD_ORDER = "01";// Ѱ��ָ��
    private static final String PASSWORD_SEND_ORDER = "02";// �����·�ָ��
    private static final String PASSWORD_VALIDATE_ORDER = "03";// ������֤����
    private static final String READ_DATA_ORDER = "04";// ��ָ��
    private static final String WRITE_DATA_ORDER = "05";// дָ��
    private static final String ENTER = "\r\n";// ���з�
    // Ѱ����ָ���
    private static final String FIND_CARD = DATA_PREFIX + FIND_CARD_ORDER
            + ENTER;
    // �·�����ָ���(A��B�������12����f��)
    private static final String SEND_PASSWORD = DATA_PREFIX
            + PASSWORD_SEND_ORDER + "ffffffffffffffffffffffff" + ENTER;
    private static final String WRITE_SUCCESS = " Write Success!" + ENTER;

    // //������ָ֤���
    // private static final static PASSWORD_VALIDATE =
    private static final String TURN_OFF = "c050602\r\n";// �ر����߳�
    // private static final String FIND_SUCCESS = "c05060501" + ENTER + "0x00,";
    private static final String FIND_SUCCESS = "0x00,";
    private static final String DEFAULT_PASSWORD = "ffffffffffff";
    public byte[] buffer = new byte[100];
    private BluetoothChatService chatService;


    public M1CardAPI(BluetoothChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * �л��ɶ�ȡRFID
     *
     * @return
     */
    private boolean switchStatus() {
        sendCommand(SWITCH_COMMAND);
        Log.i("whw", "SWITCH_COMMAND hex=" + new String(SWITCH_COMMAND));
        SystemClock.sleep(1500);
        BluetoothChatService.switchRFID = true;
        return true;
    }

    private int receive(byte[] command, byte[] buffer) {
        int length = -1;
        if (!BluetoothChatService.switchRFID) {
            switchStatus();
        }
        sendCommand(command);

        length = chatService.read(buffer, 300, 100);
        return length;
    }

    private void sendCommand(byte[] command) {
        chatService.write(command);
    }

    private String getCompletePassword(int keyType, String passwordHexStr) {
        // A.B�����볤�ȸ�Ϊ6�ֽڣ������16�����ַ��������볤�Ⱦ�Ϊ12���ַ�����
        StringBuffer passwordBuffer = new StringBuffer();
        passwordBuffer.append(passwordHexStr);
        if (passwordHexStr != null && passwordHexStr.length() < 12) {
            int length = 12 - passwordHexStr.length();
            for (int i = 0; i < length; i++) {
                passwordBuffer.append('0');
            }
        }
        passwordHexStr = passwordBuffer.toString();
        String completePasswordHexStr = "";
        switch (keyType) {
            case KEY_A:
                completePasswordHexStr = passwordHexStr + DEFAULT_PASSWORD;
                break;
            case KEY_B:
                completePasswordHexStr = DEFAULT_PASSWORD + passwordHexStr;
                break;

            default:
                break;
        }
        return completePasswordHexStr;
    }

    private String getKeyTypeStr(int keyType) {
        String keyTypeStr = null;
        switch (keyType) {
            case KEY_A:
                keyTypeStr = "60";
                break;
            case KEY_B:
                keyTypeStr = "61";
                break;
            default:
                keyTypeStr = "60";
                break;
        }
        return keyTypeStr;
    }

    // ת���������ĵ�ַΪ��λ
    private String getZoneId(int position) {
        return DataUtils.byte2Hexstr((byte) position);
    }


    /**
     * ��ȡM1������
     * Read the M1 card number
     *
     * @return
     */
    public Result readCardNum() {
        Log.i("whw", "!!!!!!!!!!!!readCard");
        Result result = new Result();
        byte[] command = FIND_CARD.getBytes();
        int length = receive(command, buffer);
        if (length == 0) {
            result.confirmationCode = Result.TIME_OUT;
            return result;
        }
        String msg = "";
        msg = new String(buffer, 0, length);
        Log.i("whw", "msg hex=" + msg);
        turnOff();
        if (msg.startsWith(FIND_SUCCESS)) {
            result.confirmationCode = Result.SUCCESS;
            result.num = msg.substring(FIND_SUCCESS.length());
        } else {
            result.confirmationCode = Result.FIND_FAIL;
        }
        return result;
    }

    /**
     * ��֤����
     * Verify password
     *
     * @param position block number
     * @param keyType  Password type
     * @param password
     * @return
     */
    public boolean validatePassword(int position, int keyType, byte[] password) {
        Log.i("whw", "!!!!!!!!!!!!!!keyType=" + keyType);
        byte[] command1 = null;
        if (password == null) {
            // �·���֤����
            command1 = SEND_PASSWORD.getBytes();
        } else {
            String passwordHexStr = DataUtils.toHexString(password);
            String completePassword = getCompletePassword(keyType,
                    passwordHexStr);
            command1 = (DATA_PREFIX + PASSWORD_SEND_ORDER + completePassword + ENTER)
                    .getBytes();
        }

        int tempLength = receive(command1, buffer);
        String verifyStr = new String(buffer, 0, tempLength);
        Log.i("whw", "validatePassword verifyStr=" + verifyStr);
        // ��֤����
        byte[] command2 = (DATA_PREFIX + PASSWORD_VALIDATE_ORDER
                + getKeyTypeStr(keyType) + getZoneId(position) + ENTER)
                .getBytes();

        int length = receive(command2, buffer);
        String msg = new String(buffer, 0, length);
        Log.i("whw", "validatePassword msg=" + msg);
        String prefix = "0x00,\r\n";
        if (msg.startsWith(prefix)) {
            return true;
        }
        return false;
    }


    /**
     * ��ȡָ����Ŵ洢�����ݣ�����һ��Ϊ16�ֽ�
     * Reads the specified number stored data, length of 16 bytes
     *
     * @param position block number
     * @return
     */
    public byte[] read(int position) {
        byte[] command = {'c', '0', '5', '0', '6', '0', '5', '0', '4', '0',
                '0', '\r', '\n'};
        char[] c = getZoneId(position).toCharArray();
        command[9] = (byte) c[0];
        command[10] = (byte) c[1];
        int length = receive(command, buffer);
        String data = new String(buffer, 0, length);
        Log.i("whw", "read data=" + data);
        String[] split = data.split(";");
        String msg = "";
        if (split.length == 2) {
            int index = split[1].indexOf("\r\n");
            if (index != -1) {
                msg = split[1].substring(0, index);
            }

            Log.i("whw", "split msg=" + msg + "  msg length=" + msg.length());
        }
        byte[] readData = DataUtils.hexStringTobyte(msg);
        return readData;
    }


    /**
     * ��ָ���Ŀ��д�����ݣ�����Ϊ16�ֽ�
     * Write data to the specified block, length is 16 bytes
     *
     * @param data
     * @param position
     * @return
     */
    public boolean write(byte[] data, int position) {
        String hexStr = DataUtils.toHexString(data);
        byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(position)
                + hexStr + ENTER).getBytes();
        Log.i("whw", "***write hexStr=" + hexStr);

        int length = receive(command, buffer);
        if (length > 0) {
            String writeResult = new String(buffer, 0, length);
            Log.i("whw", "write result=" + writeResult);
            return M1CardAPI.WRITE_SUCCESS.equals(writeResult);
        }
        return false;
    }

    // �ر����߳�
    public String turnOff() {
        // byte[] command = TURN_OFF.getBytes();
        // int length = receive(command, buffer);
        // String str = "";
        // if (length > 0) {
        // str = new String(buffer, 0, length);
        // }
        // return str;
        return "";
    }


    public static class Result {
        /**
         * �ɹ�
         * successful
         */
        public static final int SUCCESS = 1;
        /**
         * Ѱ��ʧ��
         * Find card failure
         */
        public static final int FIND_FAIL = 2;
        /**
         * ��֤ʧ��
         * Validation fails
         */
        public static final int VALIDATE_FAIL = 3;
        /**
         * д��ʧ��
         * Write card failure
         */
        public static final int WRITE_FAIL = 4;
        /**
         * ��ʱ
         * timeout
         */
        public static final int TIME_OUT = 5;
        /**
         * �����쳣
         * other exception
         */
        public static final int OTHER_EXCEPTION = 6;

        /**
         * ȷ���� 1: �ɹ� 2��Ѱ��ʧ�� 3����֤ʧ�� 4:д��ʧ�� 5����ʱ 6�������쳣
         */
        public int confirmationCode;

        /**
         * �����:��ȷ����Ϊ1ʱ�����ж��Ƿ��н��
         * Results: when the code is 1, then determine whether to have the result
         */
        public Object resultInfo;

        /**
         * ����
         * The card number
         */
        public String num;
    }

}
