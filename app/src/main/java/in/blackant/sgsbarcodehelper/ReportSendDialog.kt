package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.DialogReportSendBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("unused", "RedundantSuppression")
class ReportSendDialog(private val context: Context, onSend: (ReportSendDialog) -> Any?) {
    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    private val datePicker = MaterialDatePicker.Builder.datePicker().setSelection(today).build()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val binding: DialogReportSendBinding =
        DialogReportSendBinding.inflate(LayoutInflater.from(context))
    private val dialog: AlertDialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .create()

    private fun showDatePicker() {
        datePicker.show((context as AppCompatActivity).supportFragmentManager, null)
    }

    init {
        binding.shift.setAdapter(
            ArrayAdapter.createFromResource(
                context,
                R.array.shift,
                android.R.layout.simple_list_item_1
            )
        )
        binding.dateContainer.setEndIconOnClickListener {
            showDatePicker()
        }
        binding.date.setOnFocusChangeListener { _, focus ->
            if (focus) {
                binding.date.clearFocus()
                showDatePicker()
            }
        }
        binding.date.setText(dateFormat.format(Date(today)))
        binding.send.setOnClickListener {
            if (binding.shift.text.isNullOrEmpty()) {
                binding.shift.requestFocus()
                return@setOnClickListener
            }

            dialog.dismiss()
            onSend(this)
        }
        datePicker.addOnPositiveButtonClickListener { binding.date.setText(dateFormat.format(Date(it))) }
    }

    val shift get() = binding.shift.text.toString()
    val date get() = binding.date.text.toString()

    fun show() {
        dialog.show()
    }
}