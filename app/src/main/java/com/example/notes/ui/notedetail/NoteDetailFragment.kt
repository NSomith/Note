package com.example.notes.ui.notedetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.data.local.entity.Note
import com.example.notes.other.Constants.ADD_OWNER_DIALOG
import com.example.notes.other.Status
import com.example.notes.ui.BaseFragment
import com.example.notes.ui.dialogue.AddOwnerDialogue
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_note_detail.*

@AndroidEntryPoint
class NoteDetailFragment:BaseFragment(R.layout.fragment_note_detail) {

    private val args:NoteDetailFragmentArgs by navArgs()
    private var curNote: Note? = null
    private val viewModel: NoteDetailViewModel by viewModels()

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
        if(savedInstanceState!=null){
            val addOwnerDialogue = parentFragmentManager.findFragmentByTag(ADD_OWNER_DIALOG) as AddOwnerDialogue?
            addOwnerDialogue?.setPostiveLIstener {
                addOnwerToCurrNote(it)
            }
        }

        subscribeToObservers()
        fabEditNote.setOnClickListener {
            findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.notedetailmenu,menu)
    }

    private fun addOnwerToCurrNote(email:String){
        curNote?.let { note->
            viewModel.addowenerTONote(email,note.id)
        }
    }

    private fun showAddOwnerDialogue(){
        AddOwnerDialogue().apply {
            setPostiveLIstener {
                addOnwerToCurrNote(it)
            }
        }.show(parentFragmentManager,ADD_OWNER_DIALOG)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miAddOwner->showAddOwnerDialogue()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(tvNoteContent, markdown)
    }

    private fun subscribeToObservers() {
        viewModel.addOwnerSatus.observe(viewLifecycleOwner, Observer {event->
            event?.getContentIfNotHandled()?.let {result->
                when(result.status){
                    Status.Success->{
                        addOwnerProgressBar.visibility = View.GONE
                        showSnackbar(result.data?:"Successfully added owner to note")
                    }
                    Status.Error->{
                        addOwnerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occured")
                    }
                    Status.Loading->{
                        addOwnerProgressBar.visibility = View.VISIBLE
                    }
                }

            }

        })
        viewModel.observeNoteById(args.id).observe(viewLifecycleOwner, Observer {
            it?.let { note ->
                tvNoteTitle.text = note.title
                setMarkdownText(note.content)
                curNote = note
            } ?: showSnackbar("Note not found")
        })
    }
}