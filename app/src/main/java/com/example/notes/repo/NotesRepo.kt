package com.example.notes.repo

import android.content.Context
import com.example.notes.data.local.NotesDao
import com.example.notes.data.remote.NoteApi
import com.example.notes.data.remote.request.AccountRequest
import com.example.notes.other.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepo @Inject constructor(
//    private val notesDao: NotesDao,
    private val noteApi: NoteApi,
//    private val context: Context
){

    suspend fun login(email:String,password:String) = withContext(Dispatchers.IO){
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