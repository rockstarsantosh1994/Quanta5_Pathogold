package com.bms.pathogold_bms.model.getcomplain

data class GetComplainResponse(var Message:String,
                          var ResponseCode:Int,
                          var ResultArray:ArrayList<GetComplainBO>) {
}