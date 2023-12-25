package com.bms.pathogold_bms.model.getrefcc

import android.os.Parcel
import android.os.Parcelable

class GetRefCCBO (val code:String,
                  val name:String,
                  val defautflag:String,
                  val Check_Flag:String,
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeString(defautflag)
        parcel.writeString(Check_Flag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetRefCCBO> {
        override fun createFromParcel(parcel: Parcel): GetRefCCBO {
            return GetRefCCBO(parcel)
        }

        override fun newArray(size: Int): Array<GetRefCCBO?> {
            return arrayOfNulls(size)
        }
    }

}