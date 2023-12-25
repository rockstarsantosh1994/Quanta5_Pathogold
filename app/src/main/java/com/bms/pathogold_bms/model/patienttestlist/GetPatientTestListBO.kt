package com.bms.pathogold_bms.model.patienttestlist

import android.os.Parcel
import android.os.Parcelable

class GetPatientTestListBO (var TLCode:String,
                            var Title:String) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun toString(): String {
        return "GetPatientTestListBO(TLCode='$TLCode', Title='$Title')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(TLCode)
        parcel.writeString(Title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetPatientTestListBO> {
        override fun createFromParcel(parcel: Parcel): GetPatientTestListBO {
            return GetPatientTestListBO(parcel)
        }

        override fun newArray(size: Int): Array<GetPatientTestListBO?> {
            return arrayOfNulls(size)
        }
    }
}
