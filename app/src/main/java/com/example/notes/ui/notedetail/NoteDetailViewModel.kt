package com.example.notes.ui.notedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.entity.Note
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repo.NotesRepo
import kotlinx.coroutines.launch

class NoteDetailViewModel @ViewModelInject constructor(
    private val notesRepo: NotesRepo
):ViewModel() {

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()
    val addOwnerSatus:LiveData<Event<Resource<String>>> = _addOwnerStatus

    fun addowenerTONote(owner:String,noteId: String){
        _addOwnerStatus.postValue(Event(Resource.loading(null)))
        if(owner.isEmpty() || noteId.isEmpty()){
            _addOwnerStatus.postValue(Event(Resource.error("the owner cant be empty",null)))
            return
        }
        viewModelScope.launch {
            val result = notesRepo.addOwner(owner,noteId)
            _addOwnerStatus.postValue(Event(result))
        }
    }

    fun observeNoteById(noteId: String) = notesRepo.observeNoteById(noteId)
}