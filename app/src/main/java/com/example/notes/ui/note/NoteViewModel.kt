package com.example.notes.ui.note

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.notes.data.local.entity.LocallyDeletedNoteId
import com.example.notes.data.local.entity.Note
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repo.NotesRepo
import kotlinx.coroutines.launch

class NoteViewModel @ViewModelInject constructor(
    val repo: NotesRepo
) :ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    fun syncAllNotes() = _forceUpdate.postValue(true)

    private val _allNotes = _forceUpdate.switchMap {
        repo.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

    fun deleteLocallyDeleteNoteId(deletedNoteId: String) = viewModelScope.launch {
        repo.deleteLocallyDeletedNoteId(deletedNoteId)
    }

    fun deleteNote(noteId:String) = viewModelScope.launch {
        repo.deleteNote(noteId)
    }
    fun insertNote(note:Note) = viewModelScope.launch {
        repo.insertNote(note)
    }
}