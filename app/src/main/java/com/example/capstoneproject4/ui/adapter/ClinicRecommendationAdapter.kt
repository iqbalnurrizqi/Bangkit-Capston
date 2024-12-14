package com.example.capstoneproject4.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject4.data.model.RecommendedClinic
import com.example.capstoneproject4.databinding.RecommendedItemClinicBinding

class ClinicRecommendationAdapter : RecyclerView.Adapter<ClinicRecommendationAdapter.ClinicViewHolder>() {

    private val clinics = mutableListOf<RecommendedClinic>()

    fun submitList(newClinics: List<RecommendedClinic>) {
        clinics.clear()
        clinics.addAll(newClinics)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClinicViewHolder {
        val binding = RecommendedItemClinicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClinicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClinicViewHolder, position: Int) {
        holder.bind(clinics[position])
    }

    override fun getItemCount(): Int = clinics.size

    class ClinicViewHolder(private val binding: RecommendedItemClinicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clinic: RecommendedClinic) {
            binding.tvClinicName.text = clinic.nama_klinik
            binding.tvClinicAddress.text = clinic.alamat
            binding.tvClinicMaps.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clinic.maps))
                binding.root.context.startActivity(intent)
            }
        }
    }
}