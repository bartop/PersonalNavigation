package pl.polsl.student.personalnavigation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView


class NameInputDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        onNameEntered: (String) -> Unit
) {
    private val dialog: Dialog

    init {
        val promptsView = layoutInflater.inflate(R.layout.dialog_name, null)

        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setView(promptsView)

        val userInput = promptsView.findViewById<EditText>(R.id.editTextDialogUserInput)

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        { _, _ ->
                            onNameEntered(userInput.text.toString())
                        }
                )


        dialog = alertDialogBuilder.create()
    }

    fun show() {
        dialog.show()
    }

}