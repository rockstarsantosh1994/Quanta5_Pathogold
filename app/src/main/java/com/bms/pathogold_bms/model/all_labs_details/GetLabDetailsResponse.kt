package com.bms.pathogold_bms.model.all_labs_details

import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO

class GetLabDetailsResponse(var Message:String,
                            var ResponseCode:Int,
                            var ResultArray:ArrayList<GetLabDetailsBO>?=ArrayList())