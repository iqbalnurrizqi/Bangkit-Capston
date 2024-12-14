package com.example.capstoneproject4.ui.result

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.helper.SkinDiseaseClassifierHelper
import com.example.capstoneproject4.data.helper.SkinTypeClassifierHelper
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import com.example.capstoneproject4.data.model.ScanRequest
import com.example.capstoneproject4.data.model.SkinIssue
import com.example.capstoneproject4.data.model.SkinType
import com.example.capstoneproject4.data.model.UpdateProfileRequest
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.AnalysisResultRepository
import com.example.capstoneproject4.ui.adapter.SkinDiseaseAdapter
import com.example.capstoneproject4.ui.main.history.HistoryViewModel
import com.example.capstoneproject4.ui.settings.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private lateinit var skinTypeHelper: SkinTypeClassifierHelper
    private lateinit var skinDiseaseHelper: SkinDiseaseClassifierHelper
    private lateinit var skinDiseaseAdapter: SkinDiseaseAdapter
    @Inject
    lateinit var analysisResultRepository: AnalysisResultRepository
    private val historyViewModel: HistoryViewModel by viewModels()
    private val editProfileViewModel: EditProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scan_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        // Inisialisasi view
        val scannedImage: ImageView = view.findViewById(R.id.iv_scanned_image)
        val skinTypeProgressText: TextView = view.findViewById(R.id.tv_skin_type_progress_text)
        val skinTypeForeground: TextView = view.findViewById(R.id.tv_skin_type_foreground)
        val skinTypeBackground: TextView = view.findViewById(R.id.tv_skin_type_background)
        val rvSkinDisease: RecyclerView = view.findViewById(R.id.rv_skin_disease_progress)
        val btnViewRecommendations: Button = view.findViewById(R.id.btn_view_recommendations)
        val btnSaveToHistory: Button = view.findViewById(R.id.btn_save_to_history)

        // Atur RecyclerView untuk daftar penyakit kulit
        skinDiseaseAdapter = SkinDiseaseAdapter()
        rvSkinDisease.layoutManager = LinearLayoutManager(requireContext())
        rvSkinDisease.adapter = skinDiseaseAdapter

        // Ambil URI gambar dari arguments
        val imageUriString = arguments?.getString("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            scannedImage.setImageURI(imageUri)

            // Inisialisasi helper untuk jenis kulit
            skinTypeHelper = SkinTypeClassifierHelper(requireContext())
            val (skinType, skinTypeConfidence) = skinTypeHelper.classifySkinType(imageUri)

            // Atur progress bar untuk jenis kulit
            val skinTypePercentage = skinTypeConfidence * 100
            skinTypeProgressText.text = String.format("%.2f%% (%s)", skinTypePercentage, skinType)
            skinTypeBackground.viewTreeObserver.addOnGlobalLayoutListener {
                setProgressBar(skinTypeBackground, skinTypeForeground, skinTypePercentage)
            }

            // Inisialisasi helper untuk penyakit kulit
            skinDiseaseHelper = SkinDiseaseClassifierHelper(requireContext())
            val predictions = skinDiseaseHelper.classifySkinImage(imageUri)

            // Tambahkan hasil prediksi ke adapter
            val skinIssues = predictions.map { (label, confidence) ->
                SkinDiseaseAdapter.SkinDiseaseItem(label, confidence * 100)
            }
            skinDiseaseAdapter.submitList(skinIssues)

            Log.d("ResultFragment", "Skin Type: $skinType, Skin Issues: $skinIssues")

            // ** Tambahkan logika penyimpanan data ke DataStore **
            lifecycleScope.launch {
                // Filter predictions dengan confidence di atas 60%
                val filteredIssues = predictions.filter { it.second > 0.6f }.joinToString(", ") { it.first }
                val userId = DataStoreManager(requireContext()).getUserSession().firstOrNull()
                val skinIssueList = predictions.map { SkinIssue(it.second, it.first) }
                val analysisResult = AnalysisResultEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId.toString(),
                    imageUri = imageUri.toString(),
                    skinType = skinType,
                    skinIssues = skinIssueList,
                    timestamp = System.currentTimeMillis()
                )

                historyViewModel.addAnalysisResult(analysisResult)
                Toast.makeText(requireContext(), "Analysis result saved offline.", Toast.LENGTH_SHORT).show()

                // Simpan data ke DataStore
                DataStoreManager(requireContext()).saveProfileData(
                    name = "User",
                    skinType = skinType,
                    skinIssue = filteredIssues
                )
                Log.d("ResultFragment", "Data saved to DataStore: SkinType=$skinType, Issues=$filteredIssues")
            }

            btnSaveToHistory.setOnClickListener {
                lifecycleScope.launch {
                    val userId = DataStoreManager(requireContext()).getUserSession().firstOrNull()
                    if (userId == null) {
                        Toast.makeText(requireContext(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Siapkan data skin type dan skin issues
                    val filteredIssues = predictions.filter { it.second > 0.6f }.map { it.first }
                    val token = userId // token diambil dari DataStore

                    val updateRequest = UpdateProfileRequest(
                        name = "", // Tidak mengubah nama
                        skin_type = skinType,
                        skin_issues = filteredIssues,
                        treatment_goal = "" // Tidak mengubah treatment goal
                    )

                    // Panggil fungsi update user profile
                    try {
                        val response = editProfileViewModel.updateUserProfile(token, updateRequest)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    // Simpan ke database lokal (opsional, jika diperlukan)
                    val skinIssueList = predictions.map { SkinIssue(it.second, it.first) }
                    val analysisResult = AnalysisResultEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        imageUri = imageUri.toString(),
                        skinType = skinType,
                        skinIssues = skinIssueList,
                        timestamp = System.currentTimeMillis()
                    )
                    historyViewModel.addAnalysisResult(analysisResult)

                    Toast.makeText(requireContext(), "Analysis result saved to history.", Toast.LENGTH_SHORT).show()
                }
            }


            // Logika tombol untuk melihat rekomendasi
            btnViewRecommendations.setOnClickListener {
                lifecycleScope.launch {
                    val token = DataStoreManager(requireContext()).getUserSession().firstOrNull()
                    if (token == null) {
                        Toast.makeText(requireContext(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    sendAnalysisResult(
                        token = token,
                        imageUri = imageUriString,
                        skinType = skinType,
                        skinTypeConfidence = skinTypeConfidence,
                        skinIssues = predictions
                    )

                    // Navigasi ke halaman rekomendasi
                    val filteredSkinIssues = predictions.filter { it.second > 0.6f }.map { it.first }
                    if (filteredSkinIssues.isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString("skin_type", skinType)
                            putStringArrayList("skin_issues", ArrayList(filteredSkinIssues))
                        }
                        findNavController().navigate(R.id.action_resultFragment_to_recommendationFragment, bundle)
                    } else {
                        Toast.makeText(requireContext(), "No significant skin issues detected.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sendAnalysisResult(token: String, imageUri: String, skinType: String, skinTypeConfidence: Float, skinIssues: List<Pair<String, Float>>) {
        val skinTypeObj = SkinType(skinType, skinTypeConfidence)
        val skinIssueList = skinIssues.map { SkinIssue(it.second, it.first) }

        val request = ScanRequest(image_uri = imageUri, skin_type = skinTypeObj.toString(), skin_issues = skinIssueList)

        val apiService = RetrofitClient.instance.create(APIService::class.java)

        lifecycleScope.launch {
            try {
                val response = apiService.uploadAnalysisResult("Bearer $token", request)

                if (response.status) {
                    Toast.makeText(requireContext(), "Analysis result uploaded successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    when (response.statusCode) {
                        400 -> Toast.makeText(requireContext(), "Bad request. Please check your input.", Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(requireContext(), "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(requireContext(), "Failed to upload analysis result.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        skinTypeHelper.closeModel()
        skinDiseaseHelper.closeModel()
    }

    private fun setProgressBar(progressText: TextView, progressForeground: TextView, percentage: Float) {
        val layoutParams = progressForeground.layoutParams
        layoutParams.width = (progressText.width * (percentage / 100)).toInt()
        progressForeground.layoutParams = layoutParams
    }
}