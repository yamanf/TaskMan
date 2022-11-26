package com.yamanf.taskman.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamanf.taskman.databinding.FragmentOnboardingFinishBinding
import com.yamanf.taskman.ui.auth.AuthActivity

class OnboardingFinishFragment : Fragment() {
    private var _binding : FragmentOnboardingFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingFinishBinding.inflate(inflater,container,false)
        binding.btnLogin.setOnClickListener(){
            startActivity(Intent(requireContext(), AuthActivity::class.java))
        }

        return binding.root
    }



}