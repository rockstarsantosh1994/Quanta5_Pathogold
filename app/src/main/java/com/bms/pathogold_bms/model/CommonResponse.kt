package com.bms.pathogold_bms.model

class CommonResponse (val Message:String, val ResponseCode:Int,  val ResultArray:ArrayList<RegistrationBO>) {
    override fun toString(): String {
        return "CommonResponse(Message='$Message', ResponseCode=$ResponseCode)"
    }
}