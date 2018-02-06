package pl.polsl.student.personalnavigation.model

import android.content.SharedPreferences
import pl.polsl.student.personalnavigation.util.getJson
import pl.polsl.student.personalnavigation.util.putJson


class FilterDataRepository(private val sharedPreferences: SharedPreferences) {
    private val FILTER_DATA_KEY = "FILTER_DATA"
    private var filterData: FilterData = FilterData()

    init {
        if (sharedPreferences.contains(FILTER_DATA_KEY)) {
            filterData = sharedPreferences.getJson<FilterData>(FILTER_DATA_KEY)
        }
    }

    fun get(): FilterData = filterData

    fun set(filterData: FilterData) {
        sharedPreferences
                .edit()
                .putJson(FILTER_DATA_KEY, filterData)
                .apply()

        this.filterData = filterData
    }
}