package com.bms.pathogold_bms.model.all_labs_details

import android.os.Parcel
import android.os.Parcelable

class GetLabDetailsBO(val labcode:String,
                      val labname:String,
                      val labaddress:String,
                      val labphone:String,
                      val labemail:String,
                      val labpincode:String,
                      val city:String,
                      val state:String,
                      val country:String,
                      val webaddress:String,
                      val logo_path:String,
                      val Service_detail:String,
                      val app_type:String,
                      val service_image:String,
                      val Razorpayid:String,
                      val UPIID:String,
                      val Payu_marchantid:String,
                      val Payu_marchantsaltid:String,
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
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(labcode)
        parcel.writeString(labname)
        parcel.writeString(labaddress)
        parcel.writeString(labphone)
        parcel.writeString(labemail)
        parcel.writeString(labpincode)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(country)
        parcel.writeString(webaddress)
        parcel.writeString(logo_path)
        parcel.writeString(Service_detail)
        parcel.writeString(app_type)
        parcel.writeString(service_image)
        parcel.writeString(Razorpayid)
        parcel.writeString(UPIID)
        parcel.writeString(Payu_marchantid)
        parcel.writeString(Payu_marchantsaltid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetLabDetailsBO> {
        override fun createFromParcel(parcel: Parcel): GetLabDetailsBO {
            return GetLabDetailsBO(parcel)
        }

        override fun newArray(size: Int): Array<GetLabDetailsBO?> {
            return arrayOfNulls(size)
        }
    }
}