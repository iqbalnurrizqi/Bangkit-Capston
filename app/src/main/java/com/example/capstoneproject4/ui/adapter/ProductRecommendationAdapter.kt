// Updated ProductRecommendationAdapter
package com.example.capstoneproject4.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneproject4.data.model.RecommendedProduct
import com.example.capstoneproject4.data.model.RecommendedProductX
import com.example.capstoneproject4.databinding.RecommendedItemProductBinding

class ProductRecommendationAdapter : RecyclerView.Adapter<ProductRecommendationAdapter.ProductViewHolder>() {

    private val products = mutableListOf<RecommendedProductX>()

    fun submitList(newProducts: List<RecommendedProductX>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RecommendedItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(private val binding: RecommendedItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: RecommendedProductX) {
            // Load image using Glide
            Glide.with(binding.root.context)
                .load(product.Image)
                .into(binding.ivProductImage)
            binding.tvProductName.text = "${product.product_name}"
            binding.tvProductType.text = "${product.product_type}"
            binding.tvProductPrice.text = "Rp. ${product.price}.000"
            binding.tvProductDescription.text = "${product.Description}"
        }
    }
}