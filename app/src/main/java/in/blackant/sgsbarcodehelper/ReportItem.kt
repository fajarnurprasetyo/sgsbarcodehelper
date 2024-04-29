package `in`.blackant.sgsbarcodehelper

class ReportItem(val thick: Float, val grade: String, val pcs: Int, var crate: Int) {
    companion object {
        private val GRADE_REGEX = arrayOf(
            Regex("^ut2", RegexOption.IGNORE_CASE),
            Regex("^ut1", RegexOption.IGNORE_CASE),
            Regex("^uty", RegexOption.IGNORE_CASE),
            Regex("^uty-l", RegexOption.IGNORE_CASE),
            Regex("^uty\\+", RegexOption.IGNORE_CASE),
            Regex("^exp", RegexOption.IGNORE_CASE),
        )
    }

    private var _gradeValue: Int = 0
    val gradeValue
        get() :Int {
            return _gradeValue
        }

    init {
        for (regex in GRADE_REGEX) {
            if (regex.matches(grade)) {
                _gradeValue = GRADE_REGEX.indexOf(regex) + 1
                break
            }
        }
    }

    val volume
        get(): Float {
            return thick * 1.22f * 2.44f * pcs / 1000f
        }

    override fun toString(): String {
        return "$grade $pcs = $crate Krat"
    }


    override fun hashCode(): Int {
        var result = thick.hashCode()
        result = 31 * result + grade.hashCode()
        result = 31 * result + pcs
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReportItem

        if (thick != other.thick) return false
        if (grade != other.grade) return false
        if (pcs != other.pcs) return false

        return true
    }
}
