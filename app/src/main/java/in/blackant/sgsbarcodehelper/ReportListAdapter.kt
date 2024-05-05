package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import `in`.blackant.sgsbarcodehelper.databinding.ReportListItemBinding

class ReportListAdapter(private val context: Context, private val deleteDialog: AlertDialog) :
    RecyclerView.Adapter<ReportListAdapter.ViewHolder>(),
    ReportList.Listener {
    val list = ReportList()
    var onReportListChange: (() -> Any)? = null

    init {
        list.addListener(this)
    }

    val local
        get(): ReportList {
            return list.local
        }
    val export
        get(): ReportList {
            return list.export
        }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val binding = ReportListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root, list, deleteDialog)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onItemAdded(index: Int) {
        notifyItemInserted(index)
        notifyItemRangeChanged(list.local.size, list.export.size)
        onReportListChange?.let { it() }
    }

    override fun onItemChanged(index: Int, notify: Boolean) {
        if (notify) notifyItemChanged(index)
        onReportListChange?.let { it() }
    }

    override fun onItemRemoved(index: Int) {
        notifyItemRemoved(index)
        onReportListChange?.let { it() }
    }

    class ViewHolder(
        private val view: ReportItemView,
        private var list: ReportList,
        private val deleteDialog: AlertDialog,
    ) :
        RecyclerView.ViewHolder(view), ReportList.Listener, ReportItemView.Listener {
        private lateinit var item: ReportItem
        private var isLocal: Boolean = false

        fun bind(position: Int) {
            item = list[position]
            isLocal =
                item.grade !is ReportItem.Grade || (item.grade as ReportItem.Grade).value < 100

            val local = list.local
            val export = list.export

            view.removeTitle()
            view.removeSummary()
            view.setListener(null)

            if ((isLocal && local.indexOf(item) == 0) || (!isLocal && export.indexOf(item) == 0)) {
                view.setTitle(if (isLocal) R.string.local else R.string.export)
            }

            view.setLabel("${item.thick} ${item.grade}${if (item.glue.isEmpty()) "" else " ${item.glue}"} @${item.pcs}")
            view.setCrate(item.crate)

            if ((isLocal && local.indexOf(item) == local.size - 1) || (!isLocal && export.indexOf(
                    item
                ) == export.size - 1)
            ) {
                list.addListener(this)
                updateSummary()
            } else list.removeListener(this)

            view.setListener(this)
        }

        private fun updateSummary() {
            view.setSummary(if (isLocal) list.local else list.export)
        }

        override fun onItemAdded(index: Int) {
            updateSummary()
        }

        override fun onItemChanged(index: Int, notify: Boolean) {
            updateSummary()
        }

        override fun onItemRemoved(index: Int) {
            updateSummary()
        }

        override fun onCrateChange(crate: Int) {
            item.crate = crate
        }

        override fun onDelete() {
            deleteDialog.show()
            deleteDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                list.remove(item)
                deleteDialog.dismiss()
            }
        }
    }
}