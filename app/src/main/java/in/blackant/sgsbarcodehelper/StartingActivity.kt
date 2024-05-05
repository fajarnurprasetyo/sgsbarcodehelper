package `in`.blackant.sgsbarcodehelper

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.ActivityStartingBinding
import `in`.blackant.sgsbarcodehelper.databinding.DialogTrakteerBinding

class StartingActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStoreManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dataStore = DataStoreManager(this)

        val binding = ActivityStartingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkUpdate { checkPayday(this::openMainActivity) }
    }

    private fun checkUpdate(callback: () -> Any?) {
        Thread {
            if (Utils.isOnline(this)) {
                val update = Utils.getUpdate()
                if (update != null) {
                    runOnUiThread { UpdateDialog(this, update, callback) }
                    return@Thread
                }
            }
            callback()
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun checkPayday(callback: () -> Any?) {
        Thread {
            if (Utils.isOnline(this)) {
                val payday = Utils.getPayday(this)
                if (payday != null) {
                    lateinit var trakteerBinding: DialogTrakteerBinding
                    lateinit var trakteerDialog: AlertDialog
                    runOnUiThread {
                        trakteerBinding = DialogTrakteerBinding.inflate(layoutInflater)
                        trakteerDialog = MaterialAlertDialogBuilder(this)
                            .setView(trakteerBinding.root)
                            .setCancelable(false)
                            .show()
                        trakteerDialog.setOnDismissListener { callback() }
                        trakteerBinding.later.setOnClickListener { trakteerDialog.dismiss() }
                        trakteerBinding.trakteer.setOnClickListener {
                            dataStore.setPayday(payday)
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://trakteer.id/fajar_nur_prasetyo/tip")
                            )
                            startActivity(intent)
                            Handler(mainLooper).postDelayed({trakteerDialog.dismiss()}, 1)
                        }
                    }
                    for (i in 10 downTo 1) {
                        runOnUiThread { trakteerBinding.later.text = "Nanti aja ($i)" }
                        Thread.sleep(1000)
                    }
                    runOnUiThread { trakteerBinding.later.isEnabled = true }
                    return@Thread
                }
            }
            callback()
        }.start()
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}