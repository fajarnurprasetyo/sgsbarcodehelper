package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener

class ReportItemView : LinearLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    private lateinit var titleContainer: View
    private lateinit var titleView: TextView
    private lateinit var labelView: TextView
    private lateinit var crateView: EditText
    private lateinit var summaryContainer: View
    private lateinit var summaryCrateView: TextView
    private lateinit var summaryPcsView: TextView
    private lateinit var summaryVolumeView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleContainer = findViewById(R.id.title_container)
        titleView = findViewById(R.id.title)
        labelView = findViewById(R.id.label)
        crateView = findViewById(R.id.crate)
        summaryContainer = findViewById(R.id.summary_container)
        summaryCrateView = summaryContainer.findViewById(R.id.total_crate)
        summaryPcsView = summaryContainer.findViewById(R.id.total_pcs)
        summaryVolumeView = summaryContainer.findViewById(R.id.total_volume)

        crateView.addTextChangedListener {
            listener?.onCrateChange(
                if (it.isNullOrEmpty()) 0 else it.toString().toInt()
            )
        }
        findViewById<Button>(R.id.clear).setOnClickListener { crateView.setText("0") }
        findViewById<Button>(R.id.delete).setOnClickListener { listener?.onDelete() }
    }


    fun setTitle(@StringRes title: Int) {
        titleView.setText(title)
        titleContainer.visibility = View.VISIBLE
    }

    fun removeTitle() {
        titleContainer.visibility = View.GONE
    }

    fun setLabel(label: String) {
        labelView.text = label
    }

    fun setCrate(crate: Int) {
        crateView.setText(crate.toString())
    }

    fun setSummary(list: ReportList) {
        summaryCrateView.text = context.getString(R.string.crate_value, list.crate)
        summaryPcsView.text = context.getString(R.string.pcs_value, list.pcs)
        summaryVolumeView.text = context.getString(R.string.volume_value, list.volume)
        summaryContainer.visibility = View.VISIBLE
    }

    fun removeSummary() {
        summaryContainer.visibility = View.GONE
    }

    interface Listener {
        fun onCrateChange(crate: Int)
        fun onDelete()
    }
}