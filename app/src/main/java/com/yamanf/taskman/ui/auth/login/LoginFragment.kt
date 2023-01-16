package com.yamanf.taskman.ui.auth.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentLoginBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.auth.AuthViewModel
import com.yamanf.taskman.ui.auth.AuthViewModelFactory
import com.yamanf.taskman.utils.gone
import kotlinx.coroutines.delay


const val RC_SIGN_IN = 1


class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    private val authViewModel: AuthViewModel by viewModels(){
        AuthViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}