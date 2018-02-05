package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import pl.polsl.student.personalnavigation.model.Skill
import pl.polsl.student.personalnavigation.model.Gender

class NameViewModel(
        private val sharedPreferences: SharedPreferences
): ViewModel() {
    private val NAME_KEY = "my_name"
    private val GENDER_KEY = "my_gender"
    private val EXPERIENCE_KEY = "my_experience"

    private val mutableName = MutableLiveData<String>()
    private val mutableGender = MutableLiveData<Gender>()
    private val mutableExperience = MutableLiveData<Skill>()

    init {
        if (sharedPreferences.contains(NAME_KEY)) {
            mutableName.value = sharedPreferences.getString(NAME_KEY,"")
            mutableGender.value = Gender.valueOf(
                    sharedPreferences.getString(GENDER_KEY, Gender.Female.toString())
            )
            mutableExperience.value = Skill.valueOf(
                    sharedPreferences.getString(EXPERIENCE_KEY, Skill.Low.toString())
            )
        }
    }

    val name: LiveData<String> = mutableName
    val skill: LiveData<Skill> = mutableExperience
    val gender: LiveData<Gender> = mutableGender

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

    fun setGender(gender: Gender) {
        sharedPreferences
                .edit()
                .putString(GENDER_KEY, gender.toString())
                .apply()

        mutableGender.value = gender
    }

    fun setExperience(skill: Skill) {
        sharedPreferences
                .edit()
                .putString(EXPERIENCE_KEY, skill.toString())
                .apply()

        mutableExperience.value = skill
    }

}