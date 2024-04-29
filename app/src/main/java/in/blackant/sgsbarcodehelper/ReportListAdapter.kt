package `in`.blackant.sgsbarcodehelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import `in`.blackant.sgsbarcodehelper.databinding.ReportListItemBinding

class ReportListAdapter : RecyclerView.Adapter<ReportListAdapter.ViewHolder>(),
    ReportList.ReportListListener {
    companion object {
        const val ITEM_TYPE_TITLE = 0
        const val ITEM_TYPE_REPORT = 1
    }

    var listener: (() -> Any)? = null
    val local = ReportList(this)
    val export = ReportList(this)

    override fun getItemCount(): Int {
        return local.size + export.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 || position == local.size + 1) {
            return ITEM_TYPE_TITLE
        }
        return ITEM_TYPE_REPORT
    }

    private fun getItemPosition(list: ReportList, index: Int): Int {
        return if (list == local) index + 1 else index + local.size + 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(
            ReportListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_TITLE) {
            holder.bind(if (position == 0) R.string.local else R.string.export)
        } else {
            if (position < local.size + 1) holder.bind(local, local[position - 1], listener)
            else holder.bind(export, export[position - local.size - 2], listener)
        }
    }

    override fun onItemAdded(list: ReportList, index: Int) {
        notifyItemInserted(getItemPosition(list, index))
        listener?.let { it() }
    }

    override fun onItemChanged(list: ReportList, index: Int) {
        notifyItemChanged(getItemPosition(list, index))
        listener?.let { it() }
    }

    override fun onItemRemoved(list: ReportList, index: Int) {
        notifyItemRemoved(getItemPosition(list, index))
        listener?.let { it() }
    }

    class ViewHolder(private val binding: ReportListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var item: ReportItem? = null
        private var listener: (() -> Any)? = null

        init {
            binding.clear.setOnClickListener { binding.crate.setText("0") }
            binding.crate.addTextChangedListener {
                item?.crate = if (!it.isNullOrEmpty()) it.toString().toInt() else 0
                listener?.let { it() }
            }
        }

        fun bind(resId: Int) {
            binding.item.visibility = View.GONE
            binding.title.visibility = View.VISIBLE
            binding.title.setText(resId)
        }

        fun bind(list: ReportList, item: ReportItem, listener: (() -> Any)?) {
            this.item = null
            this.listener = null
            binding.title.visibility = View.GONE
            binding.item.visibility = View.VISIBLE
            binding.label.text = String.format("%.1fmm %s %d", item.thick, item.grade, item.pcs)
            binding.crate.setText(item.crate.toString())
            binding.delete.setOnClickListener { list.remove(item) }
            this.item = item
            this.listener = listener
        }
    }
}