package com.bms.pathogold_bms.model.getprovisionaldiagnosis

data class GetProvDiagnosisBO(var Diagno_name:String,
                              var icd_id:String,
                              var SrNo:String
                              ){
    override fun toString(): String {
        return " $Diagno_name"
    }
}
