package com.example.capstoneproject4.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.remote.AuthService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.AuthRepository
import com.example.capstoneproject4.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var authRepository: AuthRepository
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).hideToolbarAndBottomNavigation()

        // Inisialisasi AuthRepository dan DataStoreManager
        val authService = RetrofitClient.instance.create(AuthService::class.java)
        authRepository = AuthRepository(authService)
        dataStoreManager = DataStoreManager(requireContext())

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val rememberMe = binding.cbRememberMe.isChecked

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authRepository.login(email, password) { result ->
                result.onSuccess { response ->
                    val token = response.data
                    lifecycleScope.launch {
                        dataStoreManager.saveUserSession(token)
                        dataStoreManager.setRememberMe(rememberMe)
                    }
                    Toast.makeText(requireContext(), "Login Success!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }.onFailure { exception ->
                    Toast.makeText(requireContext(), "Login Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            // Simpan status Remember Me di DataStore
            lifecycleScope.launch {
                dataStoreManager.setRememberMe(isChecked)
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }
}