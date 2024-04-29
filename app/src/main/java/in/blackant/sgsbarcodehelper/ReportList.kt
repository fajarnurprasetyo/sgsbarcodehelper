package `in`.blackant.sgsbarcodehelper

class ReportList(private var listener: ReportListListener?) : ArrayList<ReportItem>() {
    override fun add(element: ReportItem): Boolean {
        for (item in this) {
            if (item == element) {
                item.crate += element.crate
                listener?.onItemChanged(this, indexOf(item))
                return true
            }
        }
        if (super.add(element)) {
            sortWith(compareBy<ReportItem> { it.thick }.thenByDescending { it.gradeValue }.thenBy { it.pcs })
            listener?.onItemAdded(this, indexOf(element))
            return true
        }
        return false
    }

    override fun remove(element: ReportItem): Boolean {
        val index = indexOf(element)
        if (super.remove(element)) {
            listener?.onItemRemoved(this, index)
            return true
        }
        return false
    }

    val pcs
        get(): Int {
            return fold(0) { result, item -> result + (item.pcs * item.crate) }
        }

    val crate
        get(): Int {
            return fold(0) { result, item -> result + item.crate }
        }

    val volume
        get(): Float {
            return fold(0f) { result, item -> result + (item.volume * item.crate) }
        }

    val grouped
        get():Map<Float, ReportList> {
            return fold(mutableMapOf()) { result, item ->
                if (!result.containsKey(item.thick)) {
                    result[item.thick] = ReportList(null)
                }
                result[item.thick]!!.add(item)
                result
            }
        }

    interface ReportListListener {
        fun onItemAdded(list: ReportList, index: Int)
        fun onItemChanged(list: ReportList, index: Int)
        fun onItemRemoved(list: ReportList, index: Int)
    }
}