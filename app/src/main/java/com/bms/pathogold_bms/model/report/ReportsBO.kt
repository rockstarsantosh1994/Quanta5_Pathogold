package com.bms.pathogold_bms.model.report

import android.os.Parcel
import android.os.Parcelable

class ReportsBO (var PFId:String,
                 var url:String,
                 var Companyid:String,
                 var regno:String,
                 var note:String,
                 var Pepatid:String,
                 var type:String,
                 var LocationNew:String,
                 var intial:String,
                 var FirstName:String,
                 var LastName:String,
                 var sex:String,
                 var MDY:String,
                 var RefDr:String,
                 var testname:String,
                 var Pno:String,
                 var Dr_name:String,
                 var Perment_PateintID:String,
                 var PatientPhoneNo:String,
                 var balance:String,
                 var entrydate:String,
) : Parcelable{

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
        parcel.readString().toString(),
        parcel.readString().toString())



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(PFId)
        parcel.writeString(url)
        parcel.writeString(Companyid)
        parcel.writeString(regno)
        parcel.writeString(note)
        parcel.writeString(Pepatid)
        parcel.writeString(type)
        parcel.writeString(LocationNew)
        parcel.writeString(intial)
        parcel.writeString(FirstName)
        parcel.writeString(LastName)
        parcel.writeString(sex)
        parcel.writeString(MDY)
        parcel.writeString(RefDr)
        parcel.writeString(testname)
        parcel.writeString(Pno)
        parcel.writeString(Dr_name)
        parcel.writeString(Perment_PateintID)
        parcel.writeString(PatientPhoneNo)
        parcel.writeString(balance)
        parcel.writeString(entrydate)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ReportsBO(PFId='$PFId', url='$url', Companyid='$Companyid', regno='$regno', note='$note', Pepatid='$Pepatid', type='$type', LocationNew='$LocationNew', intial='$intial', FirstName='$FirstName', LastName='$LastName', sex='$sex', MDY='$MDY', RefDr='$RefDr', testname='$testname', Pno='$Pno', Dr_name='$Dr_name', Perment_PateintID='$Perment_PateintID', PatientPhoneNo='$PatientPhoneNo', balance='$balance', entrydate='$entrydate')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReportsBO) return false

        if (PFId != other.PFId) return false
        if (url != other.url) return false
        if (Companyid != other.Companyid) return false
        if (regno != other.regno) return false
        if (note != other.note) return false
        if (Pepatid != other.Pepatid) return false
        if (type != other.type) return false
        if (LocationNew != other.LocationNew) return false
        if (intial != other.intial) return false
        if (FirstName != other.FirstName) return false
        if (LastName != other.LastName) return false
        if (sex != other.sex) return false
        if (MDY != other.MDY) return false
        if (RefDr != other.RefDr) return false
        if (testname != other.testname) return false
        if (Pno != other.Pno) return false
        if (Dr_name != other.Dr_name) return false
        if (Perment_PateintID != other.Perment_PateintID) return false
        if (PatientPhoneNo != other.PatientPhoneNo) return false
        if (balance != other.balance) return false
        if (entrydate != other.entrydate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = PFId.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + Companyid.hashCode()
        result = 31 * result + regno.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + Pepatid.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + LocationNew.hashCode()
        result = 31 * result + intial.hashCode()
        result = 31 * result + FirstName.hashCode()
        result = 31 * result + LastName.hashCode()
        result = 31 * result + sex.hashCode()
        result = 31 * result + MDY.hashCode()
        result = 31 * result + RefDr.hashCode()
        result = 31 * result + testname.hashCode()
        result = 31 * result + Pno.hashCode()
        result = 31 * result + Dr_name.hashCode()
        result = 31 * result + Perment_PateintID.hashCode()
        result = 31 * result + PatientPhoneNo.hashCode()
        result = 31 * result + balance.hashCode()
        result = 31 * result + entrydate.hashCode()
        return result
    }


    companion object CREATOR : Parcelable.Creator<ReportsBO> {
        override fun createFromParcel(parcel: Parcel): ReportsBO {
            return ReportsBO(parcel)
        }

        override fun newArray(size: Int): Array<ReportsBO?> {
            return arrayOfNulls(size)
        }
    }
}