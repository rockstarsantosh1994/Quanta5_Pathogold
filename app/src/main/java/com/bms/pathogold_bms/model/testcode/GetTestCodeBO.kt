package com.bms.pathogold_bms.model.testcode

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class GetTestCodeBO(
    val tlcode: String,
    val title: String,
    val rate: String,
    var isSelected: String? = "false",
):Parcelable {
/*
    private var isSelected:Boolean = false*/
    /*fun getSelected(): String {
        return isSelected
    }

    @JvmName("setSelected1")
    fun setSelected(selected: Boolean) {
        isSelected = selected.toString()
    }*/

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())


    @SuppressLint("NewApi")
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tlcode)
        parcel.writeString(title)
        parcel.writeString(rate)
        parcel.writeString(isSelected)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "$tlcode'.'$title' Rs'$rate'/-\n"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetTestCodeBO) return false

        if (tlcode != other.tlcode) return false
        if (title != other.title) return false
        if (rate != other.rate) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tlcode.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + rate.hashCode()
        result = 31 * result + isSelected.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<GetTestCodeBO> {
        override fun createFromParcel(parcel: Parcel): GetTestCodeBO {
            return GetTestCodeBO(parcel)
        }

        override fun newArray(size: Int): Array<GetTestCodeBO?> {
            return arrayOfNulls(size)
        }
    }


}