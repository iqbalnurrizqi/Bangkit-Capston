package com.example.capstoneproject4.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.repository.UserRepository

class ViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(dataStoreManager, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}