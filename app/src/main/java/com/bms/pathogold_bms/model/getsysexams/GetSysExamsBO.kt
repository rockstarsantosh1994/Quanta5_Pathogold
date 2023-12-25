package com.bms.pathogold_bms.model.getsysexams

data class GetSysExamsBO(var sysexam:String,
                         var sysexam_detail:String,
                         var examid:String,
                         var examdetailid:String,
                         var isSelected:Boolean = false
){

}
