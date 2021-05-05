package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.data.local.NoteDataBase
import com.example.notes.data.remote.BasicAuthIntercepter
import com.example.notes.data.remote.NoteApi
import com.example.notes.other.Constants.BASE_URL
import com.example.notes.other.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDataBase(@ApplicationContext context: Context)
     = Room.databaseBuilder(context,NoteDataBase::class.java,DB_NAME).build()

    @Singleton
    @Provides
    fun provideNoteDao(db:NoteDataBase) = db.noteDao()

    @Singleton
    @Provides
    fun provideBasicAuthIntercepter()
     = BasicAuthIntercepter()

    @Singleton
    @Provides
    fun provideNoteApi(basicAuthIntercepter: BasicAuthIntercepter):NoteApi{
        val client = OkHttpClient.Builder()
                .addInterceptor(basicAuthIntercepter)
                .build()
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(NoteApi::class.java)
    }

}