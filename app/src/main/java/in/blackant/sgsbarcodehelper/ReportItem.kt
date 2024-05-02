package `in`.blackant.sgsbarcodehelper

class ReportItem(
    val group: Group,
    val thick: Float,
    val grade: Any,
    val type: String,
    val pcs: Int,
    var crate: Int
) {
    val volume
        get(): Float {
            return thick * 1.22f * 2.44f * pcs / 1000f
        }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + thick.hashCode()
        result = 31 * result + grade.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + pcs
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReportItem

        if (group != other.group) return false
        if (thick != other.thick) return false
        if (grade != other.grade) return false
        if (type != other.type) return false
        if (pcs != other.pcs) return false

        return true
    }

    enum class Group(val type: Int) {
        LOCAL(0),
        EXPORT(1),
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
        BBCC("BBCC", 0),
        UT2("UT2", 1),
        UT1("UT1", 2),
        UTY("UTY", 3),
        UTY_L("UTY-L", 4),
        UTY_PLUS("UTY+", 5),
        UTY_E("EXP", 6),

        // Veneer
        LC("LC", 0);

        override fun toString(): String {
            return label
        }

        companion object {
            fun fromString(grade: String): Grade? {
                for (item in entries) {
                    if (grade == item.label) {
                        return item
                    }
                }
                return null
            }

            val plywood = arrayOf(
                UTY_E,
                UTY_PLUS,
                UTY_L,
                UTY,
                UT1,
                UT2,
                BBCC,
            )

            val veneer = arrayOf(
                LC
            )
        }
    }
}
