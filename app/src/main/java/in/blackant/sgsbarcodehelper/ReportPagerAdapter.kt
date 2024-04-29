package `in`.blackant.sgsbarcodehelper

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.blackant.sgsbarcodehelper.databinding.ReportListBinding

class ReportPagerAdapter : RecyclerView.Adapter<ReportPagerAdapter.ViewHolder>() {
    val grading = ReportListAdapter()
    val stbj = ReportListAdapter()

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(
            ReportListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(if (position == 0) grading else stbj)
    }

    class ViewHolder(private var binding: ReportListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.list.layoutManager = LinearLayoutManager(binding.root.context)
        }

        fun bind(adapter: ReportListAdapter) {
            adapter.listener = {setSummary(adapter)}
            binding.list.adapter = adapter
            setSummary(adapter)
        }

        private fun setSummary(adapter: ReportListAdapter) {
            binding.crate.text = String.format("%d Crate", adapter.local.crate + adapter.export.crate)
            binding.pcs.text = String.format("%d Pcs", adapter.local.pcs + adapter.export.pcs)
            binding.volume.text = String.format("%.2f m³", adapter.local.volume + adapter.export.volume)
        }
    }
}