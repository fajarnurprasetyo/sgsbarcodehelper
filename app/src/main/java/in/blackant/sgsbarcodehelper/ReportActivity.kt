package `in`.blackant.sgsbarcodehelper

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
    private lateinit var dataStore: DataStoreManager
    private lateinit var pagerAdapter: ReportPagerAdapter
    private lateinit var addDialog: ReportAddItemDialog
    private lateinit var sendDialog: ReportSendDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = DataStoreManager(this)
        val binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        pagerAdapter = ReportPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setText(if (position == 0) R.string.grading else R.string.stbj)
        }.attach()

        addDialog = ReportAddItemDialog(this) { dialog ->
            (if (binding.viewPager.currentItem == 0) pagerAdapter.grading else pagerAdapter.stbj).list.add(
                ReportItem(
                    dialog.group,
                    dialog.thick,
                    dialog.grade,
                    dialog.type,
                    dialog.pcs,
                    dialog.crate,
                )
            )
        }
        sendDialog = ReportSendDialog(this, this::sendReport)

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

    private fun sendReport(dialog: ReportSendDialog) {
        val report = StringBuilder()
        report.append("*${dialog.shift} @ ${dialog.date}*")

        report.append("\n\n*GRADING LOCAL*")
        pagerAdapter.grading.local.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append("\n│  *${group.key} mm*")
                    for (item in group.value) {
                        if (item.crate > 0) report.append("\n│    $item")
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
                    report.append("\n│  *${group.key} mm*")
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
        pagerAdapter.stbj.local.groupBy { it.thick }.let { grouped ->
            var first = true
            for (group in grouped) {
                if (group.value.crate > 0) {
                    if (first) first = false
                    else report.append("\n│")
                    report.append("\n│  *${group.key} mm*")
                    for (item in group.value) {
                        if (item.crate > 0) report.append("\n│    $item")
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
                    report.append("\n│  *${group.key} mm*")
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