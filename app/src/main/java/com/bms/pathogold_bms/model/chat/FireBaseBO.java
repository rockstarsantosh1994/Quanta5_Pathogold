package com.bms.pathogold_bms.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class FireBaseBO implements Parcelable {

    private String name;
    private String image;
    private String uid;
    private String status;
    private String token;

    private String regno;
    private String PatientName;
    private String UserName;
    private String Pno;
    private String age;
    private String MDY;
    private String sex;
    private String PatientPhoneNo;
    private String Dr_name;
    private String PePatID;
    private String Samplestatus;
    private String Token;
    private String type;

    public FireBaseBO(){

    }

    public FireBaseBO(String name, String image, String uid, String status, String token,String type) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
        this.token = token;
        this.type=type;
    }

    public FireBaseBO(String name, String image, String uid, String status, String token, String regno, String patientName, String userName, String pno, String age, String MDY, String sex, String patientPhoneNo, String dr_name, String pePatID, String samplestatus, String token1, String type) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
        this.token = token;
        this.regno = regno;
        this.PatientName = patientName;
        this.UserName = userName;
        this.Pno = pno;
        this.age = age;
        this.MDY = MDY;
        this.sex = sex;
        this.PatientPhoneNo = patientPhoneNo;
        this.Dr_name = dr_name;
        this.PePatID = pePatID;
        this.Samplestatus = samplestatus;
        this.Token = token1;
        this.type = type;
    }

    protected FireBaseBO(Parcel in) {
        name = in.readString();
        image = in.readString();
        uid = in.readString();
        status = in.readString();
        token = in.readString();
        regno = in.readString();
        PatientName = in.readString();
        UserName = in.readString();
        Pno = in.readString();
        age = in.readString();
        MDY = in.readString();
        sex = in.readString();
        PatientPhoneNo = in.readString();
        Dr_name = in.readString();
        PePatID = in.readString();
        Samplestatus = in.readString();
        Token = in.readString();
        type = in.readString();
    }

    public static final Creator<FireBaseBO> CREATOR = new Creator<FireBaseBO>() {
        @Override
        public FireBaseBO createFromParcel(Parcel in) {
            return new FireBaseBO(in);
        }

        @Override
        public FireBaseBO[] newArray(int size) {
            return new FireBaseBO[size];
        }
    };

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPno() {
        return Pno;
    }

    public void setPno(String pno) {
        Pno = pno;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMDY() {
        return MDY;
    }

    public void setMDY(String MDY) {
        this.MDY = MDY;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPatientPhoneNo() {
        return PatientPhoneNo;
    }

    public void setPatientPhoneNo(String patientPhoneNo) {
        PatientPhoneNo = patientPhoneNo;
    }

    public String getDr_name() {
        return Dr_name;
    }

    public void setDr_name(String dr_name) {
        Dr_name = dr_name;
    }

    public String getPePatID() {
        return PePatID;
    }

    public void setPePatID(String pePatID) {
        PePatID = pePatID;
    }

    public String getSamplestatus() {
        return Samplestatus;
    }

    public void setSamplestatus(String samplestatus) {
        Samplestatus = samplestatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", regno='" + regno + '\'' +
                ", PatientName='" + PatientName + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Pno='" + Pno + '\'' +
                ", age='" + age + '\'' +
                ", MDY='" + MDY + '\'' +
                ", sex='" + sex + '\'' +
                ", PatientPhoneNo='" + PatientPhoneNo + '\'' +
                ", Dr_name='" + Dr_name + '\'' +
                ", PePatID='" + PePatID + '\'' +
                ", Samplestatus='" + Samplestatus + '\'' +
                ", Token='" + Token + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FireBaseBO)) return false;
        FireBaseBO that = (FireBaseBO) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(uid);
        parcel.writeString(status);
        parcel.writeString(token);
        parcel.writeString(regno);
        parcel.writeString(PatientName);
        parcel.writeString(UserName);
        parcel.writeString(Pno);
        parcel.writeString(age);
        parcel.writeString(MDY);
        parcel.writeString(sex);
        parcel.writeString(PatientPhoneNo);
        parcel.writeString(Dr_name);
        parcel.writeString(PePatID);
        parcel.writeString(Samplestatus);
        parcel.writeString(Token);
        parcel.writeString(type);
    }
}
