package com.bms.pathogold_bms.model

data class PayuSuccessResponse(val txnid:String,
                               val amount:String,
                               val productinfo:String,
                               val firstname:String,
                               val email:String,
                               val phone:String,
                               val status:String,
                               val mode:String,
                               val cardCategory:String,
                               val addedon:String,

)
