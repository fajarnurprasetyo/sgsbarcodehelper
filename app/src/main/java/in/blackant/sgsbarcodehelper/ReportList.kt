package `in`.blackant.sgsbarcodehelper

class ReportList : ArrayList<ReportItem> {
    private var listener: ReportListListener? = null

    constructor()
    constructor(listener: ReportListListener) {
        this.listener = listener
    }

    companion object {
        fun from(items: List<ReportItem>): ReportList {
            val list = ReportList()
            list.addAll(items)
            return list
        }
    }

    override fun add(element: ReportItem): Boolean {
        for (item in this) {
            if (item == element) {
                item.crate += element.crate
                listener?.onItemChanged(indexOf(item))
                return true
            }
        }
        if (super.add(element)) {
            sortWith(compareBy<ReportItem> { if (it.grade is ReportItem.Grade) it.grade.value else 0 }
                .thenBy { it.thick }
                .thenByDescending { if (it.grade is ReportItem.Grade) it.grade.value else 0 }
                .thenBy { it.pcs }
            )
            listener?.onItemAdded(indexOf(element))
            return true
        }
        return false
    }

    override fun remove(element: ReportItem): Boolean {
        val index = indexOf(element)
        if (super.remove(element)) {
            listener?.onItemRemoved(index)
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

    val local
        get():ReportList {
            return from(filter { it.grade !is ReportItem.Grade || it.grade.value < 100 })
        }

    val export
        get():ReportList {
            return from(filter { it.grade is ReportItem.Grade && it.grade.value > 100 })
        }

    @Suppress("unused", "RedundantSuppression")
    fun groupBy(selector: (ReportItem) -> Any):Map<Any, ReportList> {
            return fold(mutableMapOf()) { result, item ->
                val key = selector(item)
                println(key)
                if (!result.containsKey(key)) {
                    result[key] = ReportList()
                }
                result[key]!!.add(item)
                result
            }
        }

    interface ReportListListener {
        fun onItemAdded(index: Int)
        fun onItemChanged(index: Int)
        fun onItemRemoved(index: Int)
    }
}