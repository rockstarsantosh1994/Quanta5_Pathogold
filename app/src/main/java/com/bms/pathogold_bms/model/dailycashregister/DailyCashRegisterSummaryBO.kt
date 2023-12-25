package com.bms.pathogold_bms.model.dailycashregister

import android.os.Parcel
import android.os.Parcelable

class DailyCashRegisterSummaryBO (var stCount:String,
                                  var stName:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stCount)
        parcel.writeString(stName)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "DailyCashRegisterSummaryBO(stCount='$stCount', stName='$stName')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DailyCashRegisterSummaryBO) return false

        if (stCount != other.stCount) return false
        if (stName != other.stName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stCount.hashCode()
        result = 31 * result + stName.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<DailyCashRegisterSummaryBO> {
        override fun createFromParcel(parcel: Parcel): DailyCashRegisterSummaryBO {
            return DailyCashRegisterSummaryBO(parcel)
        }

        override fun newArray(size: Int): Array<DailyCashRegisterSummaryBO?> {
            return arrayOfNulls(size)
        }
    }


}