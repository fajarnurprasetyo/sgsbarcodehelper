package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.DialogReportAddItemBinding

@Suppress("unused", "RedundantSuppression")
class ReportAddItemDialog(private val context: Context) {
    private var binding = DialogReportAddItemBinding.inflate(LayoutInflater.from(context))
    private var dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setPositiveButton(R.string.add, null)
        .create()

    init {
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

        binding.glue.setAdapter(
            ArrayAdapter(
                context, android.R.layout.simple_list_item_1, ReportItem.Glue.entries
            )
        )

        dialog.setOnShowListener { binding.crate.setText("1") }

    }

    fun show(onAdd: () -> Any?) {
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
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
            onAdd()
        }
    }

    val thick
        get() = if (binding.thick.text.isEmpty()) 0f else binding.thick.text.toString().toFloat()
    val grade
        get():Any {
            val gradeStr = binding.grade.text.toString()
            return ReportItem.Grade.fromString(gradeStr)
        }
    val glue
        get():Any {
            val glueStr = binding.glue.text.toString()
            return ReportItem.Glue.fromString(glueStr)
        }
    val pcs get() = if (binding.pcs.text.isEmpty()) 0 else binding.pcs.text.toString().toInt()
    val crate
        get() = if (binding.crate.text.isNullOrEmpty()) 0 else binding.crate.text.toString().toInt()
}