package com.example.notes.ui.dialogue

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.notes.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.fragment_note_detail.*

class AddOwnerDialogue :DialogFragment(){

    private var positiveButton:((String)->Unit)? = null

    fun setPostiveLIstener(lister:(String)->Unit){
        positiveButton = lister
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val addownerEditText = LayoutInflater.from(requireContext()).inflate(
            R.layout.edit_text_email,
            clNoteContainer,
            false
        ) as TextInputLayout
        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_add_person)
            .setTitle("Add owner to note")
            .setMessage("Enter an E-Mail of a person you want to share the note with." +
                "This person will be able to read and edit the note.")
            .setView(addownerEditText)
            .setPositiveButton("Add") { _, _ ->
                val email = addownerEditText.findViewById<EditText>(R.id.etAddOwnerEmail).text.toString()
                positiveButton?.let { yes ->
                    yes(email)
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }
}