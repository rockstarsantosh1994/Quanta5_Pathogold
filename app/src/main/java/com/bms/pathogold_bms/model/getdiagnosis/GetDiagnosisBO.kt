package com.bms.pathogold_bms.model.getdiagnosis

data class GetDiagnosisBO(var Diagno_name:String,
                          var icd_id:String,
                          var SrNo:String
){
    override fun toString(): String {
        return " $Diagno_name"
    }
}