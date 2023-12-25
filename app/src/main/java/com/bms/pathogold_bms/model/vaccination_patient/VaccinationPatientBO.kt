package com.bms.pathogold_bms.model.vaccination_patient

import android.os.Parcel
import android.os.Parcelable

class VaccinationPatientBO (var orderno:String,
                            var VaccinationId:String,
                            var vaccinationName:String,
                            var Age:String,
                            var GivenTime:String,
                            var Batch:String,
                            var Make:String,
                            var pepatid:String,
                            var intial:String,
                            var FirstName:String,
                            var LastName:String,
                            var sex:String,
                            var Mob_no:String,
                            var DOB:String,
                            var colourcode:String,
                            var vacc_lowerage:String,
                            var appointmentdate:String,
):Parcelable{
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
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderno)
        parcel.writeString(VaccinationId)
        parcel.writeString(vaccinationName)
        parcel.writeString(Age)
        parcel.writeString(GivenTime)
        parcel.writeString(Batch)
        parcel.writeString(Make)
        parcel.writeString(pepatid)
        parcel.writeString(intial)
        parcel.writeString(FirstName)
        parcel.writeString(LastName)
        parcel.writeString(sex)
        parcel.writeString(Mob_no)
        parcel.writeString(DOB)
        parcel.writeString(colourcode)
        parcel.writeString(vacc_lowerage)
        parcel.writeString(appointmentdate)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "VaccinationPatientBO(orderno='$orderno', VaccinationId='$VaccinationId', vaccinationName='$vaccinationName', Age='$Age', GivenTime='$GivenTime', Batch='$Batch', Make='$Make', pepatid='$pepatid', intial='$intial', FirstName='$FirstName', LastName='$LastName', sex='$sex', Mob_no='$Mob_no', DOB='$DOB', colourcode='$colourcode', vacc_lowerage='$vacc_lowerage', appointmentdate='$appointmentdate')"
    }

    companion object CREATOR : Parcelable.Creator<VaccinationPatientBO> {
        override fun createFromParcel(parcel: Parcel): VaccinationPatientBO {
            return VaccinationPatientBO(parcel)
        }

        override fun newArray(size: Int): Array<VaccinationPatientBO?> {
            return arrayOfNulls(size)
        }
    }

}