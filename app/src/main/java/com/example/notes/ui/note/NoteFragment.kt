package com.example.notes.ui.note

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.other.Constants.KEY_EMAIL
import com.example.notes.other.Constants.KEY_PASS
import com.example.notes.other.Constants.NO_EMAIL
import com.example.notes.other.Constants.NO_PASS
import com.example.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment:BaseFragment(R.layout.fragment_notes) {

    @Inject
    lateinit var sharedpref:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.milogout->logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout(){
        sharedpref.edit().putString(KEY_EMAIL,NO_EMAIL).apply()
        sharedpref.edit().putString(KEY_PASS,NO_PASS).apply()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.noteFragment,true)
            .build()
        findNavController().navigate(
            NoteFragmentDirections.actionNoteFragmentToAuthFragemtn(),
            navOptions
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}