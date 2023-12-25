package com.bms.pathogold_bms.model.getallergylist

import android.os.Parcel
import android.os.Parcelable

class GetAllergyListBO (val AllergyId:String,
                        val AllergyName:String,
                        val AllergyCategoryId:String ):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun toString(): String {
        return "GetAllergyListBO(AllergyId='$AllergyId', AllergyName='$AllergyName', AllergyCategoryId='$AllergyCategoryId')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(AllergyId)
        parcel.writeString(AllergyName)
        parcel.writeString(AllergyCategoryId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetAllergyListBO) return false

        if (AllergyId != other.AllergyId) return false

        return true
    }

    override fun hashCode(): Int {
        return AllergyId.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<GetAllergyListBO> {
        override fun createFromParcel(parcel: Parcel): GetAllergyListBO {
            return GetAllergyListBO(parcel)
        }

        override fun newArray(size: Int): Array<GetAllergyListBO?> {
            return arrayOfNulls(size)
        }
    }


}