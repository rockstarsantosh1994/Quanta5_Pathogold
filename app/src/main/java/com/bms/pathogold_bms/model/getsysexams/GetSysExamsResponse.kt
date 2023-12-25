package com.bms.pathogold_bms.model.getsysexams

data class GetSysExamsResponse(var Message:String,
                               var ResponseCode:Int,
                               var ResultArray:ArrayList<GetSysExamsBO>)
