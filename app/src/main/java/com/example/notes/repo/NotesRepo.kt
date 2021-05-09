package com.example.notes.repo

import android.content.Context
import com.example.notes.data.local.NotesDao
import com.example.notes.data.local.entity.Note
import com.example.notes.data.remote.NoteApi
import com.example.notes.data.remote.request.AccountRequest
import com.example.notes.other.CheckInternetConnection
import com.example.notes.other.Resource
import com.example.notes.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepo @Inject constructor(
    private val notesDao: NotesDao,
    private val noteApi: NoteApi,
    private val context: Context
){

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
                    noteApi.getNotes()
            },
            saveFetchResult = { response->
                response.body()?.let {
                    insertNotes(it)
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