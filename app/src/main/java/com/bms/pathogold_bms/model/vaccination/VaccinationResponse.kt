package com.bms.pathogold_bms.model.vaccination

class VaccinationResponse(val Message:String,
                          val ResponseCode:Int,
                          val ResultArray:ArrayList<VaccinationBO>)