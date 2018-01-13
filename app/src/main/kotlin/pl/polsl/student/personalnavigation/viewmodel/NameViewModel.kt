package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences

class NameViewModel(
        private val sharedPreferences: SharedPreferences
): ViewModel() {
    private val NAME_KEY = "name"
    private val mutableName = MutableLiveData<String>()

    init {
        if (sharedPreferences.contains(NAME_KEY)) {
            mutableName.value = sharedPreferences.getString(NAME_KEY,"")
        }
    }

    val name: LiveData<String> = mutableName

    fun setName(name: String) {
        if (!name.isBlank()) {
            sharedPreferences
                    .edit()
                    .putString(NAME_KEY, name)
                    .apply()
            mutableName.value = name
        } else {
            throw RuntimeException("Name cannot be empty!")
        }
    }

}