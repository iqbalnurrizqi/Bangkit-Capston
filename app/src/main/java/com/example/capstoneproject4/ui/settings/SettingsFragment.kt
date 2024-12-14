package com.example.capstoneproject4.ui.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.worker.RoutineNotificationWorker
import com.example.capstoneproject4.ui.main.routine.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by activityViewModels()
    private var isSwitchManuallyUpdated = false // Flag untuk mencegah looping
    private lateinit var sharedViewModel: SharedViewModel
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sembunyikan toolbar dan tampilkan bottom navigation
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).showToolbarAndBottomNavigation()

        // Inisialisasi UI
        val profileImage = view.findViewById<ImageView>(R.id.image_profile)
        val userName = view.findViewById<TextView>(R.id.tv_user_name)
        val skinType = view.findViewById<TextView>(R.id.tv_skin_type)
        val skinProblems = view.findViewById<TextView>(R.id.tv_skin_problems)
        val treatmentGoals = view.findViewById<TextView>(R.id.tv_treatment_goals)
        val btnEditProfile = view.findViewById<Button>(R.id.btn_edit_profile)
        val btnAboutUs = view.findViewById<Button>(R.id.btn_about_us)
        val btnPrivacyPolicy = view.findViewById<Button>(R.id.btn_privacy_policy)
        val btnTermsConditions = view.findViewById<Button>(R.id.btn_terms_conditions)
        val btnFeedbackSupport = view.findViewById<Button>(R.id.btn_feedback_support)
        val darkModeSwitch = view.findViewById<Switch>(R.id.switch_dark_mode)
        val notificationSwitch = view.findViewById<Switch>(R.id.switch_push_notification)
        // Inisialisasi SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Observasi data profil
        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                Glide.with(requireContext())
                    .load(profile.data.data.photo_url.takeIf { it != "N/A" } ?: R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(profileImage)

                userName.text = profile.data.data.name ?: "Unknown User"
                skinType.text = "Skin Type: ${profile.data.data.skin_type ?: "Not Specified"}"
                skinProblems.text = "Skin Problems: ${
                    profile.data.data.skin_issues?.joinToString(", ") ?: "None"
                }"
                treatmentGoals.text = "Treatment Goals: ${profile.data.data.treatment_goal ?: "Not Specified"}"
            } else {
                userName.text = "No Data"
                skinType.text = "Skin Type: Not Specified"
                skinProblems.text = "Skin Problems: None"
                treatmentGoals.text = "Treatment Goals: Not Specified"
                Glide.with(requireContext())
                    .load(R.drawable.ic_placeholder)
                    .into(profileImage)
            }
        }

        viewModel.loadUserProfile()

        checkAndRequestNotificationPermission()

        // Navigasi ke EditProfileFragment
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_editProfileFragment)
        }

        // Setting click listeners to show dialogs
        btnAboutUs.setOnClickListener { showCustomDialog("About Us", getAboutUsContent()) }
        btnPrivacyPolicy.setOnClickListener { showCustomDialog("Privacy Policy", getPrivacyPolicyContent()) }
        btnTermsConditions.setOnClickListener { showCustomDialog("Terms and Conditions", getTermsAndConditionsContent()) }
        btnFeedbackSupport.setOnClickListener { showCustomDialog("Feedback and Support", getFeedbackAndSupportContent()) }

        // Observasi dark mode
        viewModel.darkMode.observe(viewLifecycleOwner) { isDarkModeEnabled ->
            Log.d("SettingsFragment", "Observed dark mode state: $isDarkModeEnabled")

            // Update Switch tanpa memicu listener
            if (darkModeSwitch.isChecked != isDarkModeEnabled) {
                isSwitchManuallyUpdated = true
                darkModeSwitch.isChecked = isDarkModeEnabled
                isSwitchManuallyUpdated = false
            }

            updateTheme(isDarkModeEnabled)
        }

        // Set listener Switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isSwitchManuallyUpdated) { // Hanya proses jika perubahan dari user
                lifecycleScope.launch {
                    Log.d("SettingsFragment", "Switch toggled: $isChecked")
                    viewModel.setDarkMode(isChecked)
                }
            }
        }

        // Observasi data rutinitas
        sharedViewModel.routines.observe(viewLifecycleOwner) { routines ->
            // Gunakan data rutinitas untuk mengatur notifikasi
            viewModel.pushNotification.observe(viewLifecycleOwner) { isEnabled ->
                if (isEnabled) {
                    routines.forEach { routine ->
                        val delay = calculateDelay(routine.time) // Hitung delay
                        scheduleRoutineNotification(
                            requireContext(),
                            routine.product_name,
                            routine.time,
                            delay
                        )
                    }
                } else {
                    WorkManager.getInstance(requireContext()).cancelAllWork()
                }
            }
        }
    }

    private fun scheduleRoutineNotification(
        context: Context,
        productName: String,
        routineTime: String,
        delay: Long
    ) {
        val workRequest = OneTimeWorkRequestBuilder<RoutineNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "PRODUCT_NAME" to productName,
                    "ROUTINE_TIME" to routineTime
                )
            )
            .build()

        // Membuat nama unik berbasis waktu
        val uniqueWorkName = "RoutineWork_${productName}_${System.currentTimeMillis()}"

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName,  // Nama unik untuk pekerjaan ini
            ExistingWorkPolicy.KEEP,  // Tidak mengganti pekerjaan jika sudah ada
            workRequest
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lakukan aksi yang diperlukan
                Log.d("SettingsFragment", "Izin notifikasi diberikan")
            } else {
                // Izin ditolak, tampilkan pesan atau tindakan lain
                Log.d("SettingsFragment", "Izin notifikasi ditolak")
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val isPermissionGranted = requireContext().checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!isPermissionGranted) {
                // Minta izin kepada pengguna
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun calculateDelay(time: String): Long {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val routineTime = formatter.parse(time)
        val currentTime = Calendar.getInstance().time

        val delay = routineTime.time - currentTime.time
        return if (delay < 0) delay + 24 * 60 * 60 * 1000 else delay
    }


    private fun updateTheme(isDarkModeEnabled: Boolean) {
        val mode = if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        // Hanya ubah mode jika berbeda
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun showCustomDialog(title: String, content: String) {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val tvContent = dialogView.findViewById<TextView>(R.id.tv_dialog_content)

        tvTitle.text = title
        tvContent.text = content

        // Create and show the dialog
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
            .show()
    }

    private fun getAboutUsContent(): String {
        return "We are a dedicated team passionate about empowering individuals to achieve their best skincare results. Using advanced technology and in-depth research, our app provides personalized skin analysis and recommendations. Thank you for trusting the Skin Face Analyzer as your skincare companion."
    }

    private fun getPrivacyPolicyContent(): String {
        return "We value your privacy and are committed to protecting your personal data. The Skin Face Analyzer app collects the following data to provide you with an optimal experience:\n\n" +
                "- Camera: Used to analyze your skin condition through facial images. All images are processed locally and are only utilized for skin analysis purposes.\n" +
                "- Location (optional): Used to recommend nearby clinics, activated only with your permission.\n" +
                "- User Data: Including name and skin analysis history, to offer personalized skincare recommendations.\n\n" +
                "All data is securely stored and handled in compliance with our privacy policy. We never share your data with third parties without your explicit consent."
    }

    private fun getTermsAndConditionsContent(): String {
        return "By using the Skin Face Analyzer app, you agree to the following terms and conditions:\n\n" +
                "1. Users are prohibited from uploading irrelevant or offensive content through the camera feature.\n" +
                "2. Misusing the app’s recommendation feature or user data for illegal activities is strictly prohibited.\n\n" +
                "Currently, all app features are completely free, including skin analysis, product recommendations, and other functionalities. We reserve the right to modify these terms in future updates."
    }

    private fun getFeedbackAndSupportContent(): String {
        return "Your feedback helps us improve! If you encounter any issues or have suggestions, here’s how you can reach us:\n\n" +
                "- Support Email: Contact us at support@dermateam.com for any assistance.\n" +
                "- App Store Reviews: Leave a review on the Google Play Store to share your experience.\n\n" +
                "We’re here to assist you and appreciate your input in making Skin Face Analyzer better for everyone."
    }
}
