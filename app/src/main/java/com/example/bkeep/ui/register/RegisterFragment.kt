package com.example.bkeep.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bkeep.auth.TokenManager
import com.example.bkeep.databinding.FragmentRegisterBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.bkeep.ui.login.LoginActivity
import com.example.lib.data.login.LoginRequest
import com.example.lib.data.register.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val confirmPassword = binding.etPasswordConfirm.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {

                Toast.makeText(requireContext(), "Izpolni vsa polja", Toast.LENGTH_SHORT).show()
            } else {
                if (password != confirmPassword){
                    Toast.makeText(requireContext(), "gesli nista enaki", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener;
                }
                registerUser(username, password,email)
            }
        }
    }

    private fun registerUser(username: String, password: String, email : String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.authApi.register(RegisterRequest(username, password,email))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        //TokenManager.saveToken(response.body()!!.resString) // shrani JWT
                        //Toast.makeText(requireContext(), "Uspešen login!", Toast.LENGTH_SHORT)
                            //.show()

                        (activity as? LoginActivity)?.onRegisterSuccess()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Napaka pri registraciji",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Napaka povezave: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}