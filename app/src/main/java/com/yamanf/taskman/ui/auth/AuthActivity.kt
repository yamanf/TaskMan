package com.yamanf.taskman.ui.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.ActivityAuthBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.utils.gone
import com.yamanf.taskman.utils.visible

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private val authViewModel: AuthViewModel by viewModels(){
        AuthViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.progressBarBackground.alpha = 0.5f
        binding.progressBarBackground.gone()
        binding.indeterminateBar.gone()

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        binding.signInButton.setOnClickListener {
            binding.indeterminateBar.visible()
            binding.progressBarBackground.visible()
            signInGoogle()
        }
        val webViewDialog = PrivacyFragment()
        binding.tvTerms.setOnClickListener(){

            webViewDialog.show(supportFragmentManager, "Terms")
        }
        binding.tvPrivacy.setOnClickListener(){

            webViewDialog.show(supportFragmentManager, "Privacy")
        }


        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.progressBarBackground.gone()
        binding.indeterminateBar.gone()
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
            println(task.exception.toString())
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(this , MainActivity::class.java))
                finish()
            }else{
                println(it.exception.toString())
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }
}