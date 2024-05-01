package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.DialogReportAddBinding

class ReportAddItemDialog(context: Context) {
    private var binding: DialogReportAddBinding
    private var dialog: AlertDialog
    private var listener: ((ReportAddItemDialog) -> Any)? = null

    init {
        binding = DialogReportAddBinding.inflate(LayoutInflater.from(context))
        dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()

        val thickAdapter =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, ReportItem.Thick.plywood)
        binding.thick.setAdapter(thickAdapter)

        binding.thick.setOnItemClickListener { _, _, position, _ ->
            val thick = thickAdapter.getItem(position)
            if (thick != null) {
                binding.pcs.setAdapter(
                    ArrayAdapter(
                        context, android.R.layout.simple_list_item_1, thick.pcs
                    )
                )
            }
        }

        binding.grade.setAdapter(
            ArrayAdapter(
                context, android.R.layout.simple_list_item_1, ReportItem.Grade.plywood
            )
        )

        binding.add.setOnClickListener {
            if (binding.thick.text.isEmpty()) {
                binding.thick.requestFocus()
                return@setOnClickListener
            }

            if (binding.grade.text.isEmpty()) {
                binding.grade.requestFocus()
                return@setOnClickListener
            }

            if (binding.pcs.text.isEmpty()) {
                binding.pcs.requestFocus()
                return@setOnClickListener
            }

            if (binding.crate.text?.isEmpty() != false) {
                binding.crate.requestFocus()
                return@setOnClickListener
            }

            dialog.dismiss()
            listener?.let { it(this) }
        }

        dialog.setOnShowListener { binding.crate.setText("1") }
    }

    fun setOnAddItemListener(listener: ((ReportAddItemDialog) -> Any)): ReportAddItemDialog {
        this.listener = listener
        return this
    }

    fun show() {
        dialog.show()
    }

    val group get() = if (binding.local.isChecked) ReportItem.Group.LOCAL else ReportItem.Group.EXPORT
    val thick
        get() = if (binding.thick.text.isEmpty()) 0f else binding.thick.text.toString().toFloat()
    val grade
        get():Any {
            val gradeStr = binding.grade.text.toString()
            return ReportItem.Grade.fromString(gradeStr) ?: gradeStr
        }

    val type get() = binding.type.text.toString()
    val pcs get() = if (binding.pcs.text.isEmpty()) 0 else binding.pcs.text.toString().toInt()
    val crate
        get() = if (binding.crate.text.isNullOrEmpty()) 0 else binding.crate.text.toString().toInt()
}