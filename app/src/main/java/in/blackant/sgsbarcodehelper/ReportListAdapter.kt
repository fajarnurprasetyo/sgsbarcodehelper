package `in`.blackant.sgsbarcodehelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.ReportListItemBinding

class ReportListAdapter(context: Context) : RecyclerView.Adapter<ReportListAdapter.ViewHolder>(),
    ReportList.ReportListListener {
    private val deleteDialog: AlertDialog
    val list = ReportList(this)
    var onReportListChange: (() -> Any)? = null

    init {
        deleteDialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.delete_item_title)
            .setMessage(R.string.delete_item_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete, null)
            .setCancelable(false)
            .create()
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
        return ViewHolder(
            ReportListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val isFirst = list.local.indexOf(item) == 0 || list.export.indexOf(item) == 0
        holder.bind(item, isFirst, onReportListChange, deleteDialog) {
            list.remove(item)
            onReportListChange?.let { it() }
        }
    }

    override fun onItemAdded(index: Int) {
        notifyItemInserted(index)
        onReportListChange?.let { it() }
    }

    override fun onItemChanged(index: Int) {
        notifyItemChanged(index)
        onReportListChange?.let { it() }
    }

    override fun onItemRemoved(index: Int) {
        notifyItemRemoved(index)
        onReportListChange?.let { it() }
    }

    class ViewHolder(private val binding: ReportListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var item: ReportItem? = null
        private var onChange: (() -> Any?)? = null
        private var deleteDialog: AlertDialog? = null
        private var onDelete: (() -> Any?)? = null

        init {
            binding.clear.setOnClickListener { binding.crate.setText("0") }
            binding.crate.addTextChangedListener {
                item?.crate = if (!it.isNullOrEmpty()) it.toString().toInt() else 0
                onChange?.let { it() }
            }
            binding.delete.setOnClickListener {
                deleteDialog?.show()
                    deleteDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                        onDelete?.let { it() }
                        deleteDialog!!.dismiss()
                    }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            item: ReportItem,
            isFirst: Boolean,
            onChange: (() -> Any?)?,
            deleteDialog: AlertDialog,
            onDelete: (() -> Any?)?,
        ) {
            this.item = null
            this.onChange = null
            this.deleteDialog = null
            this.onDelete = null
            if (isFirst) {
                binding.title.setText(if (item.group == ReportItem.Group.LOCAL) R.string.local else R.string.export)
                binding.titleContainer.visibility = View.VISIBLE
            } else {
                binding.titleContainer.visibility = View.GONE
            }
            binding.label.text = "${item.thick} ${item.grade}${if (item.type.isEmpty()) "" else " ${item.type}"} @${item.pcs}"
            binding.crate.setText(item.crate.toString())
            this.item = item
            this.onChange = onChange
            this.deleteDialog = deleteDialog
            this.onDelete = onDelete
        }
    }
}