package `in`.blackant.sgsbarcodehelper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import `in`.blackant.sgsbarcodehelper.databinding.ActivityReportBinding
import `in`.blackant.sgsbarcodehelper.databinding.DialogReportAddBinding
import `in`.blackant.sgsbarcodehelper.databinding.DialogReportSendBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStoreManager
    private lateinit var binding: ActivityReportBinding
    private lateinit var pagerAdapter: ReportPagerAdapter
    private lateinit var addDialog: AlertDialog
    private lateinit var sendDialog: AlertDialog
    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    private var datePicker = MaterialDatePicker.Builder.datePicker().setSelection(today).build()
    private var dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private fun formatThousand(n: Int): String {
        return DecimalFormat("#,###").format(n).replace(",", ".")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = DataStoreManager(this)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        pagerAdapter = ReportPagerAdapter()
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setText(if (position == 0) R.string.grading else R.string.stbj)
        }.attach()

        val addBinding = DialogReportAddBinding.inflate(layoutInflater)
        addDialog = MaterialAlertDialogBuilder(this)
            .setView(addBinding.root)
            .create()
        addDialog.setOnShowListener { addBinding.crate.setText("1") }
        addBinding.add.setOnClickListener {
            if (addBinding.thick.text.isEmpty()) {
                addBinding.thick.requestFocus()
                return@setOnClickListener
            }

            if (addBinding.grade.text.isEmpty()) {
                addBinding.grade.requestFocus()
                return@setOnClickListener
            }

            if (addBinding.pcs.text.isEmpty()) {
                addBinding.pcs.requestFocus()
                return@setOnClickListener
            }

            if (addBinding.crate.text?.isEmpty() != false) {
                addBinding.crate.requestFocus()
                return@setOnClickListener
            }

            addDialog.dismiss()

            val adapter =
                if (addBinding.grading.isChecked) pagerAdapter.grading else pagerAdapter.stbj
            val list = if (addBinding.local.isChecked) adapter.local else adapter.export
            list.add(
                ReportItem(
                    addBinding.thick.text.toString().toFloat(),
                    addBinding.grade.text.toString(),
                    addBinding.pcs.text.toString().toInt(),
                    addBinding.crate.text.toString().toInt(),
                )
            )
        }

        val sendBinding = DialogReportSendBinding.inflate(layoutInflater)
        sendBinding.shift.setAdapter(
            ArrayAdapter.createFromResource(
                this,
                R.array.shift,
                android.R.layout.simple_list_item_1
            )
        )
        sendBinding.dateContainer.setEndIconOnClickListener {
            datePicker.show(
                supportFragmentManager,
                null
            )
        }
        sendBinding.date.setText(dateFormat.format(Date(today)))
        datePicker.addOnPositiveButtonClickListener {
            sendBinding.date.setText(dateFormat.format(Date(it)))
        }
        sendDialog = MaterialAlertDialogBuilder(this)
            .setView(sendBinding.root)
            .create()
        sendBinding.send.setOnClickListener {
            if (sendBinding.shift.text.isEmpty()) {
                sendBinding.shift.requestFocus()
                return@setOnClickListener
            }

            if (sendBinding.date.text?.isEmpty() != false) {
                sendBinding.date.requestFocus()
                return@setOnClickListener
            }

            sendDialog.dismiss()

            val report = StringBuilder()
            report.append("*${sendBinding.shift.text} @ ${sendBinding.date.text}*")

            report.append("\n\n*GRADING LOCAL*")
            pagerAdapter.grading.local.grouped.let { grouped ->
                var first = true
                for (group in grouped) {
                    if (group.value.crate > 0) {
                        if (first) first = false
                        else report.append("\n│")
                        report.append(String.format("\n│  *%.1f mm*", group.key))
                        for (item in group.value) {
                            if (item.crate > 0) report.append("\n│    $item")
                        }
                    }
                }
            }

            report.append("\n\n*GRADING EXPORT*")
            pagerAdapter.grading.export.grouped.let { grouped ->
                var first = true
                for (group in grouped) {
                    if (group.value.crate > 0) {
                        if (first) first = false
                        else report.append("\n│")
                        report.append(String.format("\n│  *%.1f mm*", group.key))
                        for (item in group.value) {
                            if (item.crate > 0) report.append("\n│    $item")
                        }
                    }
                }
            }

            report.append("\n\n*TOTAL GRADING*")
            report.append(
                String.format(
                    "\n│  %d Krat\n│  %s Pcs\n│  %.2f m³",
                    pagerAdapter.grading.local.crate + pagerAdapter.grading.export.crate,
                    formatThousand(pagerAdapter.grading.local.pcs + pagerAdapter.grading.export.pcs),
                    pagerAdapter.grading.local.volume + pagerAdapter.grading.export.volume,
                )
            )

            report.append("\n\n*STBJ LOCAL*")
            pagerAdapter.stbj.local.grouped.let { grouped ->
                var first = true
                for (group in grouped) {
                    if (group.value.crate > 0) {
                        if (first) first = false
                        else report.append("\n│")
                        report.append(String.format("\n│  *%.1f mm*", group.key))
                        for (item in group.value) {
                            if (item.crate > 0) report.append("\n│    $item")
                        }
                    }
                }
            }

            report.append("\n\n*STBJ EXPORT*")
            pagerAdapter.stbj.export.grouped.let { grouped ->
                var first = true
                for (group in grouped) {
                    if (group.value.crate > 0) {
                        if (first) first = false
                        else report.append("\n│")
                        report.append(String.format("\n│  *%.1f mm*", group.key))
                        for (item in group.value) {
                            if (item.crate > 0) report.append("\n│    $item")
                        }
                    }
                }
            }

            report.append("\n\n*TOTAL STBJ*")
            report.append(
                String.format(
                    "\n│  %d Krat\n│  %s Pcs\n│  %.2f m³",
                    pagerAdapter.stbj.local.crate + pagerAdapter.stbj.export.crate,
                    formatThousand(pagerAdapter.stbj.local.pcs + pagerAdapter.stbj.export.pcs),
                    pagerAdapter.stbj.local.volume + pagerAdapter.stbj.export.volume,
                )
            )

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Laporan")
            intent.putExtra(Intent.EXTRA_TEXT, report.toString())
            startActivity(Intent.createChooser(intent, "Karim laporan"))
        }

        runBlocking(Dispatchers.IO) {
            val reportData = dataStore.getReportList().first()
            if (reportData != null) {
                for (item in reportData.split("\n").map { it.split(";") }) {
                    if (item.size == 5) {
                        val adapter =
                            if (item[0] == "grading") pagerAdapter.grading else pagerAdapter.stbj
                        (if (item[1] == "local") adapter.local else adapter.export).add(
                            ReportItem(item[2].toFloat(), item[3], item[4].toInt(), 0)
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        dataStore.setReportList(
            listOf(
                pagerAdapter.grading.local.joinToString("\n") { item -> "grading;local;${item.thick};${item.grade};${item.pcs}" },
                pagerAdapter.grading.export.joinToString("\n") { item -> "grading;export;${item.thick};${item.grade};${item.pcs}" },
                pagerAdapter.stbj.local.joinToString("\n") { item -> "stbj;local;${item.thick};${item.grade};${item.pcs}" },
                pagerAdapter.stbj.export.joinToString("\n") { item -> "stbj;export;${item.thick};${item.grade};${item.pcs}" },
            ).joinToString("\n")
        )
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.add_report_item -> {
                addDialog.show()
                true
            }

            R.id.send_report -> {
                sendDialog.show()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}