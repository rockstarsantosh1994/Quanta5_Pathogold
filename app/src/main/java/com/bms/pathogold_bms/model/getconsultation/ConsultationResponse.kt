package com.bms.pathogold_bms.model.getconsultation

class ConsultationResponse(val Message:String,
                           val ResponseCode:Int,
                           val ResultArray:ArrayList<ConsultationBO>)