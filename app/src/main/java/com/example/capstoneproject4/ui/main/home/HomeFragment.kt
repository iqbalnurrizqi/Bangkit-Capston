package com.example.capstoneproject4.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.Feature
import com.example.capstoneproject4.data.model.HomeResponse
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.HomeRepository
import com.example.capstoneproject4.databinding.FragmentHomeBinding
import com.example.capstoneproject4.ui.adapter.FeaturesAdapter
import com.example.capstoneproject4.ui.adapter.ProductsAdapter
import com.example.capstoneproject4.ui.adapter.RoutinesAdapter
import com.example.capstoneproject4.ui.main.routine.SharedViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var dataStoreManager: DataStoreManager

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pastikan binding tidak null
        if (_binding == null) return

        // Inisialisasi SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Sembunyikan toolbar dan tampilkan bottom navigation
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).showToolbarAndBottomNavigation()

        // Inisialisasi DataStoreManager
        dataStoreManager = DataStoreManager(requireContext())

        // Inisialisasi repository dan ViewModel
        val repository = HomeRepository(RetrofitClient.instance.create(APIService::class.java))
        val viewModelFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setupObservers()

        // Tampilkan ProgressBar saat memulai pengambilan data
        binding.progressBar.visibility = View.VISIBLE

        // Ambil token dari DataStoreManager
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStoreManager.getUserSession().collect { token ->
                    token?.let {
                        viewModel.fetchHomeData(it)
                    } ?: run {
                        binding.errorTextView.text = "Token not found. Please log in again."
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        // Simpan rutinitas ke SharedViewModel setelah data selesai dimuat
        viewModel.homeData.observe(viewLifecycleOwner) { homeResponse ->
            binding.progressBar.visibility = View.GONE
            populateData(homeResponse)

            // Simpan data rutinitas ke SharedViewModel
            sharedViewModel.setRoutines(homeResponse.routines)
        }
    }

    private fun setupObservers() {
        viewModel.homeData.observe(viewLifecycleOwner) { homeResponse ->
            binding.progressBar.visibility = View.GONE // Sembunyikan ProgressBar saat data selesai dimuat
            populateData(homeResponse)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.GONE
            binding.errorTextView.text = error
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun populateData(homeResponse: HomeResponse) {
        // Populate Features (Horizontal Layout)
        val features = listOf(
            Feature(R.drawable.ic_scan, "Scan", "Quickly scan items."),
            Feature(R.drawable.ic_history, "History", "View past activities."),
            Feature(R.drawable.ic_recommendation, "Recommendation", "Get tailored suggestions.")
        )
        binding.featuresRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = FeaturesAdapter(features)
        }

        // Populate Recommended Products (Horizontal Layout)
        binding.recommendedRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ProductsAdapter(requireContext(), homeResponse.recommended_products) { productId, productName, time ->
                lifecycleScope.launch {
                    dataStoreManager.getUserSession().collect { token ->
                        token?.let {
                            viewModel.addProductToRoutine(it, productId, productName, time)
                        } ?: run {
                            Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Populate Routines (Grid Layout with 2 columns)
        binding.routinesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = RoutinesAdapter(homeResponse.routines, homeResponse.recommended_products)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
