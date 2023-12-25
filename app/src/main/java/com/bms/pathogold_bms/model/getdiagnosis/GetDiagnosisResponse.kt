package com.bms.pathogold_bms.model.getdiagnosis

data class GetDiagnosisResponse(var Message:String,
                                var ResponseCode:Int,
                                var ResultArray:ArrayList<GetDiagnosisBO>)