package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.ReportListBinding

class ReportPagerAdapter(private val context: Context) :
    RecyclerView.Adapter<ReportPagerAdapter.ViewHolder>() {
    private val deleteDialog = MaterialAlertDialogBuilder(context)
        .setTitle(R.string.delete_item_title)
        .setMessage(R.string.delete_item_message)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.delete, null)
        .setCancelable(false)
        .create()
    val grading = ReportListAdapter(context, deleteDialog)
    val stbj = ReportListAdapter(context, deleteDialog)

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(
            context,
            ReportListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(if (position == 0) grading else stbj)
    }

    class ViewHolder(private val context: Context, private val binding: ReportListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.list.layoutManager = LinearLayoutManager(binding.root.context)
        }

        fun bind(adapter: ReportListAdapter) {
            adapter.onReportListChange = { updateSummary(adapter) }
            binding.list.adapter = adapter
            updateSummary(adapter)
        }

        private fun updateSummary(adapter: ReportListAdapter) {
            binding.summary.totalCrate.text = context.getString(R.string.crate_value, adapter.list.crate)
            binding.summary.totalPcs.text = context.getString(R.string.pcs_value, adapter.list.pcs)
            binding.summary.totalVolume.text =
                context.getString(R.string.volume_value, adapter.list.volume)
        }
    }
}