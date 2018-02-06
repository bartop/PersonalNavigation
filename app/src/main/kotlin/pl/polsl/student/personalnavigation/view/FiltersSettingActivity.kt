package pl.polsl.student.personalnavigation.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.koin.android.architecture.ext.getViewModel
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.FilterData
import pl.polsl.student.personalnavigation.model.Gender
import pl.polsl.student.personalnavigation.model.Skill
import pl.polsl.student.personalnavigation.util.observeNotNull
import pl.polsl.student.personalnavigation.util.toEnumSet
import pl.polsl.student.personalnavigation.viewmodel.FilterDataViewModel
import java.util.*

class FiltersSettingActivity : AppCompatActivity() {

    private val filterViewModel by lazy { getViewModel<FilterDataViewModel>() }
    private val gendersCheckBoxes by lazy {
        mapOf(
                Gender.Female to R.id.femaleCheckBox,
               Gender.Male to R.id.maleCheckBox
        )
                .mapValues { findViewById<CheckBox>(it.value) }
    }

    private val skillsCheckBoxes by lazy {
        mapOf(
                Skill.Low to R.id.lowCheckBox,
                Skill.Medium to R.id.mediumCheckBox,
                Skill.High to R.id.highCheckBox
        )
                .mapValues { findViewById<CheckBox>(it.value) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters_setting)

        filterViewModel.filterData.observeNotNull(this) { filter ->
            for ((gender, cb) in gendersCheckBoxes) {
                cb.isChecked = filter.genders.contains(gender)
            }

            for ((skill, cb) in skillsCheckBoxes) {
                cb.isChecked = filter.skills.contains(skill)
            }
        }

        supportActionBar?.title = resources.getString(R.string.filters)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            confirmFilters()
            return true
        }
        return false
    }

    private fun confirmFilters() {
        val genders = selectedGenders()
        val skills = selectedSkills()

        when {
            genders.isEmpty() -> toast(R.string.select_one_gender)
            skills.isEmpty() -> toast(R.string.select_one_skill)
            else -> {
                filterViewModel.setFilterData(
                        FilterData(
                                genders, skills
                        )
                )
                finish()
            }
        }
    }

    private fun selectedGenders(): EnumSet<Gender> {
        return gendersCheckBoxes
                .filterValues { it.isChecked }
                .keys
                .toEnumSet()

    }

    private fun selectedSkills(): EnumSet<Skill> {
        return skillsCheckBoxes
                .filterValues { it.isChecked }
                .keys
                .toEnumSet()
    }
}
