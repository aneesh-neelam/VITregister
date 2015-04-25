package app.vit.corewise.logic;

import android.os.SystemClock;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import app.vit.corewise.utils.DataUtils;

public class FingerprintAPI {

    /**
     * ȷ���룺0x20��0xefΪ������ȷ���� ��ʾָ��ִ����ϻ�OK��
     * 0x20��0xefThe  for the Reserved
     * Indicates instruction completes or OK
     */
    public static final int EXEC_COMMAND_SUCCESS = 0x00;
    /**
     * ��ʾ���ݰ����մ���
     * Indicates that the data packet reception error;
     */
    public static final int RECEIVE_PACKAGE_ERROR = 0x01;
    /**
     * ��ʾ��������û����ָ��
     * Indicates that the sensor does not have fingers;
     */
    public static final int NO_FINGER = 0x02;
    /**
     * ��ʾ¼��ָ��ͼ��ʧ�ܣ�
     * Input fingerprint image indicates failure;
     */
    public static final int GET_IMAGE_FAIL = 0x03;
    /**
     * ��ʾָ��ͼ��̫�ɡ�̫����������������
     * Represents the fingerprint image is too dry, not too light and health characteristics;
     */
    public static final int FINGERPRINT_SMALL = 0x04;
    /**
     * ��ʾָ��ͼ��̫ʪ��̫����������������
     * Represents the fingerprint image is too wet, not too paste feature is born;
     */
    public static final int FINGERPRINT_BLURRING = 0x05;
    /**
     * ��ʾָ��ͼ��̫�Ҷ�������������
     * Indicates born fingerprint image is too chaotic fragmentation characteristics;
     */
    public static final int FINGERPRINT_NOT_GENCHAR = 0x06;
    /**
     * ��ʾָ��ͼ����������������̫�٣������̫С����������������
     * Represents the fingerprint image is normal, but too few feature points (or too small) was born not characteristic;
     */
    public static final int FINGERPRINT_CHAR_LESS = 0x07;
    /**
     * ��ʾָ�Ʋ�ƥ�䣻
     * Means that fingerprints do not match;
     */
    public static final int FINGERPRINT_NO_MATCH = 0x08;
    /**
     * ��ʾû������ָ�ƣ�
     * Said they were not searching to fingerprint;
     */
    public static final int FINGERPRINT_NO_SEARCH = 0x09;
    /**
     * ��ʾ�����ϲ�ʧ�ܣ�
     * Indicates characteristics merge failed;
     */
    public static final int FINGERPRINT_REG_MODEL_FAIL = 0x0a;
    /**
     * ��ʾ����ָ�ƿ�ʱ��ַ��ų���ָ�ƿⷶΧ��
     * Represents the address number when accessing fingerprint database fingerprint database beyond the scope;
     */
    public static final int FLASH_OUT_OF_INDEX = 0x0b;
    /**
     * ��ʾ��ָ�ƿ��ģ��������Ч��
     * Represents a template from a fingerprint database read error or invalid;
     */
    public static final int READ_MODEL_FROM_FLASH = 0x0c;
    /**
     * ��ʾ�ϴ�����ʧ�ܣ�
     * Uploads a feature to fail;
     */
    public static final int UP_CHAR_FAIL = 0x0d;
    /**
     * ��ʾģ�鲻�ܽ��ܺ������ݰ���
     * Indicates that the module can not accept subsequent packets;
     */
    public static final int NOT_RECEIVE_PACKAGE = 0x0e;
    /**
     * ��ʾ�ϴ�ͼ��ʧ�ܣ�
     * Upload image indicates failure;
     */
    public static final int UP_IMAGE_FAIL = 0x0f;
    /**
     * ��ʾɾ��ģ��ʧ�ܣ�
     * Means to delete the template failed;
     */
    public static final int DELETE_MODEL_FAIL = 0x10;
    /**
     * ��ʾ���ָ�ƿ�ʧ�ܣ�
     * Empty fingerprint database indicates failure;
     */
    public static final int EMPTY_FLASH = 0x11;
    /**
     * ��ʾ���ܽ���͹���״̬��
     * That they can not enter a low power state;
     */
    public static final int NOT_ENTRY_LESS_STATUS = 0x12;
    /**
     * ��ʾ�����ȷ��
     * Means that the password is incorrect;
     */
    public static final int COMMAND_FAIL = 0x13;
    public static final int ERROR1 = 0x14;// ��ʾϵͳ��λʧ�ܣ�
    public static final int ERROR2 = 0x15;// ��ʾ��������û����Чԭʼͼ��������ͼ��
    public static final int ERROR3 = 0x16;// ��ʾ��������ʧ�ܣ�
    public static final int ERROR4 = 0x17;// ��ʾ����ָ�ƻ����βɼ�֮����ָû���ƶ�����
    public static final int ERROR5 = 0x18;// ��ʾ��дFLASH ����
    public static final int ERROR6 = 0xf0;// �к������ݰ���ָ���ȷ���պ���0xf0 Ӧ��
    public static final int ERROR7 = 0xf1;// �к������ݰ���ָ��������0xf1 Ӧ��
    public static final int ERROR8 = 0xf2;// ��ʾ��д�ڲ�FLASH ʱ��У��ʹ���
    public static final int ERROR9 = 0xf3;// ��ʾ��д�ڲ�FLASH ʱ������ʶ����
    public static final int ERROR10 = 0xf4;// ��ʾ��д�ڲ�FLASH ʱ�������ȴ���
    public static final int ERROR11 = 0xf5;// ��ʾ��д�ڲ�FLASH ʱ�����볤��̫����
    public static final int ERROR12 = 0xf6;// ��ʾ��д�ڲ�FLASH ʱ����дFLASH ʧ�ܣ�
    public static final int ERROR13 = 0x19;// δ�������
    public static final int ERROR14 = 0x1a;// ��Ч�Ĵ����ţ�
    public static final int ERROR15 = 0x1b;// �Ĵ����趨���ݴ���ţ�
    public static final int ERROR16 = 0x1c;// ���±�ҳ��ָ������
    public static final int ERROR17 = 0x1d;// �˿ڲ���ʧ�ܣ�
    /**
     * �Զ�ע�ᣨenroll��ʧ�ܣ�
     * Automatically register (enroll) failed;
     */
    public static final int ENROLL_FAIL = 0x1e;
    /**
     * ָ�ƿ���
     * Fingerprint database is full
     */
    public static final int FLASH_FULL = 0x1f;
    /**
     * ����Ӧ
     * No response
     */
    public static final int NO_RESPONSE = 0xff;
    /**
     * ��Ӧ����ͼ�����ݹ�40044�ֽ�
     */
    private static final int UP_IMAGE_RESPONSE_SIZE = 40044;
    private BluetoothChatService chatService;
    /**
     * ������:��λ���������ݴ�ŵ�
     * Buffer: The next crew to return data storage places
     */
    private byte[] data = new byte[1024 * 50];
    private byte[] buffer = new byte[1024 * 50];
    private byte[] bufferImage = new byte[1024 * 50];
    private int index;// ���ݰ�����ʼ�±�
    private int packetNum;// ���ݰ��ĸ���

