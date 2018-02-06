package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import pl.polsl.student.personalnavigation.model.FilterData
import pl.polsl.student.personalnavigation.model.FilterDataRepository



class FilterDataViewModel(
        private val filterDataRepository: FilterDataRepository
): ViewModel() {
    private val mutableFilterData: MutableLiveData<FilterData> = MutableLiveData()

    init {
        mutableFilterData.value = filterDataRepository.get()
    }

    fun setFilterData(filterData: FilterData) {
        filterDataRepository.set(filterData)
        mutableFilterData.value = filterData
    }

    val filterData: LiveData<FilterData> = mutableFilterData
}