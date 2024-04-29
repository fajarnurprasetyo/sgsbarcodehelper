package `in`.blackant.sgsbarcodehelper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import `in`.blackant.sgsbarcodehelper.databinding.ActivityStartBinding
import java.io.File
import java.io.FileOutputStream

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)

        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Utils.isOnline(this)) {
            binding.progressBar.isIndeterminate = false
            binding.text.setText(R.string.no_internet)
            Thread {
                Thread.sleep(3000)
                finish()
            }.start()
        } else {
            binding.text.setText(R.string.checking_updates)
            Thread {
                val update = Utils.getUpdate()
                if (update != null) {
                    val contentLength = update.connection.contentLength
                    runOnUiThread {
                        binding.progressBar.isIndeterminate = false
                        binding.progressBar.max = contentLength
                        binding.text.setText(R.string.downloading_updates)
                    }
                    update.connection.inputStream.use { reader ->
                        val file = File(
                            String.format(
                                "%s/%s.apk",
                                externalCacheDir,
                                update.version
                            )
                        )
                        FileOutputStream(file).use { output ->
                            val buffer = ByteArray(contentLength)
                            var length: Int
                            while ((reader.read(buffer).also { length = it }) > 0) {
                                output.write(buffer, 0, length)
                                runOnUiThread { binding.progressBar.progress += length }
                            }
                            reader.close()
                            output.close()

                            val uri = FileProvider.getUriForFile(
                                this,
                                BuildConfig.APPLICATION_ID,
                                file
                            )
                            val install = Intent(Intent.ACTION_VIEW)
                            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            install.setDataAndType(uri, update.connection.contentType)
                            startActivity(install)
                        }
                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }.start()
        }
    }
}