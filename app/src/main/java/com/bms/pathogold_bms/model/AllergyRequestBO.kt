package com.bms.pathogold_bms.model

class AllergyRequestBO {

    private var PepatId: String? = null
    private var OpdNo: String? = null
    private var Companyid: String? = null
    private var AllergyId: String? = null
    private var AllergyName: String? = null
    private var AllergyCatId: String? = null

    fun getPepatId(): String? {
        return PepatId
    }

    fun setPepatId(pepatId: String?) {
        PepatId = pepatId
    }

    fun getOpdNo(): String? {
        return OpdNo
    }

    fun setOpdNo(opdNo: String?) {
        OpdNo = opdNo
    }

    fun getCompanyid(): String? {
        return Companyid
    }

    fun setCompanyid(companyid: String?) {
        Companyid = companyid
    }

    fun getAllergyId(): String? {
        return AllergyId
    }

    fun setAllergyId(allergyId: String?) {
        AllergyId = allergyId
    }

    fun getAllergyName(): String? {
        return AllergyName
    }

    fun setAllergyName(allergyName: String?) {
        AllergyName = allergyName
    }

    fun getAllergyCatId(): String? {
        return AllergyCatId
    }

    fun setAllergyCatId(allergyCatId: String?) {
        AllergyCatId = allergyCatId
    }
}