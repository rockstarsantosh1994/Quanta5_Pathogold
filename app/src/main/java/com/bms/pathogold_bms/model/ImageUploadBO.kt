package com.bms.pathogold_bms.model

import android.graphics.Bitmap

class ImageUploadBO{

    private var regno: String? = null
    private var tlcode: String? = null
    private var TestName: String? = null
    private var remark: String? = null
    private var Seqno: String? = null
    private var f: String? = null
    private var CompId: String? = null
    private var type: String? = null
    private var location: String? = null
    private var bitmap: Bitmap? = null

    fun getRegno(): String? {
        return regno
    }

    fun setRegno(regno: String?) {
        this.regno = regno
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getTlcode(): String? {
        return tlcode
    }

    fun setTlcode(tlcode: String?) {
        this.tlcode = tlcode
    }

    fun getTestName(): String? {
        return TestName
    }

    fun setTestName(testName: String?) {
        TestName = testName
    }

    fun getRemark(): String? {
        return remark
    }

    fun setRemark(remark: String?) {
        this.remark = remark
    }

    fun getSeqno(): String? {
        return Seqno
    }

    fun setSeqno(seqno: String?) {
        Seqno = seqno
    }

    fun getF(): String? {
        return f
    }

    fun setF(f: String?) {
        this.f = f
    }

    fun getCompId(): String? {
        return CompId
    }

    fun setCompId(compId: String?) {
        CompId = compId
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type =type
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    override fun toString(): String {
        return "ImageUploadBO(regno=$regno, tlcode=$tlcode, TestName=$TestName, remark=$remark, Seqno=$Seqno, f=$f, CompId=$CompId, bitmap=$bitmap)"
    }


}