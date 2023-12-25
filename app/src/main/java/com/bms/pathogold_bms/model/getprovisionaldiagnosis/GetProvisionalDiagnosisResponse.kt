package com.bms.pathogold_bms.model.getprovisionaldiagnosis

data class GetProvisionalDiagnosisResponse(var Message:String,
                                           var ResponseCode:Int,
                                           var ResultArray:ArrayList<GetProvDiagnosisBO>)
