package `in`.blackant.sgsbarcodehelper

import org.junit.Test

class Test {
    @Test
    fun main() {
        val list = ReportList()
        list.add(ReportItem(ReportItem.Group.LOCAL, 2.7f, ReportItem.Grade.UTY, null, 250, 0))
        list.add(ReportItem(ReportItem.Group.EXPORT, 11.5f, ReportItem.Grade.UTY, "MR", 250, 0))
        println(list.groupBy { it.thick })
    }
}