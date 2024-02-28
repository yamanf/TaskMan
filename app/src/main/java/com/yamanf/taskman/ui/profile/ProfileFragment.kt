package com.yamanf.taskman.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentProfileBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.auth.AuthActivity
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.Utils

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels() {
        ProfileModelFactory(
            FirebaseRepositoryImpl()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        updateUserdata()
        val url = "https://www.buymeacoffee.com/furkanyaaman"

        binding.apply {
            btnGoBack.setOnClickListener { it.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment()) }
            tvUsername.setOnLongClickListener {
                changeUsernameDialog { changeUsername(it) }
                return@setOnLongClickListener true
            }
            btnBuyMeCoffee.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            btnSendEmail.setOnClickListener{sendEmail()}
            btnChangeUsername.setOnClickListener {
                changeUsernameDialog {
                    if (it) {
                        profileViewModel.getCurrentUser()?.let { user ->
                            if (user.displayName.isNullOrBlank()) {
                                tvUsername.text = user.email!!.split("@")[0]
                            } else tvUsername.text = user.displayName
                            tvEmail.text = user.email
                        }
                    }
                }
            }
            btnSignOut.setOnClickListener { signOut() }
            btnDeleteAccount.setOnClickListener { deleteAccountDialog() }
        }
        return binding.root
    }

    private fun updateUserdata() {
        profileViewModel.getCurrentUser()?.let { user ->
            if (user.displayName.isNullOrBlank()) {
                binding.tvUsername.text = user.email!!.split("@")[0]
            } else binding.tvUsername.text = user.displayName
            binding.tvEmail.text = user.email
            Glide.with(requireParentFragment())
                .load(user.photoUrl)
                .into(binding.ivProfile)
            println(user.photoUrl.toString())
        }

    }

    private fun deleteAccountDialog() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.delete_account))
            setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account))
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(context, getString(R.string.you_cancelled), Toast.LENGTH_SHORT)
                    .show()
            }
            setPositiveButton(getString(R.string.ok)) { dialog, which ->
                deleteAccount()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAccount() {
        profileViewModel.deleteUser {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_could_not_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeUsernameDialog(result: (Boolean) -> Unit) {
        Utils.showEditTextDialog(
            getString(R.string.change_username),
            getString(R.string.enter_new_username),
            getString(R.string.change),
            layoutInflater,
            requireContext()
        ) { username ->
            if (username.isNotBlank() && username.length <= Constants.MAX_USERNAME_LENGTH) {
                profileViewModel.updateUsername(username) {
                    return@updateUsername result(it)
                }
            } else if (username.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.username_cannot_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (username.length > Constants.MAX_WORKSPACE_TITLE_LENGTH) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.username_cannot_be_longer),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeUsername(boolean: Boolean) {
        if (boolean) {
            profileViewModel.getCurrentUser()?.let { user ->
                if (user.displayName.isNullOrBlank()) {
                    binding.tvUsername.text = user.email!!.split("@")[0]
                } else binding.tvUsername.text = user.displayName
                binding.tvEmail.text = user.email
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("app.taskman@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))

    }
    private fun signOut(){
        profileViewModel.logOut()
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        activity?.finish()
    }


}