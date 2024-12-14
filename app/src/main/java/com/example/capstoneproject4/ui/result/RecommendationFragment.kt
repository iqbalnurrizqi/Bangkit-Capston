package com.example.capstoneproject4.ui.result

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.RecommendationRequest
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.RecommendationRepository
import com.example.capstoneproject4.databinding.FragmentRecommendationBinding
import com.example.capstoneproject4.ui.adapter.ClinicRecommendationAdapter
import com.example.capstoneproject4.ui.adapter.ProductRecommendationAdapter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.getValue

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecommendationViewModel by viewModels {
        RecommendationViewModel.Factory(
            RecommendationRepository(RetrofitClient.instance.create(APIService::class.java))
        )
    }

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var productAdapter: ProductRecommendationAdapter
    private lateinit var clinicAdapter: ClinicRecommendationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).showToolbarAndBottomNavigation()

        // Inisialisasi RecyclerViews
        setupRecyclerViews()

        // Ambil data yang dikirim dari ResultFragment
        val skinType = arguments?.getString("skin_type") ?: ""
        val skinIssues = arguments?.getStringArrayList("skin_issues") ?: arrayListOf()

        // Validasi data sebelum melakukan fetch
        if (skinType.isNotEmpty() && skinIssues.isNotEmpty()) {
            val filteredIssues = skinIssues.filter { it.isNotEmpty() }
            fetchRecommendations(skinType, filteredIssues)
        } else {
            Toast.makeText(requireContext(), "Invalid skin type or issues. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViews() {
        // Adapter untuk produk
        productAdapter = ProductRecommendationAdapter()

        // Adapter untuk klinik
        clinicAdapter = ClinicRecommendationAdapter()

        // RecyclerView untuk produk
        binding.rvRecommendedProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // RecyclerView untuk klinik
        binding.rvRecommendedClinics.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = clinicAdapter
        }
    }

    private fun fetchRecommendations(skinType: String, skinIssues: List<String>) {
        lifecycleScope.launch {
            val token = dataStoreManager.getUserSession().firstOrNull()
            Log.d("RecommendationFragment", "Token retrieved: $token")

            if (token != null) {
                val bearerToken = "Bearer $token"

                viewModel.getRecommendations(
                    bearerToken,
                    RecommendationRequest(skinIssues, skinType),
                    onSuccess = { data ->
                        if (data.recommended_products.isNullOrEmpty() && data.recommended_clinics.isNullOrEmpty()) {
                            Toast.makeText(requireContext(), "No recommendations available.", Toast.LENGTH_SHORT).show()
                        } else {
                            productAdapter.submitList(data.recommended_products)
                            clinicAdapter.submitList(data.recommended_clinics)
                        }
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("RecommendationFragment", "Error: $errorMessage")
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}