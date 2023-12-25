package com.bms.pathogold_bms.model.lab_super_admin

import android.os.Parcel
import android.os.Parcelable

class LabNameBO (val labname:String,
                 val labname_c:String):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(labname)
        parcel.writeString(labname_c)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "LabNameBO(labname='$labname', labname_c='$labname_c')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LabNameBO) return false

        if (labname != other.labname) return false
        if (labname_c != other.labname_c) return false

        return true
    }

    override fun hashCode(): Int {
        var result = labname.hashCode()
        result = 31 * result + labname_c.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<LabNameBO> {
        override fun createFromParcel(parcel: Parcel): LabNameBO {
            return LabNameBO(parcel)
        }

        override fun newArray(size: Int): Array<LabNameBO?> {
            return arrayOfNulls(size)
        }
    }


}