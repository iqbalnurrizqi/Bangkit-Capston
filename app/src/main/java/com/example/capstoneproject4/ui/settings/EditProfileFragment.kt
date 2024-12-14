package com.example.capstoneproject4.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.UpdateProfileRequest
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileFragment : Fragment() {


    private lateinit var viewModel: EditProfileViewModel
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var ivProfilePicture: ImageView
    private lateinit var radioGroupSkinType: RadioGroup
    private lateinit var checkboxSkinIssue: LinearLayout

    private val skinTypeOptions = listOf("Oily", "Dry", "Normal", "Combination")
    private val skinIssueOptions = listOf("Acne", "Blackhead", "Darkspot", "Enlarged Pore", "Redness", "Wrinkles")

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d("EditProfileFragment", "Selected URI: $it")
                Glide.with(this).load(it).into(ivProfilePicture) // Use Glide to load the image

                // Tambahkan lambda onResult
                viewModel.uploadPhoto(requireContext(), it) { photoUrl ->
                    if (photoUrl != null) {
                        Log.d("EditProfileFragment", "Photo uploaded successfully: $photoUrl")
                        // Simpan URL atau lakukan sesuatu dengan URL gambar
                    } else {
                        Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())
        // Initialize APIService
        val apiService = RetrofitClient.instance.create(APIService::class.java)

        // Initialize UserRepository with APIService
        val userRepository = UserRepository(apiService)

        // Pass kedua parameter ke ViewModelFactory
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataStoreManager, userRepository)
        )[EditProfileViewModel::class.java]

        // Observasi data profil
        observeUserProfile()

        // Muat data profil pengguna
        Log.d("EditProfileFragment", "Calling viewModel.loadUserProfile()")
        viewModel.loadUserProfile()

        dataStoreManager = DataStoreManager(requireContext())

        ivProfilePicture = view.findViewById(R.id.iv_profile_picture)
        radioGroupSkinType = view.findViewById(R.id.rg_skin_type) // Properti global
        checkboxSkinIssue = view.findViewById(R.id.checkbox_skin_issue) // Properti global

        val btnChangePicture: Button = view.findViewById(R.id.btn_change_picture)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        val etTreatmentGoal: EditText = view.findViewById(R.id.et_treatment_goal)

        // Set default value for treatment goal
        etTreatmentGoal.setText("Menjaga kesehatan kulit secara optimal")

        // Dynamically add radio buttons for skin types
        skinTypeOptions.forEach { option ->
            val radioButton = RadioButton(requireContext()).apply {
                text = option
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
            }
            radioGroupSkinType.addView(radioButton)
        }

        // Dynamically add checkboxes for skin issues
        skinIssueOptions.forEach { option ->
            val checkBox = CheckBox(requireContext()).apply {
                text = option
            }
            checkboxSkinIssue.addView(checkBox)
        }

        // Load existing profile data
        loadProfileData()

        // Handle change picture button
        btnChangePicture.setOnClickListener {
            if (isStoragePermissionGranted()) {
                pickImageLauncher.launch("image/*")
            } else {
                requestStoragePermission()
            }
        }

        // Handle tombol "Save"
        btnSave.setOnClickListener {
            lifecycleScope.launch {
                try {
                    Log.d("EditProfileFragment", "Save button clicked")
                    val token = dataStoreManager.getUserSession().first()

                    // Debugging untuk upload gambar
                    val savedImagePath = dataStoreManager.getUserProfileImagePath().firstOrNull()
                    val photoUrl = if (savedImagePath != null) {
                        val photoUri = Uri.parse(savedImagePath)
                        var uploadedUrl: String? = null
                        viewModel.uploadPhoto(requireContext(), photoUri) { url ->
                            uploadedUrl = url
                        }
                        uploadedUrl
                    } else null

                    // Debugging data sebelum dikirim ke server
                    Log.d("EditProfileFragment", "Collecting data for update request")
                    val name = view.findViewById<EditText>(R.id.et_name).text.toString()
                    val treatmentGoal = etTreatmentGoal.text.toString()

                    val selectedSkinTypeId = radioGroupSkinType.checkedRadioButtonId
                    val selectedSkinType = if (selectedSkinTypeId != -1) {
                        view.findViewById<RadioButton>(selectedSkinTypeId).text.toString()
                    } else ""
                    Log.d("EditProfileFragment", "Selected skin type: $selectedSkinType")

                    val selectedSkinIssues = skinIssueOptions.filterIndexed { index, _ ->
                        val checkBox = checkboxSkinIssue.getChildAt(index) as CheckBox
                        checkBox.isChecked
                    }
                    Log.d("EditProfileFragment", "Selected skin issues: $selectedSkinIssues")

                    val updateRequest = UpdateProfileRequest(
                        name = name,
                        skin_type = selectedSkinType,
                        skin_issues = selectedSkinIssues,
                        treatment_goal = treatmentGoal
                    )
                    Log.d("EditProfileFragment", "Update request created: $updateRequest")

                    val profileUpdateResponse = viewModel.updateUserProfile("Bearer $token", updateRequest)
                    if (profileUpdateResponse.isSuccessful) {
                        Log.d("EditProfileFragment", "Profile updated successfully")
                        findNavController().navigateUp()
                    } else {
                        Log.e("EditProfileFragment", "Profile update failed: ${profileUpdateResponse.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("EditProfileFragment", "Exception in save button click: ${e.message}", e)
                }
            }
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("EditProfileFragment", "Storage permission granted: $granted")
        return granted
    }

    private fun requestStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermissions(permissions, REQUEST_STORAGE_PERMISSION)
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            try {
                Log.d("EditProfileFragment", "Loading profile data from DataStore")
                val name = dataStoreManager.getUserSession().first() ?: ""
                val treatmentGoal = dataStoreManager.getTreatmentGoal().first() ?: ""
                val skinType = dataStoreManager.getSkinType().first() ?: ""
                val skinIssues = dataStoreManager.getSkinIssues().first()

                Log.d("EditProfileFragment", "Profile data loaded: name=$name, treatmentGoal=$treatmentGoal, skinType=$skinType, skinIssues=$skinIssues")

                // Tampilkan data pada UI
                view?.findViewById<EditText>(R.id.et_name)?.setText(name)
                view?.findViewById<EditText>(R.id.et_treatment_goal)?.setText(treatmentGoal)

                // Pilih skin type
                skinTypeOptions.forEachIndexed { index, option ->
                    if (option == skinType) {
                        val radioButton = radioGroupSkinType.getChildAt(index) as RadioButton
                        radioButton.isChecked = true
                    }
                }

                // Tandai skin issues
                skinIssueOptions.forEachIndexed { index, option ->
                    val checkBox = checkboxSkinIssue.getChildAt(index) as CheckBox
                    checkBox.isChecked = skinIssues.contains(option)
                }
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Error loading profile data: ${e.message}", e)
            }
        }
    }

    private fun observeUserProfile() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                // Set nilai EditText untuk nama dan tujuan perawatan
                view?.findViewById<EditText>(R.id.et_name)?.setText(it.name)
                view?.findViewById<EditText>(R.id.et_treatment_goal)?.setText(it.treatment_goal)

                // Pilih tipe kulit
                val selectedSkinType = it.skin_type
                skinTypeOptions.forEachIndexed { index, option ->
                    if (option == selectedSkinType) {
                        val radioButton = radioGroupSkinType.getChildAt(index) as RadioButton
                        radioButton.isChecked = true
                    }
                }

                // Tandai skin issues yang dipilih
                val selectedSkinIssues = it.skin_issues
                skinIssueOptions.forEachIndexed { index, option ->
                    val checkBox = checkboxSkinIssue.getChildAt(index) as CheckBox
                    checkBox.isChecked = selectedSkinIssues.contains(option)
                }

                // Muat foto profil
                Glide.with(this).load(it.photo_url).into(ivProfilePicture)
            }
        }
    }


    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 1
    }
}