package com.yamanf.taskman.ui.auth.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.R
import com.yamanf.taskman.data.RegisterModel
import com.yamanf.taskman.databinding.FragmentRegisterBinding
import com.yamanf.taskman.ui.auth.AuthViewModel


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener(){
           register()
        }

        binding.tvLogin.setOnClickListener(){
            val navController= Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.navigate(R.id.loginFragment)
        }

        return binding.root
    }

    private fun register(){
        val eMail = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val passwordRepeat = binding.etPasswordRepeat.text.toString()
        val cbTerms = binding.cbTerms.isChecked
        val registerModel = RegisterModel(eMail, password , passwordRepeat, cbTerms)
        authViewModel.registerWithEmail(registerModel,
            {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            },{
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            })
    }


}