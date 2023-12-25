package com.bms.pathogold_bms.model.service

import android.os.Parcel
import android.os.Parcelable

class GetServiceBO(
    internal var Servicename: String,
    internal var Servicecode: String,
    internal var merchantid: String,
    internal var gpayid: String,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Servicename)
        parcel.writeString(Servicecode)
        parcel.writeString(merchantid)
        parcel.writeString(gpayid)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GetServiceBO(Servicename='$Servicename', Servicecode='$Servicecode')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetServiceBO) return false

        if (Servicename != other.Servicename) return false
        if (Servicecode != other.Servicecode) return false
        if (merchantid != other.merchantid) return false
        if (gpayid != other.gpayid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Servicename.hashCode()
        result = 31 * result + Servicecode.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<GetServiceBO> {
        override fun createFromParcel(parcel: Parcel): GetServiceBO {
            return GetServiceBO(parcel)
        }

        override fun newArray(size: Int): Array<GetServiceBO?> {
            return arrayOfNulls(size)
        }
    }


}