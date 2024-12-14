package com.example.capstoneproject4.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneproject4.data.model.RecommendedProductX
import com.example.capstoneproject4.data.model.RoutineRequest
import com.example.capstoneproject4.databinding.HomeItemRoutineBinding
import com.example.capstoneproject4.R

class RoutinesAdapter(
    private val routines: List<RoutineRequest>,
    private val recommendedProducts: List<RecommendedProductX> // Tambahkan daftar produk yang direkomendasikan
) : RecyclerView.Adapter<RoutinesAdapter.RoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = HomeItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(routines[position], recommendedProducts) // Kirim daftar produk
    }

    override fun getItemCount(): Int = routines.size

    class RoutineViewHolder(private val binding: HomeItemRoutineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: RoutineRequest, recommendedProducts: List<RecommendedProductX>) {
            // Set data ke elemen UI
            binding.productName.text = routine.product_name
            binding.routineTime.text = routine.time

            // Cari gambar berdasarkan product_id
            val product = recommendedProducts.find { it.id == routine.product_id }

            if (product != null) {
                // Jika produk ditemukan, tampilkan gambar menggunakan Glide
                Glide.with(binding.productImage.context)
                    .load(product.Image)
                    .placeholder(R.drawable.ic_placeholder) // Placeholder jika gambar belum tersedia
                    .error(R.drawable.ic_error) // Gambar jika terjadi error
                    .into(binding.productImage)
            } else {
                // Gunakan gambar default jika produk tidak ditemukan
                binding.productImage.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }
}

