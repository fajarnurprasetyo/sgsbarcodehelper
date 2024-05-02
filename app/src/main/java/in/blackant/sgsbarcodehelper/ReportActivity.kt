package `in`.blackant.sgsbarcodehelper

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import `in`.blackant.sgsbarcodehelper.databinding.ActivityReportBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat

class ReportActivity : AppCompatActivity() {
    companion object {
        private const val THICK_STRING = "\n│  *%.1f mm*"
        private const val ITEM_STRING = "\n│    %s%s @%d = %d Krat"
    }

    private lateinit var dataStore: DataStoreManager
    private lateinit var binding: ActivityReportBinding
    private lateinit var pagerAdapter: ReportPagerAdapter
    private lateinit var addDialog: ReportAddItemDialog
    private lateinit var sendDialog: ReportSendDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = DataStoreManager(this)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        pagerAdapter = ReportPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setText(if (position == 0) R.string.grading else R.string.stbj)
        }.attach()

        addDialog = ReportAddItemDialog(this)
        sendDialog = ReportSendDialog(this)

        runBlocking {
            val reportData = dataStore.getReportList().first()
            if (reportData != null) {
                for (item in reportData.split("\n").map { it.split(";") }) {
                    if (item.size == 6) {
                        (if (item[0] == "grading") pagerAdapter.grading else pagerAdapter.stbj).list.add(
                            ReportItem(
                                if (item[1] == "local") ReportItem.Group.LOCAL else ReportItem.Group.EXPORT,
                                item[2].toFloat(),
                                ReportItem.Grade.fromString(item[3]) ?: "Unknown",
                                item[4],
                                item[5].toInt(),
                                0,
                            )
                        )
                    }
                }
            }
        }

        binding.loading.visibility = View.GONE
        binding.main.visibility = View.VISIBLE
    }

    override fun onStop() {
        dataStore.setReportList(
            listOf(
                pagerAdapter.grading.local.joinToString("\n") { item -> "grading;local;${item.thick};${item.grade};${item.type};${item.pcs}" },
                pagerAdapter.grading.export.joinToString("\n") { item -> "grading;export;${item.thick};${item.grade};${item.type};${item.pcs}" },
                pagerAdapter.stbj.local.joinToString("\n") { item -> "stbj;local;${item.thick};${item.grade};${item.type};${item.pcs}" },
                pagerAdapter.stbj.export.joinToString("\n") { item -> "stbj;export;${item.thick};${item.grade};${item.type};${item.pcs}" },
            ).joinToString("\n")
        )
        super.onStop()
    }

    private fun formatThousand(n: Int): String {
        return DecimalFormat("#,###").format(n).replace(",", ".")
    }

    private fun addReportItem() {
        (if (binding.viewPager.currentItem == 0) pagerAdapter.grading else pagerAdapter.stbj).list.add(
            ReportItem(
                addDialog.group,
                addDialog.thick,
                addDialog.grade,
                addDialog.type,
                addDialog.pcs,
                addDialog.crate,
            )
        )
    }

    @SuppressLint("DefaultLocale")
    private fun sendReport() {
        val report = StringBuilder()
        report.append("*${sendDialog.shift} @ ${sendDialog.date}*")

        report.append("\n\n*GRADING LOCAL*")
        pagerAdapter.grading.local.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append(String.format(THICK_STRING, group.key))
                    for (item in group.value) {
                        if (item.crate > 0) report.append(
                            String.format(
                                ITEM_STRING,
                                item.grade,
                                if (item.type.isEmpty()) "" else " ${item.type}",
                                item.pcs,
                                item.crate,
                            )
                        )
                    }
                }
            }
        }

        report.append("\n\n*GRADING EXPORT*")
        pagerAdapter.grading.export.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append(String.format(THICK_STRING, group.key))
                    for (item in group.value) {
                        if (item.crate > 0) report.append(
                            String.format(
                                ITEM_STRING,
                                item.grade,
                                if (item.type.isEmpty()) "" else " ${item.type}",
                                item.pcs,
                                item.crate,
                            )
                        )
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
        pagerAdapter.stbj.local.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append(String.format(THICK_STRING, group.key))
                    for (item in group.value) {
                        if (item.crate > 0) report.append(
                            String.format(
                                ITEM_STRING,
                                item.grade,
                                if (item.type.isEmpty()) "" else " ${item.type}",
                                item.pcs,
                                item.crate,
                            )
                        )
                    }
                }
            }
        }

        report.append("\n\n*STBJ EXPORT*")
        pagerAdapter.stbj.export.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append(String.format(THICK_STRING, group.key))
                    for (item in group.value) {
                        if (item.crate > 0) report.append(
                            String.format(
                                ITEM_STRING,
                                item.grade,
                                if (item.type.isEmpty()) "" else " ${item.type}",
                                item.pcs,
                                item.crate,
                            )
                        )
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

        if (sendDialog.advanced) {
            report.append(
                String.format(
                    "\n\n*UTY+ Up = %.2f%%*\n*Reject = %.2f%%*\n*Reject+Repair = %.2f%%*",
                    sendDialog.utyPlusUp,
                    sendDialog.reject,
                    sendDialog.rejectRepair,
                )
            )
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Laporan")
        intent.putExtra(Intent.EXTRA_TEXT, report.toString())
        startActivity(Intent.createChooser(intent, "Kirim laporan"))
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
                addDialog.show(this::addReportItem)
                true
            }

            R.id.send_report -> {
                sendDialog.show(this::sendReport)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}