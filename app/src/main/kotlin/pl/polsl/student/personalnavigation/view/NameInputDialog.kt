package pl.polsl.student.personalnavigation.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_name.view.*
import pl.polsl.student.personalnavigation.R


class NameInputDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        private val onNameEntered: (String) -> Unit
) {
    private val dialog: Dialog
    private val userInput: EditText

    init {
        val promptsView = layoutInflater.inflate(R.layout.dialog_name, null)

        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setView(promptsView)

        userInput = promptsView.findViewById<EditText>(R.id.editTextDialogUserInput)

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(
                        android.R.string.ok,
                        { _, _ ->
                            onNameEntered(userInput.text.toString())
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
}