package com.example.bkeep.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bkeep.R
import com.example.bkeep.auth.JwtUtils
import com.example.bkeep.auth.TokenManager
import com.example.bkeep.databinding.FragmentAccountBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.lib.data.user.UpdateUserRequest
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadUser()

        binding.btnSave.setOnClickListener {
            updateProfile()
        }

        binding.btnChangePassword.setOnClickListener {
            togglePasswordFields()
        }
    }

    private fun loadUser() {
        val token = TokenManager.getToken() ?: return
        val userId = JwtUtils.getUserId(token) // dobi id iz tokena

        lifecycleScope.launch {
            val response = RetrofitInstance.authApi.getUser(userId)
            if (response.isSuccessful) {
                val user = response.body()!!
                binding.etUsername.setText(user.username)
                binding.etEmail.setText(user.mail)
            }
        }
    }

    private fun updateProfile() {
        val request = UpdateUserRequest(
            username = binding.etUsername.text.toString(),
            mail = binding.etEmail.text.toString()
        )

        lifecycleScope.launch {
            RetrofitInstance.authApi.updateUser(request)
            Toast.makeText(requireContext(), "Profil posodobljen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePasswordFields() {
        val visible = binding.etNewPassword.visibility == View.VISIBLE
        binding.etNewPassword.visibility = if (visible) View.GONE else View.VISIBLE
        binding.etConfirmPassword.visibility = if (visible) View.GONE else View.VISIBLE

        if (!visible) {
            binding.btnChangePassword.setOnClickListener {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        val pass1 = binding.etNewPassword.text.toString()
        val pass2 = binding.etConfirmPassword.text.toString()

        if (pass1 != pass2) {
            Toast.makeText(requireContext(), "Gesli se ne ujemata", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            RetrofitInstance.authApi.updateUser(
                UpdateUserRequest(password = pass1)
            )
            Toast.makeText(requireContext(), "Geslo spremenjeno", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