    public FingerprintAPI(BluetoothChatService chatSerivce) {
        this.chatService = chatSerivce;
    }

    /**
     * ¼��ָ��ͼ�� ����˵���� ̽����ָ��̽�⵽��¼��ָ��ͼ�����ImageBuffer�� ����ȷ�����ʾ��¼��ɹ�������ָ�ȡ�
     *
     * @return Confirmation code
     * Confirmation code=00H Indicates successful entry
     * Confirmation code=01H An indication that the package is wrong
     * Confirmation code=02H Indicates that the sensor is no finger
     * Confirmation code=03H Indicates unsuccessful entry
     * Confirmation code=ffH Indicates no response
     */
    public synchronized int PSGetImage() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x01, 0x00, 0x05};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSGetImage", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * ����˵���� ��ImageBuffer �е�ԭʼͼ������ָ�������ļ�����CharBuffer1 ��CharBuffer2 ���������
     * BufferID(������������)
     *
     * @param bufferId ��CharBuffer1:1h,CharBuffer2:2h��
     * @param bufferId ��CharBuffer1:1h,CharBuffer2:2h��
     * @return Confirmation code
     * Confirmation code=00H Indicates successful generation features
     * Confirmation code=01H An indication that the package is wrong
     * Confirmation code=06H Indicates born fingerprint image is too chaotic fragmentation characteristics
     * Confirmation code=07H Represents the fingerprint image is normal, but too few feature points fragmentation characteristics and health
     * Confirmation code=15H Means that there is no valid image buffer born fragmentation original image image
     * Confirmation code=ffH Indicates no response
     */
    public synchronized int PSGenChar(int bufferId) {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 01, (byte) 0x00, (byte) 0x04,
                (byte) 0x02, (byte) bufferId, (byte) 0x00,
                (byte) (0x7 + bufferId)};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 200);
        printlog("PSGenChar", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * �ϲ���������ģ�壬��CharBuffer1��CharBuffer2�е������ļ��ϲ�����ģ�壬
     * �������CharBuffer1��CharBuffer2�С�
     *
     * @return Confirmation code
     * Confirmation code=00H Means that the merge was successful
     * Confirmation code=01H An indication that the package is wrong
     * Confirmation code=0aH Expressed merge failure (two fingerprints do not belong to the same finger)
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSRegModel() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x05, 0x00, 0x09};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 300);
        printlog("PSRegModel", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * ��CharBuffer�е�ģ�崢�浽ָ����pageId�ŵ�flash���ݿ�λ�� bufferId:ֻ��Ϊ1h��2h
     * pageId����ΧΪ0~1010 ��������� BufferID(��������)��PageID��ָ�ƿ�λ�úţ�
     *
     * @return Confirmation code
     * Confirmation code=00H Expressed successfully saved
     * Confirmation code=01H An indication that the package is wrong
     * Confirmation code=0bH PageID beyond the scope of representation fingerprint database
     * Confirmation code=18H FLASH write error indicates
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSStoreChar(int bufferId, int pageId) {
        byte[] pageIDArray = short2byte((short) pageId);
        // Log.i("whw", "pageid hex=" + DataUtils.toHexString(pageIDArray));
        int checkSum = 0x01 + 0x00 + 0x06 + 0x06 + bufferId
                + (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
        byte[] checkSumArray = short2byte((short) checkSum);
        // Log.i("whw",
        // "checkSumArray hex=" + DataUtils.toHexString(checkSumArray)
        // + "    checkSum=" + checkSum);
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x06, (byte) 0x06, (byte) bufferId,
                (byte) pageIDArray[0], (byte) pageIDArray[1],
                (byte) checkSumArray[0], (byte) checkSumArray[1]};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSStoreChar", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * Specified in the database will flash fingerprint template pageId number read into the stencil buffer CharBuffer1 or CharBuffer2
     * bufferId:1h or 2h pageId��0~1010
     * Input parameters: BufferID (buffer number), PageID (fingerprint database template number)
     *
     * @return Confirmation code
     * Confirmation code=00H Readout indicates success
     * Confirmation code=01H An indication that the package is wrong
     * Confirmation code=0cH Is the readout wrong or wrong template
     * Confirmation code=0BH PageID beyond the scope of representation fingerprint database
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSLoadChar(int bufferId, int pageId) {
        byte[] pageIDArray = short2byte((short) pageId);
        int checkSum = 0x01 + 0x00 + 0x06 + 0x07 + bufferId
                + (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
        byte[] checkSumArray = short2byte((short) checkSum);
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x06, (byte) 0x07, (byte) bufferId,
                (byte) pageIDArray[0], (byte) pageIDArray[1],
                (byte) checkSumArray[0], (byte) checkSumArray[1]};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSLoadChar", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * ��CharBuffer1 ��CharBuffer2 �е������ļ����������򲿷�ָ�ƿ⡣�����������򷵻�ҳ�롣 ��������� BufferID��
     * StartPage(��ʼҳ)��PageNum��ҳ���� ���ز����� ȷ���֣�ҳ�루����ָ��ģ�壩
     *
     * @param bufferId    ������1h��2h
     * @param startPageId ��ʼҳ
     * @param pageNum     ҳ��
     * @param bufferId    buffer:1h��2h
     * @param startPageId
     * @param pageNum
     * @return Confirmation code=00H Indicates that the search to;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=09H  Said they were not searched; this time with a score of 0 page
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized Result PSSearch(int bufferId, int startPageId,
                                        int pageNum) {
        byte[] startPageIDArray = short2byte((short) startPageId);
        byte[] pageNumArray = short2byte((short) pageNum);
        int checkSum = 0x01 + 0x00 + 0x08 + 0x04 + bufferId
                + (startPageIDArray[0] & 0xff) + (startPageIDArray[1] & 0xff)
                + (pageNumArray[0] & 0xff) + (pageNumArray[1] & 0xff);
        byte[] checkSumArray = short2byte((short) checkSum);
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x08, (byte) 0x04, (byte) bufferId,
                (byte) startPageIDArray[0], (byte) startPageIDArray[1],
                (byte) pageNumArray[0], (byte) pageNumArray[1],
                (byte) checkSumArray[0], (byte) checkSumArray[1]};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSSearch", length);
        Result result = new Result();
        if (length == 16) {
            result.code = buffer[9];
            result.pageId = getShort(buffer[10], buffer[11]);
            result.matchScore = getShort(buffer[12], buffer[13]);
        } else {
            result.code = NO_RESPONSE;
        }
        return result;
    }

    /**
     * ��ȷ�ȶ�CharBuffer1��CharBuffer2�е������ļ� ע���:��λ�����ص��������滹��һ���÷֣����÷ִ��ڵ���50ʱ��ָ��ƥ��
     *
     * @return true��Fingerprint matching is successful
     * false��Match fails
     */
    public synchronized boolean PSMatch() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x07};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSMatch", length);
        if (length == 14) {
            if (buffer[9] == 0x00) {
                return score(buffer[10], buffer[11]);
            }
        }
        return false;
    }

    /**
     * �ɼ�һ��ָ��ע��ģ�壬��ָ�ƿ���������λ���洢�����ش洢pageId ���ز����� ȷ���֣�ҳ�루����ָ��ģ�壩
     *
     * @return Confirmation code=00H Indicates successful registration;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=1eH Means that the registration failed.
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized Result PSEnroll() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x14};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSEnroll", length);
        Result result = new Result();
        if (length == 14) {
            result.code = buffer[9];
            result.pageId = getShort(buffer[10], buffer[11]);
        } else {
            result.code = NO_RESPONSE;
        }
        return result;
    }

    /**
     * �Զ��ɼ�ָ�ƣ���ָ�ƿ�������Ŀ��ģ�岢������������� ���Ŀ��ģ��ͬ��ǰ�ɼ���ָ�ƱȶԵ÷ִ�����߷�ֵ��
     * ����Ŀ��ģ��Ϊ�������������Բɼ�����������Ŀ��ģ��Ŀհ����� ���ز����� ȷ���룬ҳ�루����ָ��ģ�壩
     *
     * @return Confirmation code=00H Indicates that the search to;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=09H Said they were not searched; this time with a score of 0 page
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized Result PSIdentify() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x15};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSIdentify", length);
        Result result = new Result();
        if (length == 16) {
            result.code = buffer[9];
            result.pageId = getShort(buffer[10], buffer[11]);
            result.matchScore = getShort(buffer[12], buffer[13]);
        } else {
            result.code = NO_RESPONSE;
        }
        return result;
    }

    /**
     * ɾ��ģ�� ɾ��flash ���ݿ���ָ��ID �ſ�ʼ��N ��ָ��ģ�� ���������PageID(ָ�ƿ�ģ���)��N ɾ����ģ�������
     *
     * @param pageIDStart
     * @param delNum
     * @param pageIDStart
     * @param delNum
     * @return Confirmation code=00H Success means to delete the template;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=10H Means to delete the template failed;
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSDeleteChar(short pageIDStart, short delNum) {
        byte[] pageIDArray = short2byte(pageIDStart);
        byte[] delNumArray = short2byte(delNum);
        int checkSum = 0x01 + 0x07 + 0x0c + (pageIDArray[0] & 0xff)
                + (pageIDArray[1] & 0xff) + (delNumArray[0] & 0xff)
                + (delNumArray[1] & 0xff);
        byte[] checkSumArray = short2byte((short) checkSum);
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x07, (byte) 0x0c, pageIDArray[0], pageIDArray[1],
                delNumArray[0], delNumArray[1], checkSumArray[0],
                checkSumArray[1]};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSDeleteChar", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * ����˵���� ɾ��flash ���ݿ�������ָ��ģ��
     *
     * @return Confirmation code=00H Empty successful representation;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=11H Represents clear failure;
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSEmpty() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x03, (byte) 0x0d, (byte) 0x00, (byte) 0x11};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSEmpty", length);
        if (length == 12) {
            return buffer[9];
        }
        return NO_RESPONSE;
    }

    /**
     * �������������е������ļ��ϴ�����λ��(Ĭ�ϵ�����������Ϊcharbuffer1)
     *
     * @return byte[]��Length success  is 512 bytes
     * null:Characteristics file failed upload
     */
    public synchronized byte[] PSUpChar() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x04, (byte) 0x08, (byte) 0x01, (byte) 0x00, (byte) 0x0e};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 300);
        printlog("PSUpChar", 12);
        // ��ӦΪ12�ֽڣ���4�����ݰ���ÿ����Ϊ139�ֽڣ����Է��ص����ֽ���Ϊ568�ֽ�
        if (length == 568) {
            index = 12;// ���ݰ�����ʼ�±�
            packetNum = 0;
            byte[] packets = new byte[568];
            System.arraycopy(buffer, 0, packets, 0, 568);
            return parsePacketData(packets);
        }
        return null;

    }

    /**
     * ��λ�����������ļ���ģ�������������(Ĭ�ϵĻ�����ΪCharBuffer2)
     *
     * @param model :ָ�Ƶ������ļ�
     * @param model :Fingerprint template
     * @return Confirmation code
     * Confirmation code=00H Means that subsequent packets can be received;
     * Confirmation code=01H  An indication that the package is wrong��
     * Confirmation code=0eH That they can not receive subsequent data packets;
     * Confirmation code=ffH  Indicates no response
     */
    public synchronized int PSDownChar(byte[] model) {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x04, (byte) 0x09, (byte) 0x02, (byte) 0x00, (byte) 0x10};
        sendCommand(command);
        int length = chatService.read(buffer, 4000, 100);
        printlog("PSDownChar", length);
        if (length == 12 && buffer[9] == 0x00) {
            sendData(model);
            return 0x00;
        }
        return NO_RESPONSE;
    }

    /**
     * ��ͼ�񻺳����������ϴ�����λ��
     *
     * @return Return value bmp format image of the fingerprint, if null upload failed
     */
    public synchronized byte[] PSUpImage() {
        byte[] command = {(byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
                (byte) 0x03, (byte) 0x0a, (byte) 0x00, (byte) 0x0e};
        byte[] nextCommand = {'O'};
        int startLength = 3348;
        int dataLength = 3336;
        boolean isStart = true;
        boolean isExit = false;
        int size = 0;
        int length = 0;
        int requestLength = 0;
        while (!isExit) {
            if (isStart) {
                sendCommand(command);
                requestLength = startLength;
                isStart = false;
            } else {
                if (startLength == size || dataLength == size) {

                    System.arraycopy(buffer, 0, bufferImage, length, size);
                    length += size;
                    Log.i("whw", "receive length=" + length);
                    if (length == UP_IMAGE_RESPONSE_SIZE) {
                        sendCommand(nextCommand);
                        isExit = true;
                        break;
                    }
                    sendCommand(nextCommand);
                    requestLength = dataLength;
                } else {
                    Log.i("whw", "----------upimage fail size=" + size);
                    break;
                }

            }
            size = chatService.readImage(buffer, 2000, requestLength);
        }

        Log.i("whw", "PSUpImage length=" + length);
        if (length == UP_IMAGE_RESPONSE_SIZE) {
            byte[] packets = new byte[length];
            System.arraycopy(bufferImage, 0, packets, 0, length);
            index = 12;
            packetNum = 0;
            byte[] data = parsePacketData(packets);
            return getFingerprintImage(data);
        }
        return null;
    }

    /**
     * ����ָ��ģ�����ݰ�512�ֽ�,��Ϊ4�η��ͣ�3�����ݰ���һ�ν�����
     *
     * @param data
     */
    private void sendData(byte[] data) {
        // ���ݰ�ָ��ͷ
        byte[] dataPrefix = {(byte) 0xef, (byte) 0x01, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x02,
                (byte) 0x00, (byte) 0x82};
        // ������ָ��ͷ
        byte[] endPrefix = {(byte) 0xef, (byte) 0x01, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x08,
                (byte) 0x00, (byte) 0x82};
        byte[] command = new byte[dataPrefix.length + 128 + 2];
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                System.arraycopy(endPrefix, 0, command, 0, endPrefix.length);
            } else {
                System.arraycopy(dataPrefix, 0, command, 0, dataPrefix.length);
            }
            System.arraycopy(data, i * 128, command, dataPrefix.length, 128);
            short sum = 0;
            for (int j = 6; j < command.length - 2; j++) {
                sum += (command[j] & 0xff);
            }
            byte[] size = short2byte(sum);
            command[command.length - 2] = size[0];
            command[command.length - 1] = size[1];
            sendCommand(command);
            SystemClock.sleep(50);
        }

    }

    private byte[] parsePacketData(byte[] packet) {
        int dstPos = 0;
        int packageLength = 0;
        int size = 0;
        do {
            packageLength = getShort(packet[index + 7], packet[index + 8]);
            System.arraycopy(packet, index + 9, data, dstPos, packageLength - 2);
            dstPos += packageLength - 2;// 2��У���
            packetNum++;
            size += packageLength - 2;
        } while (moveToNext(index + 6, packageLength, packet));
        if (size != 0) {
            byte[] dataPackage = new byte[size];
            Log.i("whw", "**************packetNum=" + packetNum);
            System.arraycopy(data, 0, dataPackage, 0, size);
            return dataPackage;
        }
        return null;
    }

    private boolean moveToNext(int position, int packageLength, byte[] packet) {
        if (packet[position] == 0x02) {
            index += packageLength + 9;
            return true;
        }
        return false;
    }

    private byte[] getFingerprintImage(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] imageData = new byte[data.length * 2];
        // Log.i("whw", "*****************data.length="+data.length);
        for (int i = 0; i < data.length; i++) {
            imageData[i * 2] = (byte) (data[i] & 0xf0);
            imageData[i * 2 + 1] = (byte) (data[i] << 4 & 0xf0);
        }

        byte[] bmpData = toBmpByte(256, packetNum, imageData);
        return bmpData;
    }

    /**
     * �����ݴ����ڴ�
     */
    private byte[] toBmpByte(int width, int height, byte[] data) {
        byte[] buffer = null;
        try {
            // // ����������ļ�����
            // java.io.FileOutputStream fos = new
            // java.io.FileOutputStream(path);
            // // ����ԭʼ�������������
            // java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // ���ļ�ͷ�ı�����ֵ
            int bfType = 0x424d; // λͼ�ļ����ͣ�0��1�ֽڣ�
            int bfSize = 54 + 1024 + width * height;// bmp�ļ��Ĵ�С��2��5�ֽڣ�
            int bfReserved1 = 0;// λͼ�ļ������֣�����Ϊ0��6-7�ֽڣ�
            int bfReserved2 = 0;// λͼ�ļ������֣�����Ϊ0��8-9�ֽڣ�
            int bfOffBits = 54 + 1024;// �ļ�ͷ��ʼ��λͼʵ������֮����ֽڵ�ƫ������10-13�ֽڣ�

            // �������ݵ�ʱ��Ҫע��������������ڴ���Ҫռ�����ֽڣ�
            // Ȼ����ѡ����Ӧ��д�뷽�������������Լ��������������
            // �����ļ�ͷ����
            dos.writeShort(bfType); // ����λͼ�ļ�����'BM'
            dos.write(changeByte(bfSize), 0, 4); // ����λͼ�ļ���С
            dos.write(changeByte(bfReserved1), 0, 2);// ����λͼ�ļ�������
            dos.write(changeByte(bfReserved2), 0, 2);// ����λͼ�ļ�������
            dos.write(changeByte(bfOffBits), 0, 4);// ����λͼ�ļ�ƫ����

            // ����Ϣͷ�ı�����ֵ
            int biSize = 40;// ��Ϣͷ������ֽ�����14-17�ֽڣ�
            int biWidth = width;// λͼ�Ŀ�18-21�ֽڣ�
            int biHeight = height;// λͼ�ĸߣ�22-25�ֽڣ�
            int biPlanes = 1; // Ŀ���豸�ļ��𣬱�����1��26-27�ֽڣ�
            int biBitcount = 8;// ÿ�����������λ����28-29�ֽڣ���������1λ��˫ɫ����4λ��16ɫ����8λ��256ɫ������24λ�����ɫ��֮һ��
            int biCompression = 0;// λͼѹ�����ͣ�������0����ѹ������30-33�ֽڣ���1��BI_RLEBѹ�����ͣ���2��BI_RLE4ѹ�����ͣ�֮һ��
            int biSizeImage = width * height;// ʵ��λͼͼ��Ĵ�С��������ʵ�ʻ��Ƶ�ͼ���С��34-37�ֽڣ�
            int biXPelsPerMeter = 0;// λͼˮƽ�ֱ��ʣ�ÿ����������38-41�ֽڣ��������ϵͳĬ��ֵ
            int biYPelsPerMeter = 0;// λͼ��ֱ�ֱ��ʣ�ÿ����������42-45�ֽڣ��������ϵͳĬ��ֵ
            int biClrUsed = 256;// λͼʵ��ʹ�õ���ɫ���е���ɫ����46-49�ֽڣ������Ϊ0�Ļ���˵��ȫ��ʹ����
            int biClrImportant = 0;// λͼ��ʾ��������Ҫ����ɫ��(50-53�ֽ�)�����Ϊ0�Ļ���˵��ȫ����Ҫ

            // ��Ϊjava�Ǵ�˴洢����ôҲ����˵ͬ�����������
            // ��������ǰ�С�˶�ȡ��������ǲ��ı���ֽ����ݵ�˳��Ļ�����ô�����Ͳ���������ȡ��
            // �������ȵ��÷�����int����ת��Ϊ���byte���ݣ����Ұ�С�˴洢��˳��

            // ������Ϣͷ����
            dos.write(changeByte(biSize), 0, 4);// ������Ϣͷ���ݵ����ֽ���
            dos.write(changeByte(biWidth), 0, 4);// ����λͼ�Ŀ�
            dos.write(changeByte(biHeight), 0, 4);// ����λͼ�ĸ�
            dos.write(changeByte(biPlanes), 0, 2);// ����λͼ��Ŀ���豸����
            dos.write(changeByte(biBitcount), 0, 2);// ����ÿ������ռ�ݵ��ֽ���
            dos.write(changeByte(biCompression), 0, 4);// ����λͼ��ѹ������
            dos.write(changeByte(biSizeImage), 0, 4);// ����λͼ��ʵ�ʴ�С
            dos.write(changeByte(biXPelsPerMeter), 0, 4);// ����λͼ��ˮƽ�ֱ���
            dos.write(changeByte(biYPelsPerMeter), 0, 4);// ����λͼ�Ĵ�ֱ�ֱ���
            dos.write(changeByte(biClrUsed), 0, 4);// ����λͼʹ�õ�����ɫ��
            dos.write(changeByte(biClrImportant), 0, 4);// ����λͼʹ�ù�������Ҫ����ɫ��

            // �����ɫ������
            byte[] palatte = new byte[1024];
            for (int i = 0; i < 256; i++) {
                palatte[i * 4] = (byte) i;
                palatte[i * 4 + 1] = (byte) i;
                palatte[i * 4 + 2] = (byte) i;
                palatte[i * 4 + 3] = 0;
            }
            dos.write(palatte);

            dos.write(data);
            // �ر����ݵĴ���
            dos.flush();
            buffer = baos.toByteArray();
            dos.close();
            // fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * ��һ��int����תΪ��С��˳�����е��ֽ�����
     *
     * @param data int����
     * @return ��С��˳�����е��ֽ�����
     */
    private byte[] changeByte(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = {b1, b2, b3, b4};
        return bytes;
    }

    private short getShort(byte b1, byte b2) {
        short temp = 0;
        temp |= (b1 & 0xff);
        temp <<= 8;
        temp |= (b2 & 0xff);
        return temp;
    }

    private byte[] short2byte(short s) {
        byte[] size = new byte[2];
        size[1] = (byte) (s & 0xff);
        size[0] = (byte) ((s >> 8) & 0xff);
        return size;
    }

    /**
     * ָ�Ƶ÷ֱȶ�,>=50�ַ���true���ȶԳɹ�
     *
     * @return
     */
    private boolean score(byte b1, byte b2) {
        byte[] temp = {b1, b2};
        short score = 0;
        score |= (temp[0] & 0xff);
        score <<= 8;
        score |= (temp[1] & 0xff);
        return score >= 50;
    }

    private void sendCommand(byte[] commandBytes) {
        chatService.write(commandBytes);
        BluetoothChatService.switchRFID = false;
    }

    private void printlog(String tag, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(buffer, 0, temp, 0, length);
        Log.i("whw", tag + "=" + DataUtils.toHexString(temp));
    }

    public class Result {
        /**
         * ȷ����
         */
        public int code;
        /**
         * ҳ��
         */
        public int pageId;
        /**
         * �÷�
         */
        public int matchScore;
    }

}
