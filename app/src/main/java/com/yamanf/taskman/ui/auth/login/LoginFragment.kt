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

/*
        binding.btnLogin.setOnClickListener(){
            val eMail = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val cbRemember = binding.cbRemember.isChecked
            val loginModel = LoginModel(eMail,password,cbRemember)
            authViewModel.loginWithEmail(loginModel,
                {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                },{
                    Toast.makeText(requireContext(),it, Toast.LENGTH_SHORT).show()
                })
        }


        binding.tvRegisterText.setOnClickListener(){
            val navController= Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.navigate(R.id.registerFragment)
        }

         */




        binding.signInButton.setSize(SignInButton.SIZE_WIDE)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity() , gso)

        binding.signInButton.setOnClickListener {
            signInGoogle()
        }



        return binding.root
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
            updateUI(account)
            }
        }else{
            Toast.makeText(requireContext(), task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(requireContext() , MainActivity::class.java))
            }else{
                Toast.makeText(requireContext(), it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}