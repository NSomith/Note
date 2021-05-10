package com.example.notes.ui.note

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.adapter.NoteAdapter
import com.example.notes.other.Constants.KEY_EMAIL
import com.example.notes.other.Constants.KEY_PASS
import com.example.notes.other.Constants.NO_EMAIL
import com.example.notes.other.Constants.NO_PASS
import com.example.notes.other.Status
import com.example.notes.ui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notes.*
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment:BaseFragment(R.layout.fragment_notes) {

    @Inject
    lateinit var sharedpref:SharedPreferences

    private lateinit var noteAdapter: NoteAdapter
    private val viewModel:NoteViewModel by viewModels()

    val swiping = MutableLiveData<Boolean>(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER
        setRecylerView()
        subscribetoObserver()
        noteAdapter.setOnItemClickListener {
            findNavController().navigate(
                NoteFragmentDirections.actionNoteFragmentToNoteDetailFragment(it.id)
            )
        }
        fabAddNote.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(""))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    val ItemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
        0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ){

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                swiping.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            viewModel.deleteNote(note.id)
            Snackbar.make(requireView(),"Note was deleted",Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    viewModel.insertNote(note)
                    viewModel.deleteLocallyDeleteNoteId(note.id)
                }
                show()
            }
        }

    }

    private fun subscribetoObserver(){
        viewModel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let {event->
               val result = event.peekContent()
               when(result.status){
                   Status.Success->{
                       noteAdapter.notes = result.data!!
                       swipeRefreshLayout.isRefreshing = false
                   }
                   Status.Error->{
                       event.getContentIfNotHandled()?.let {
                           it.message?.let {
                               showSnackbar(it)
                           }
                       }
                       result.data?.let {
                           noteAdapter.notes = it
                       }
                       swipeRefreshLayout.isRefreshing = false
                   }
                   Status.Loading->{
                       result.data?.let {
                           noteAdapter.notes = it
                       }
                       swipeRefreshLayout.isRefreshing = true
                   }
               }
            }
        })

        swiping.observe(viewLifecycleOwner, Observer {
            swipeRefreshLayout.isEnabled = !it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.milogout->logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setRecylerView() = rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(ItemTouchHelperCallback).attachToRecyclerView(this)
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


}