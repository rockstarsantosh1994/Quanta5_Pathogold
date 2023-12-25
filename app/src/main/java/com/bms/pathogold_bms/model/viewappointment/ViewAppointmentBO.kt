package com.bms.pathogold_bms.model.viewappointment

import android.os.Parcel
import android.os.Parcelable

class ViewAppointmentBO (val BookApp_Id:String,
                         val PatientName:String,
                         val FullAddress:String,
                         val Area:String,
                         val Mobileno:String,
                         val Testname:String,
                         val Price:String,
                         val Day:String,
                         val Timeslot:String,
                         val Status:String,
                         val Remark:String,
                         val Advance:String,
                         val Age:String,
                         val MDY:String,
                         val Gender:String,

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(BookApp_Id)
        parcel.writeString(PatientName)
        parcel.writeString(FullAddress)
        parcel.writeString(Area)
        parcel.writeString(Mobileno)
        parcel.writeString(Testname)
        parcel.writeString(Price)
        parcel.writeString(Day)
        parcel.writeString(Timeslot)
        parcel.writeString(Status)
        parcel.writeString(Remark)
        parcel.writeString(Advance)
        parcel.writeString(Age)
        parcel.writeString(MDY)
        parcel.writeString(Gender)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViewAppointmentBO> {
        override fun createFromParcel(parcel: Parcel): ViewAppointmentBO {
            return ViewAppointmentBO(parcel)
        }

        override fun newArray(size: Int): Array<ViewAppointmentBO?> {
            return arrayOfNulls(size)
        }
    }
}