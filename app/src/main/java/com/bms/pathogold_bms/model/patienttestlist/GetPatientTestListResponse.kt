package com.bms.pathogold_bms.model.patienttestlist



class GetPatientTestListResponse(var Message:String,
                                 var ResponseCode:Int,
                                 var ResultArray:ArrayList<GetPatientTestListBO>) {

    override fun toString(): String {
        return "GetPatientTestListResponse(Message='$Message', ResponseCode=$ResponseCode, ResultArray=$ResultArray)"
    }
}