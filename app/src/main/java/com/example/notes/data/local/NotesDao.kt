package com.example.notes.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notes.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("delete from notes where id = :noteId")
    suspend fun deleteNoteById(noteId:String)

    @Query("delete from notes where isSynced=1")
    suspend fun deleteAllSyncedNote()

    @Query("select * from notes where id = :noteid")
    fun observeNoteById(noteid:String):LiveData<Note>

    @Query("select * from notes where id = :noteid")
    suspend fun getNoteById(noteid:String):Note?

    @Query("select * from notes order by date desc")
    fun getAllNotes():Flow<List<Note>>

    @Query("select * from notes where isSynced=0")
    suspend fun getAllUnsyncedNote():List<Note>
}