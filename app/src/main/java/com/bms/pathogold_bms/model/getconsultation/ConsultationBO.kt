package com.bms.pathogold_bms.model.getconsultation

import android.os.Parcel
import android.os.Parcelable

class ConsultationBO(val Pno:String,
                     val Name:String,
                     val token:String ) :Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Pno)
        parcel.writeString(Name)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ConsultationBO(Pno='$Pno', Name='$Name', token='$token')"
    }

    companion object CREATOR : Parcelable.Creator<ConsultationBO> {
        override fun createFromParcel(parcel: Parcel): ConsultationBO {
            return ConsultationBO(parcel)
        }

        override fun newArray(size: Int): Array<ConsultationBO?> {
            return arrayOfNulls(size)
        }
    }


}