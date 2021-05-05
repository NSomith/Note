package com.example.notes.data.local

import androidx.room.Database
import androidx.room.TypeConverters
import com.example.notes.data.local.entity.Note


@Database(entities = [Note::class],version = 1)
@TypeConverters(Converters::class)
abstract class NoteDataBase {
    abstract fun noteDao():NotesDao
}