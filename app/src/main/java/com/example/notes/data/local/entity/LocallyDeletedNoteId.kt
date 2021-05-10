package com.example.notes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locally_deleted_noteId")
data class LocallyDeletedNoteId (
    @PrimaryKey(autoGenerate = false)
    val deletedNoteId:String
    )