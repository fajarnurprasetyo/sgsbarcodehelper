package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import `in`.blackant.sgsbarcodehelper.databinding.DialogTrakteerBinding
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import java.io.BufferedReader
import java.io.InputStreamReader

class TrakteerDialog(context: Context) {
    init {
        val reader = BufferedReader(InputStreamReader(context.assets.open("trakteer.md")))
        val message = reader.readText()
        reader.close()

        val binding = DialogTrakteerBinding.inflate(LayoutInflater.from(context))
        Markwon.builder(context)
            .usePlugins(listOf(HtmlPlugin.create(), ImagesPlugin.create()))
            .build().setMarkdown(binding.message, message)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .setCancelable(false)
            .show()

        binding.message.setOnClickListener { dialog.dismiss() }
    }
}