package com.bms.pathogold_bms.model.otp

class OtpResponse (var Message:String,
                   var ResponseCode:Int,
                   var ResultArray:ArrayList<OtpBO>){
    override fun toString(): String {
        return "OtpResponse(Message='$Message', ResponseCode=$ResponseCode, ResultArray=$ResultArray)"
    }
}