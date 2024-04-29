package `in`.blackant.sgsbarcodehelper

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class DataStoreManager(context: Context) : ViewModel() {
    companion object {
        private val Context.dataStore by preferencesDataStore("preferences")
        val REPORT_LIST = stringPreferencesKey("report_list")
    }

    private val dataStore = context.dataStore

    fun setReportList(reportList: String) {
        viewModelScope.launch {
            dataStore.edit { preferences -> preferences[REPORT_LIST] = reportList }
        }
    }

    fun getReportList(): Flow<String?> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences -> preferences[REPORT_LIST] }
    }
}