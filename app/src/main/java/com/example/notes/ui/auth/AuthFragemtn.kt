package com.example.notes.ui.auth

import android.accounts.AccountManager.KEY_PASSWORD
import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.data.remote.BasicAuthIntercepter
import com.example.notes.other.Constants.KEY_EMAIL
import com.example.notes.other.Constants.KEY_PASS
import com.example.notes.other.Constants.NO_EMAIL
import com.example.notes.other.Constants.NO_PASS
import com.example.notes.other.Status
import com.example.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragemtn:BaseFragment(R.layout.fragment_auth) {

    private val viewModel:AuthViewMode by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthIntercepter: BasicAuthIntercepter

    private var currEmail:String? = null
    private var currPassword:String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        if(isLoggedIn()){
            authernticateApi(currEmail ?:"",currPassword?:"")
            redirectLogin()
        }

        subscribeToObservers()

        btnRegister.setOnClickListener {
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            val confirmedPassword = etRegisterPasswordConfirm.text.toString()
            viewModel.register(email, password, confirmedPassword)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            currEmail = email
            currPassword = password
            viewModel.login(email, password)
        }
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragemtn, true)
            .build()
        findNavController().navigate(
            AuthFragemtnDirections.actionAuthFragemtnToNoteFragment(),
            navOptions
        )
    }

    fun authernticateApi(email:String,password:String){
        basicAuthIntercepter.email = email
        basicAuthIntercepter.password = password
    }


    private fun subscribeToObservers() {

        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status) {
                    Status.Success -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully logged in")
                        sharedPref.edit().putString(KEY_EMAIL, currEmail).apply()
                        sharedPref.edit().putString(KEY_PASS, currPassword).apply()
                        authernticateApi(currEmail ?: "", currPassword ?: "")
                        redirectLogin()
                    }
                    Status.Error -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occured")
                    }
                    Status.Loading -> {
                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })

        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status) {
                    Status.Success -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully registered an account")
                    }
                    Status.Error -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occurred")
                    }
                    Status.Loading -> {
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun isLoggedIn():Boolean{
        currEmail = sharedPref.getString(KEY_EMAIL, NO_EMAIL) ?: NO_EMAIL
        currPassword = sharedPref.getString(KEY_PASS, NO_PASS) ?: NO_PASS
        return currEmail != NO_EMAIL && currPassword != NO_PASS
    }

}