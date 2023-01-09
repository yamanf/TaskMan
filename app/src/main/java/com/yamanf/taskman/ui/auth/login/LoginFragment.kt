package com.yamanf.taskman.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.R
import com.yamanf.taskman.data.LoginModel
import com.yamanf.taskman.databinding.FragmentLoginBinding
import com.yamanf.taskman.ui.auth.AuthViewModel



class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.btnLogin.setOnClickListener(){
            val eMail = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val cbRemember = binding.cbRemember.isChecked
            val loginModel = LoginModel(eMail,password,cbRemember)
            authViewModel.loginWithEmail(loginModel,
                {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                },{
                    Toast.makeText(requireContext(),it, Toast.LENGTH_SHORT).show()
                })
        }
        binding.tvRegisterText.setOnClickListener(){
            val navController= Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.navigate(R.id.registerFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}