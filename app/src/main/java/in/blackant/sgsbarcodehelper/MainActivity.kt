package `in`.blackant.sgsbarcodehelper

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.blackant.sgsbarcodehelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.materials.setOnClickListener {}
        binding.report.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ReportActivity::class.java
                )
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Thread {
            if (Utils.isOnline(this)) {
                val update = Utils.getUpdate()
                if (update != null) {
                    runOnUiThread { UpdateDialog(this, update) }
                } else if (Utils.isPayday(this)) {
                    runOnUiThread { TrakteerDialog(this) }
                }
            }
            runOnUiThread {
                binding.loading.visibility = View.GONE
                binding.main.visibility = View.VISIBLE
            }
        }.start()
    }
}