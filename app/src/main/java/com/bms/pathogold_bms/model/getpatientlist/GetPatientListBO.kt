package com.bms.pathogold_bms.model.getpatientlist

import android.os.Parcel
import android.os.Parcelable

class GetPatientListBO (var regno:String,
                        var PatientName:String,
                        var UserName:String,
                        var Pno:String,
                        var age:String,
                        var MDY:String,
                        var sex:String,
                        var PatientPhoneNo:String,
                        var Dr_name:String,
                        var PePatID:String,
                        var finalteststatus:String,
                        var DOB:String,
                        var Token:String,
                        var status:String,
                        var Samplestatus:String,
                        var Remark:String,
                        var balance:String,
                        var billamount:String,
                        var Entrydate:String, ) : Parcelable {

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
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regno)
        parcel.writeString(PatientName)
        parcel.writeString(UserName)
        parcel.writeString(Pno)
        parcel.writeString(age)
        parcel.writeString(MDY)
        parcel.writeString(sex)
        parcel.writeString(PatientPhoneNo)
        parcel.writeString(Dr_name)
        parcel.writeString(PePatID)
        parcel.writeString(finalteststatus)
        parcel.writeString(DOB)
        parcel.writeString(Token)
        parcel.writeString(status)
        parcel.writeString(Samplestatus)
        parcel.writeString(Remark)
        parcel.writeString(balance)
        parcel.writeString(billamount)
        parcel.writeString(Entrydate)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GetPatientListBO(regno='$regno', PatientName='$PatientName', UserName='$UserName', Pno='$Pno', age='$age', MDY='$MDY', sex='$sex', PatientPhoneNo='$PatientPhoneNo', Dr_name='$Dr_name', PePatID='$PePatID', finalteststatus='$finalteststatus', DOB='$DOB', Token='$Token', status='$status', Samplestatus='$Samplestatus', Remark='$Remark', balance='$balance', billamount='$billamount', Entrydate='$Entrydate')"
    }


    companion object CREATOR : Parcelable.Creator<GetPatientListBO> {
        override fun createFromParcel(parcel: Parcel): GetPatientListBO {
            return GetPatientListBO(parcel)
        }

        override fun newArray(size: Int): Array<GetPatientListBO?> {
            return arrayOfNulls(size)
        }
    }
}

