package com.bms.pathogold_bms.model.dailycashregister

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class DailyCashRegisterBO (var exam_date:String="0",
                           var BillNo:String="0",
                           var Modeofpay:String="0",
                           var BillAmt:String="0",
                           var Othercharges:String="0",
                           var TestCharges:String="0",
                           var NetPayment:String="0",
                           var Discount:String="0",
                           var Balance:String="0",
                           var PrevBal:String="0",
                           var testname:String,
                           var username:String,
                           var RegNo:String,
                           var intial:String,
                           var FirstName:String,
                           var LastName:String,
                           var TelNo:String,
                           var labcode:String,
                           var labname:String,
                           var Collection_Center:String,
                           var DocName:String,
                           var patientcount:String?="0",
                           var status:String,
                           var unbillamt:String?="0",
                           var rec_total:String?="0"
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
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DailyCashRegisterBO) return false

        if (exam_date != other.exam_date) return false
        if (BillNo != other.BillNo) return false
        if (Modeofpay != other.Modeofpay) return false
        if (BillAmt != other.BillAmt) return false
        if (Othercharges != other.Othercharges) return false
        if (TestCharges != other.TestCharges) return false
        if (NetPayment != other.NetPayment) return false
        if (Discount != other.Discount) return false
        if (Balance != other.Balance) return false
        if (PrevBal != other.PrevBal) return false
        if (testname != other.testname) return false
        if (username != other.username) return false
        if (RegNo != other.RegNo) return false
        if (intial != other.intial) return false
        if (FirstName != other.FirstName) return false
        if (LastName != other.LastName) return false
        if (TelNo != other.TelNo) return false
        if (labcode != other.labcode) return false
        if (labname != other.labname) return false
        if (Collection_Center != other.Collection_Center) return false
        if (DocName != other.DocName) return false
        if (status != other.status) return false
        if (unbillamt != other.unbillamt) return false
        if (rec_total != other.rec_total) return false

        return true
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exam_date)
        parcel.writeString(BillNo)
        parcel.writeString(Modeofpay)
        parcel.writeString(BillAmt)
        parcel.writeString(Othercharges)
        parcel.writeString(TestCharges)
        parcel.writeString(NetPayment)
        parcel.writeString(Discount)
        parcel.writeString(Balance)
        parcel.writeString(PrevBal)
        parcel.writeString(testname)
        parcel.writeString(username)
        parcel.writeString(RegNo)
        parcel.writeString(intial)
        parcel.writeString(FirstName)
        parcel.writeString(LastName)
        parcel.writeString(TelNo)
        parcel.writeString(labcode)
        parcel.writeString(labname)
        parcel.writeString(Collection_Center)
        parcel.writeString(DocName)
        parcel.writeString(patientcount)
        parcel.writeString(status)
        parcel.writeString(unbillamt)
        parcel.writeString(rec_total)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        var result = exam_date.hashCode()
        result = 31 * result + BillNo.hashCode()
        result = 31 * result + Modeofpay.hashCode()
        result = 31 * result + BillAmt.hashCode()
        result = 31 * result + Othercharges.hashCode()
        result = 31 * result + TestCharges.hashCode()
        result = 31 * result + NetPayment.hashCode()
        result = 31 * result + Discount.hashCode()
        result = 31 * result + Balance.hashCode()
        result = 31 * result + PrevBal.hashCode()
        result = 31 * result + testname.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + RegNo.hashCode()
        result = 31 * result + intial.hashCode()
        result = 31 * result + FirstName.hashCode()
        result = 31 * result + LastName.hashCode()
        result = 31 * result + TelNo.hashCode()
        result = 31 * result + labcode.hashCode()
        result = 31 * result + labname.hashCode()
        result = 31 * result + Collection_Center.hashCode()
        result = 31 * result + DocName.hashCode()
        result = 31 * result + patientcount.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + unbillamt.hashCode()
        result = 31 * result + rec_total.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<DailyCashRegisterBO> {
        override fun createFromParcel(parcel: Parcel): DailyCashRegisterBO {
            return DailyCashRegisterBO(parcel)
        }

        override fun newArray(size: Int): Array<DailyCashRegisterBO?> {
            return arrayOfNulls(size)
        }
    }
}