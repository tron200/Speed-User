package com.speed.user.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MPesaNumber implements Parcelable {
    public static final Creator<MPesaNumber> CREATOR = new Creator<MPesaNumber>() {
        @Override
        public MPesaNumber createFromParcel(Parcel in) {
            return new MPesaNumber(in);
        }

        @Override
        public MPesaNumber[] newArray(int size) {
            return new MPesaNumber[size];
        }
    };
    private String mPesaNumber;

    public MPesaNumber() {

    }

    protected MPesaNumber(Parcel in) {
        mPesaNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPesaNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getmPesaNumber() {
        return mPesaNumber;
    }

    public void setmPesaNumber(String mPesaNumber) {
        this.mPesaNumber = mPesaNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MPesaNumber mNumber = (MPesaNumber) o;

        return mPesaNumber != null ? mPesaNumber.equals(mNumber.mPesaNumber) : mNumber.mPesaNumber == null;

    }

    @Override
    public int hashCode() {
        return mPesaNumber != null ? mPesaNumber.hashCode() : 0;
    }
}
