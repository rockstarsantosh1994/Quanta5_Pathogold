package com.bms.pathogold_bms.model.login

class LoginResponse(var Message:String,
                    var ResponseCode:Int,
                    var ResultArray:ArrayList<LoginBO>


) {
    override fun toString(): String {
        return "LoginResponse(Message='$Message', ResponseCode=$ResponseCode, ResultArray=$ResultArray)"
    }
}



