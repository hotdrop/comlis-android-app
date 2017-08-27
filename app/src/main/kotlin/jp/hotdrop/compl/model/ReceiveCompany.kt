package jp.hotdrop.compl.model

class ReceiveCompany {

    var id: String = ""
    var name: String = ""
    var overview: String? = null
    var workPlace: String? = null
    var employeesNum: Int = 0
    var salaryLow: Int = 0
    var salaryHigh: Int = 0

    fun toCompany(): Company {
        return Company().apply {
            name = name
            overview = overview
            workPlace = workPlace
            employeesNum = employeesNum
            salaryLow = salaryLow
            salaryHigh = salaryHigh
        }
    }
}