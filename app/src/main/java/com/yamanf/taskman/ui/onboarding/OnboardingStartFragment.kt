package com.yamanf.taskman.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentOnboardingStartBinding


class OnboardingStartFragment : Fragment() {
    private var _binding: FragmentOnboardingStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingStartBinding.inflate(inflater,container,false)
        binding.btnGetStarted.setOnClickListener(){
            val navController = Navigation.findNavController(requireActivity(),R.id.onboardingContainer)
            navController.navigate(R.id.onboardingFinishFragment)
        }
        return binding.root
    }


}