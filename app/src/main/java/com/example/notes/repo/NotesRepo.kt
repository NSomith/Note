package com.example.notes.repo

import android.content.Context
import com.example.notes.data.local.NotesDao
import com.example.notes.data.local.entity.LocallyDeletedNoteId
import com.example.notes.data.local.entity.Note
import com.example.notes.data.remote.NoteApi
import com.example.notes.data.remote.request.AccountRequest
import com.example.notes.data.remote.request.DeleteRequest
import com.example.notes.other.CheckInternetConnection
import com.example.notes.other.Resource
import com.example.notes.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NotesRepo @Inject constructor(
    private val notesDao: NotesDao,
    private val noteApi: NoteApi,
    private val context: Context
){

    private var currNoteResponse:Response<List<Note>>? = null

    suspend fun deleteLocallyDeletedNoteId(deletedNoteId: String)
        = notesDao.deleteLocallyDeletedNoteId(deletedNoteId)

    suspend fun deleteNote(noteId:String){
        val response = try {
            noteApi.deleteNote(DeleteRequest(noteId))
        }catch (e:Exception){
            null
        }
        notesDao.deleteNoteById(noteId)
        if(response == null || !response.isSuccessful){
            notesDao.insertLocallyDeltedNoteId(LocallyDeletedNoteId(noteId))
        }else{
            notesDao.deleteLocallyDeletedNoteId(noteId)
        }
    }

    suspend fun syncNotes(){
        val locallyDeletedNoteId = notesDao.getAllLocallyDeletedNoteId()
        locallyDeletedNoteId.forEach { id -> deleteNote(id.deletedNoteId) }
        val unsyncedNote = notesDao.getAllUnsyncedNote()
        unsyncedNote.forEach { note-> insertNote(note) }
        currNoteResponse = noteApi.getNotes()
        currNoteResponse?.body()?.let { notes->
            notesDao.deleteAllNotes()
            insertNotes(notes.onEach { note-> note.isSynced = true })
        }
    }

    suspend fun insertNote(note:Note){
        val response = try{
            noteApi.addNote(note)
        }catch (e:Exception){
            null
        }
        if(response!=null && response.isSuccessful){
            notesDao.insertNote(note.apply { isSynced = true })
        }else{
            notesDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes:List<Note>){
        notes.forEach { insertNote(it) }
    }

    fun getAllNotes(): Flow<Resource<List<Note>>>{
        return networkBoundResource(
            query = {
                    notesDao.getAllNotes()
            },
            fetch = {
                    syncNotes()
                currNoteResponse
            },
            saveFetchResult = { response->
                response?.body()?.let { notes->
                    insertNotes(notes.onEach { note-> note.isSynced =true })
                }
            },
            shouldFetch = {
                CheckInternetConnection(context)
            }
        )
    }
    suspend fun getNoteById(id:String) = notesDao.getNoteById(id)

    suspend fun login(email:String,password:String) = withContext(Dispatchers.IO){
        try{
            val response = noteApi.login(AccountRequest(email,password))
            if(response.isSuccessful && response.body()!!.success){
                Resource.success(response.body()?.msg)
            }else{
                Resource.error(response.body()?.msg?: response.message(),null)
            }
        }catch (e:Exception){
            Resource.error("Check network connection",null)
        }
    }

    suspend fun register(email:String,password:String) = withContext(Dispatchers.IO){
        try{
            val response = noteApi.register(AccountRequest(email,password))
            if(response.isSuccessful && response.body()!!.success){
                Resource.success(response.body()?.msg)
            }else{
                Resource.error(response.body()?.msg?: response.message(),null)
            }
        }catch (e:Exception){
            Resource.error("Check network connection",null)
        }
    }
}