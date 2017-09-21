package jp.hotdrop.comlis.model

class ReceiveCompany {

    var id: String = ""
    var name: String = ""
    var overview: String? = null
    var workPlace: String? = null
    var employeesNum: Int = 0
    var salaryLow: Int = 0
    var salaryHigh: Int = 0
    var dateEpoch: Long = 0

    fun toCompany() = Company().also {
        it.name = name
        it.overview = overview
        it.workPlace = workPlace
        it.employeesNum = employeesNum
        it.salaryLow = salaryLow
        it.salaryHigh = salaryHigh
    }
}