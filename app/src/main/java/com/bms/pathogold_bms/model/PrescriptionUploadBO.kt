package com.bms.pathogold_bms.model

class PrescriptionUploadBO {

    private var Pepatid: String? = null
    private var PharmacyDayabaseName: String? = null
    private var OpdNo: String? = null
    private var IpdNo: String? = null
    private var Generic_Id: String? = null
    private var DrugId: String? = null
    private var Dose: String? = null
    private var Day: String? = null
    private var Qty: String? = null
    private var Note: String? = null
    private var Companyid: String? = null
    private var UserName: String? = null
    private var TId: String? = null
    private var Action: String? = null
    private var FinancialYearID: String? = null
    private var TreatmentNo: String? = null
    private var ItemId: String? = null
    private var HivNo: String? = null
    private var ClinicNo: String? = null
    private var RouteID: String? = null
    private var Noteid: String? = null
    private var TDrugName: String? = null

    fun getPepatid(): String? {
        return Pepatid
    }

    fun setPepatid(pepatid: String?) {
        Pepatid = pepatid
    }

    fun getPharmacyDayabaseName(): String? {
        return PharmacyDayabaseName
    }

    fun setPharmacyDayabaseName(pharmacyDayabaseName: String?) {
        PharmacyDayabaseName = pharmacyDayabaseName
    }

    fun getOpdNo(): String? {
        return OpdNo
    }

    fun setOpdNo(opdNo: String?) {
        OpdNo = opdNo
    }

    fun getIpdNo(): String? {
        return IpdNo
    }

    fun setIpdNo(ipdNo: String?) {
        IpdNo = ipdNo
    }

    fun getGeneric_Id(): String? {
        return Generic_Id
    }

    fun setGeneric_Id(generic_Id: String?) {
        Generic_Id = generic_Id
    }

    fun getDrugId(): String? {
        return DrugId
    }

    fun setDrugId(drugId: String?) {
        DrugId = drugId
    }

    fun getDose(): String? {
        return Dose
    }

    fun setDose(dose: String?) {
        Dose = dose
    }

    fun getDay(): String? {
        return Day
    }

    fun setDay(day: String?) {
        Day = day
    }

    fun getQty(): String? {
        return Qty
    }

    fun setQty(qty: String?) {
        Qty = qty
    }

    fun getNote(): String? {
        return Note
    }

    fun setNote(note: String?) {
        Note = note
    }

    fun getCompanyid(): String? {
        return Companyid
    }

    fun setCompanyid(companyid: String?) {
        Companyid = companyid
    }

    fun getUserName(): String? {
        return UserName
    }

    fun setUserName(userName: String?) {
        UserName = userName
    }

    fun getTId(): String? {
        return TId
    }

    fun setTId(TId: String?) {
        this.TId = TId
    }

    fun getAction(): String? {
        return Action
    }

    fun setAction(action: String?) {
        Action = action
    }

    fun getFinancialYearID(): String? {
        return FinancialYearID
    }

    fun setFinancialYearID(financialYearID: String?) {
        FinancialYearID = financialYearID
    }

    fun getTreatmentNo(): String? {
        return TreatmentNo
    }

    fun setTreatmentNo(treatmentNo: String?) {
        TreatmentNo = treatmentNo
    }

    fun getItemId(): String? {
        return ItemId
    }

    fun setItemId(itemId: String?) {
        ItemId = itemId
    }

    fun getHivNo(): String? {
        return HivNo
    }

    fun setHivNo(hivNo: String?) {
        HivNo = hivNo
    }

    fun getClinicNo(): String? {
        return ClinicNo
    }

    fun setClinicNo(clinicNo: String?) {
        ClinicNo = clinicNo
    }

    fun getRouteID(): String? {
        return RouteID
    }

    fun setRouteID(routeID: String?) {
        RouteID = routeID
    }

    fun getNoteid(): String? {
        return Noteid
    }

    fun setNoteid(noteid: String?) {
        Noteid = noteid
    }

    fun getTDrugName(): String? {
        return TDrugName
    }

    fun setTDrugName(TDrugName: String?) {
        this.TDrugName = TDrugName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrescriptionUploadBO) return false

        if (DrugId != other.DrugId) return false

        return true
    }

    override fun hashCode(): Int {
        return DrugId?.hashCode() ?: 0
    }


}