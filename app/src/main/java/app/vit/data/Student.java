package app.vit.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    private String registerNumber;
    private int fingerprintPageId;
    private String rfid;

    public Student(String registerNumber) {
        this.registerNumber = registerNumber;
        this.fingerprintPageId = -1;
    }

    protected Student(Parcel in) {
        registerNumber = in.readString();
        fingerprintPageId = in.readInt();
        rfid = in.readString();
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public int getFingerprintPageId() {
        return fingerprintPageId;
    }

    public void setFingerprintPageId(int fingerprintPageId) {
        this.fingerprintPageId = fingerprintPageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(registerNumber);
        dest.writeInt(fingerprintPageId);
        dest.writeString(rfid);
    }
}
