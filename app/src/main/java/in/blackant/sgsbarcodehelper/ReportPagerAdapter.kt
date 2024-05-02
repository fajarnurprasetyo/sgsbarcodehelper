package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.blackant.sgsbarcodehelper.databinding.ReportListBinding

class ReportPagerAdapter(private val context: Context) :
    RecyclerView.Adapter<ReportPagerAdapter.ViewHolder>() {
    val grading = ReportListAdapter(context)
    val stbj = ReportListAdapter(context)

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
            adapter.onReportListChange = { setSummary(adapter) }
            binding.list.adapter = adapter
            setSummary(adapter)
        }

        private fun setSummary(adapter: ReportListAdapter) {
            binding.crate.text = context.getString(R.string.crate_value, adapter.list.crate)
            binding.pcs.text = context.getString(R.string.pcs_value, adapter.list.pcs)
            binding.volume.text = context.getString(R.string.volume_value, adapter.list.volume)
        }
    }
}