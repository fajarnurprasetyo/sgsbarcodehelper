package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
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
class ReportSendDialog(private val context: Context) {
    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    private val datePicker = MaterialDatePicker.Builder.datePicker().setSelection(today).build()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val binding: DialogReportSendBinding =
        DialogReportSendBinding.inflate(LayoutInflater.from(context))
    private val dialog: AlertDialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setPositiveButton(R.string.send, null)
        .create()

    private fun showDatePicker() {
        datePicker.show((context as AppCompatActivity).supportFragmentManager, null)
    }

    init {
        datePicker.addOnPositiveButtonClickListener { binding.date.setText(dateFormat.format(Date(it))) }
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
        binding.advanced.setOnCheckedChangeListener { _, checked ->
            binding.advancedContainer.visibility = if (checked) View.VISIBLE else View.GONE
        }
    }

    val shift get() = binding.shift.text.toString()
    val date get() = binding.date.text.toString()
    val advanced get() = binding.advanced.isChecked
    val utyPlusUp get() = binding.utyPlusUp.text.toString().toFloat()
    val reject get() = binding.reject.text.toString().toFloat()
    val rejectRepair get() = binding.rejectRepair.text.toString().toFloat()

    fun show(onSend: () -> Any?) {
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            if (binding.shift.text.isNullOrEmpty()) {
                binding.shift.requestFocus()
                return@setOnClickListener
            }

            if (advanced) {
                if (binding.utyPlusUp.text.isNullOrEmpty()) {
                    binding.utyPlusUp.requestFocus()
                    return@setOnClickListener
                }

                if (binding.reject.text.isNullOrEmpty()) {
                    binding.reject.requestFocus()
                    return@setOnClickListener
                }

                if (binding.rejectRepair.text.isNullOrEmpty()) {
                    binding.rejectRepair.requestFocus()
                    return@setOnClickListener
                }
            }

            dialog.dismiss()
            onSend()
        }
    }
}