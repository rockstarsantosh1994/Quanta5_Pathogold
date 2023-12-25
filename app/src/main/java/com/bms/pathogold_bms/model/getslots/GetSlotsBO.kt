package com.bms.pathogold_bms.model.getslots

import android.os.Parcel
import android.os.Parcelable

class GetSlotsBO (val ShiftName:String,
                  val Slot:String):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ShiftName)
        parcel.writeString(Slot)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GetSlotsBO(ShiftName='$ShiftName', Slot='$Slot')"
    }

    companion object CREATOR : Parcelable.Creator<GetSlotsBO> {
        override fun createFromParcel(parcel: Parcel): GetSlotsBO {
            return GetSlotsBO(parcel)
        }

        override fun newArray(size: Int): Array<GetSlotsBO?> {
            return arrayOfNulls(size)
        }
    }


}