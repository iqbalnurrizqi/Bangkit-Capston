package com.example.capstoneproject4.ui.main.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import com.example.capstoneproject4.ui.adapter.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sembunyikan toolbar dan tampilkan bottom navigation
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).showToolbarAndBottomNavigation()

        dataStoreManager = DataStoreManager(requireContext())
        progressBar = view.findViewById(R.id.progress_bar)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_history)
        adapter = HistoryAdapter { analysisResult ->
            deleteAnalysisResult(analysisResult)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe data dari ViewModel
        viewModel.history.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }

        setupObservers()

        // Ambil token dari DataStoreManager dan muat data
        loadHistoryData()
    }

    private fun loadHistoryData() {
        dataStoreManager.getUserSession().asLiveData().observe(viewLifecycleOwner) { token ->
            token?.let {
                viewModel.loadHistory(it)
            } ?: run {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe data
        viewModel.history.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }

        // Observe error
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAnalysisResult(result: AnalysisResultEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Analysis")
            .setMessage("Are you sure you want to delete this analysis?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAnalysisResult(result)
                Toast.makeText(requireContext(), "Analysis deleted.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

