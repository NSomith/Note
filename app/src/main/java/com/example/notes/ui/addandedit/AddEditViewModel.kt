package com.example.notes.ui.addandedit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.entity.Note
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repo.NotesRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddEditViewModel @ViewModelInject constructor(val notesRepo: NotesRepo):ViewModel() {

    private val _note = MutableLiveData<Event<Resource<Note>>>()
    val note:LiveData<Event<Resource<Note>>> = _note

    fun insertNote(note:Note) = GlobalScope.launch {
        notesRepo.insertNote(note)
    }

    fun getNotById(noteid:String) = viewModelScope.launch {
        _note.postValue(Event(Resource.loading(null)))
        val note = notesRepo.getNoteById(noteid)
        note?.let {
            _note.postValue(Event(Resource.success(it)))
        } ?: _note.postValue(Event(Resource.error("Note not found",null)))

    }
}