package com.bms.pathogold_bms.model.getclientuploadedfile

class GetClientUploadedBO ( var type:String,
                            var note:String,
                            var url:String,
                            var DateOfEntry:String,
                            var intial:String,
                            var FirstName:String,
                            var LastName:String,
                            var sex:String,
                            var Age:String,
                            var MDY:String,
                            var isPlaying:Boolean=false){
 override fun toString(): String {
  return "GetClientUploadedBO(type='$type', note='$note', url='$url', DateOfEntry='$DateOfEntry', intial='$intial', FirstName='$FirstName', LastName='$LastName', sex='$sex', Age='$Age', MDY='$MDY')"
 }
}
