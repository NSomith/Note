package com.example.notes.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.other.Resource
import com.example.notes.repo.NotesRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


class AuthViewMode @ViewModelInject constructor(
    private val repo: NotesRepo
) :ViewModel() {


    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus:LiveData<Resource<String>> = _registerStatus

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus:LiveData<Resource<String>> = _loginStatus

    fun register(email:String,password:String,repeatedPassword:String){
        _registerStatus.postValue(Resource.loading(null))
        if(email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()){
            _registerStatus.postValue(Resource.error("enter all the field",null))
            return
        }
        if(password!=repeatedPassword){
            _registerStatus.postValue(Resource.error("password not match",null))
            return
        }
        viewModelScope.launch {
            val result = repo.register(email,password)
            _registerStatus.postValue(result)
        }
    }

    fun login(email:String,password:String){
        _loginStatus.postValue(Resource.loading(null))
        if(email.isEmpty() || password.isEmpty()){
            _loginStatus.postValue(Resource.error("enter all the field",null))
            return
        }

        viewModelScope.launch {
            val result = repo.login(email,password)
            _loginStatus.postValue(result)
        }
    }
}