package pl.polsl.student.personalnavigation.view

import android.app.AlertDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.jetbrains.anko.toast
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.Gender
import pl.polsl.student.personalnavigation.model.Skill
import pl.polsl.student.personalnavigation.viewmodel.NameViewModel


class NameInputDialog(
        private val activity: AppCompatActivity,
        private val nameViewModel: NameViewModel
) {
    private val dialog: Dialog
    private val userInput: EditText
    private val genderSpinner: Spinner
    private val skillSpinner: Spinner

    init {
        val promptsView = activity.layoutInflater.inflate(R.layout.dialog_name, null)

        val alertDialogBuilder = AlertDialog.Builder(activity)

        alertDialogBuilder.setView(promptsView)

        userInput = promptsView.findViewById(R.id.editTextDialogUserInput)
        genderSpinner = promptsView.findViewById(R.id.genderSpinner)
        skillSpinner = promptsView.findViewById(R.id.skillSpinner)

        genderSpinner.adapter = TranslatedEnumSpinnerAdapter<Gender>(
                activity,
                mapOf(
                        R.string.Female to Gender.Female,
                        R.string.Male to Gender.Male
                ),
                { Gender.valueOf(it) }
        )

        skillSpinner.adapter = TranslatedEnumSpinnerAdapter<Skill>(
                activity,
                mapOf(
                        R.string.Low to Skill.Low,
                        R.string.Medium to Skill.Medium,
                        R.string.High to Skill.High
                ),
                { Skill.valueOf(it) }
        )

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(
                        android.R.string.ok,
                        { _, _ ->
                            onAccepted()
                        }
                )

        dialog = alertDialogBuilder.create()
    }

    fun setName(name: String) {
        userInput.setText(name, TextView.BufferType.EDITABLE)
    }

    fun show() {
        dialog.show()
    }

    private fun onAccepted() {
        try {
            nameViewModel.setName(userInput.text.toString())
            nameViewModel.setGender(genderSpinner.selectedItem as Gender)
        } catch (e: Exception) {
            activity.toast(e.message.toString())
        }
    }
}