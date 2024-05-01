package `in`.blackant.sgsbarcodehelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.DialogUpdateBinding
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class UpdateDialog(private val context: Context, private val update: Utils.Update) {
    private val binding = DialogUpdateBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialAlertDialogBuilder(context)
        .setTitle(R.string.update_available)
        .setView(binding.root)
        .setNegativeButton(R.string.ignore, null)
        .setPositiveButton(R.string.update, null)
        .setCancelable(false)
        .create()

    init {
        binding.message.text = update.desc
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { runUpdate() }
    }

    @SuppressLint("SetTextI18n")
    private fun setProgress(progress: Int, max: Int) {
        val progressStr = Formatter.formatFileSize(context, progress.toLong())
        val maxStr = Formatter.formatFileSize(context, max.toLong())
        binding.message.text = "$progressStr / $maxStr"
        binding.progressBar.progress = progress * 100 / if (max == 0) 1 else max
    }

    private fun runUpdate() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(context.mainLooper)
        var canceled = false

        dialog.setTitle(R.string.downloading)
        binding.progressBar.visibility = View.VISIBLE
        setProgress(0, 0)
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).let {
            it.setText(R.string.cancel)
            it.setOnClickListener {
                canceled = true
                dialog.dismiss()
            }
        }
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.GONE

        executor.execute {
            val contentLength = update.connection.contentLength
            handler.run { setProgress(0, contentLength) }
            update.connection.inputStream.use { reader ->
                val file = File("${context.externalCacheDir}/update.apk")
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(contentLength)
                    var progress = 0
                    var length: Int
                    while ((reader!!.read(buffer).also { length = it }) > 0) {
                        if (canceled) {
                            reader.close()
                            output.close()
                            return@execute
                        }
                        progress += length
                        handler.run { setProgress(progress, contentLength) }
                        output.write(buffer, 0, length)
                    }
                    reader.close()
                    output.close()

                    val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
                    val install = Intent(Intent.ACTION_VIEW)
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.setDataAndType(uri, update.connection.contentType)
                    context.startActivity(install)
                }
            }
        }
    }
}