package com.example.capstoneproject4.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject4.data.model.Feature
import com.example.capstoneproject4.databinding.HomeItemFeatureBinding

class FeaturesAdapter(private val features: List<Feature>) : RecyclerView.Adapter<FeaturesAdapter.FeatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = HomeItemFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int = features.size

    class FeatureViewHolder(private val binding: HomeItemFeatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.featureImage.setImageResource(feature.imageResId)
            binding.featureName.text = feature.title
            binding.featureDescription.text = feature.description
        }
    }
}
