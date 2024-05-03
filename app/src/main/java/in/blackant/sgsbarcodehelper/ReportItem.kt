package `in`.blackant.sgsbarcodehelper

class ReportItem(
    val thick: Float,
    val grade: Any,
    private val _glue: Any,
    val pcs: Int,
    var crate: Int
) {
    val glue = if (_glue is Glue) _glue.value else _glue.toString()
    val volume
        get(): Float {
            return thick * 1.22f * 2.44f * pcs / 1000f
        }

    override fun hashCode(): Int {
        var result = thick.hashCode()
        result = 31 * result + grade.hashCode()
        result = 31 * result + _glue.hashCode()
        result = 31 * result + pcs
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReportItem

        if (thick != other.thick) return false
        if (grade != other.grade) return false
        if (_glue != other._glue) return false
        if (pcs != other.pcs) return false

        return true
    }

    enum class Thick(val value: Float, val pcs: Array<Int>) {
        PLY_027(2.7f, arrayOf(250, 450)),
        PLY_034(3.4f, arrayOf(270)),
        PLY_036(3.6f, arrayOf(200, 330)),
        PLY_046(4.6f, arrayOf(210)),
        PLY_050(5.0f, arrayOf(150, 240)),
        PLY_052(5.2f, arrayOf(210)),
        PLY_075(7.5f, arrayOf(84, 100, 141)),
        PLY_085(8.5f, arrayOf(74, 90, 125)),
        PLY_115(11.5f, arrayOf(56, 70, 94)),
        PLY_145(14.5f, arrayOf(44, 72, 73)),
        PLY_150(15.0f, arrayOf(50)),
        PLY_175(17.5f, arrayOf(36, 61)),
        PLY_180(18.0f, arrayOf(40, 50));

        override fun toString(): String {
            return value.toString()
        }

        companion object {
            fun fromFloat(thick: Float): Thick? {
                for (item in entries) {
                    if (thick == item.value) {
                        return item
                    }
                }
                return null
            }

            val plywood
                get() = arrayOf(
                    PLY_027,
                    PLY_034,
                    PLY_036,
                    PLY_046,
                    PLY_050,
                    PLY_052,
                    PLY_075,
                    PLY_085,
                    PLY_115,
                    PLY_145,
                    PLY_150,
                    PLY_175,
                    PLY_180,
                )
        }
    }

    enum class Grade(val label: String, val value: Int) {
        // Plywood
        UT2("UT2", 0),
        UT2_DGE("UT2 DGE", 1),
        UT1("UT1", 2),
        UT1_DGE("UT1 DGE", 3),
        UTY("UTY", 4),
        UTY_L("UTY-L", 5),
        UTY_PLUS("UTY+", 101),
        UTY_E("EXP", 102),
        BBCC("BBCC", 103),

        // Veneer
        LC("LC", 0);

        override fun toString(): String {
            return label
        }

        companion object {
            fun fromString(grade: String): Any {
                for (item in entries) {
                    if (grade == item.label) {
                        return item
                    }
                }
                return grade
            }

            val plywood = arrayOf(
                BBCC,
                UTY_E,
                UTY_PLUS,
                UTY_L,
                UTY,
                UT1_DGE,
                UT1,
                UT2_DGE,
                UT2,
            )

            val veneer = arrayOf(
                LC
            )
        }
    }

    enum class Glue(val value:String) {
        MR("MR"),
        E1("E1"),
        E2("E2"),
        CARB("CARB");

        companion object {
            fun fromString(glue: String): Any {
                for (item in entries) {
                    if (glue == item.value) {
                        return item
                    }
                }
                return glue
            }
        }
    }
}
