package com.bms.pathogold_bms.model.getpatientlist

class GetPatientListResponse (var Message:String,
                                var ResponseCode:Int,
                                    var ResultArray:ArrayList<GetPatientListBO>){

    override fun toString(): String {
        return "GetPatientListResponse(Message='$Message', ResponseCode=$ResponseCode, ResultArray=$ResultArray)"
    }
}
