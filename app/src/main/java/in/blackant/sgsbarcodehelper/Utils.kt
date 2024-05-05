package `in`.blackant.sgsbarcodehelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class Utils {
    companion object {
        fun isOnline(context: Context): Boolean {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (manager != null) {
                val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            }
            return false
        }

        private const val UPDATE_URL =
            "https://api.github.com/repos/fajarnurprasetyo/sgsbarcodehelper/releases/latest"

        fun getUpdate(): Update? {
            try {
                with(URL(UPDATE_URL).openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    inputStream.bufferedReader().use {
                        val json = JSONObject(it.readText())
                        it.close()
                        val version = json.getString("name")
                        if (BuildConfig.VERSION_NAME != version) {
                            val apkUrl = json.getJSONArray("assets")
                                .getJSONObject(0)
                                .getString("browser_download_url")
                            return Update(
                                URL(apkUrl).openConnection() as HttpURLConnection,
                                version,
                                json.getString("body"),
                            )
                        }
                    }
                }
            } catch (_: FileNotFoundException) {
            }
            return null
        }

        private const val PAYDAY_URL =
            "https://raw.githubusercontent.com/fajarnurprasetyo/sgsbarcodehelper/main/latest-payday.txt"

        fun getPayday(context: Context): String? {
            val dataStore = DataStoreManager(context)
            try {
                val latestPayday: String?
                runBlocking {
                    latestPayday = dataStore.getPayday().first()
                }
                with(URL(PAYDAY_URL).openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "text/plain")
                    inputStream.bufferedReader().use {
                        val payday = it.readText()
                        it.close()
                        if (payday != latestPayday) {
                            return payday
                        }
                    }
                }
            } catch (_: FileNotFoundException) {
            }
            return null
        }
    }

    data class Update(
        val connection: HttpURLConnection,
        val version: String,
        val desc: String
    )
}