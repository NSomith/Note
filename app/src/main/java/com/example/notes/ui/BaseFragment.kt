package com.example.notes.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseFragment(layoutid:Int):Fragment(layoutid) {
    fun showSnackbar(text: String){
        Snackbar.make(
                requireActivity().rootLayout,
                text,
                Snackbar.LENGTH_LONG
        ).show()
    }
}