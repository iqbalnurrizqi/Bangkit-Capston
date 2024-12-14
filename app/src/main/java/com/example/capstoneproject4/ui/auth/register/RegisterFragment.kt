package com.example.capstoneproject4.ui.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.remote.AuthService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.AuthRepository
import com.example.capstoneproject4.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).hideToolbarAndBottomNavigation()

        val authService = RetrofitClient.instance.create(AuthService::class.java)
        authRepository = AuthRepository(authService)

        binding.buttonreg.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.name.text.toString()

            // Validasi input
            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi register dari AuthRepository
            authRepository.register(email, password, name) { result ->
                result.onSuccess {
                    Toast.makeText(requireContext(), "Register Success! Please Login", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }.onFailure { exception ->
                    Toast.makeText(requireContext(), "Register Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
}