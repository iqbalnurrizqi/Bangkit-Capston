package com.example.capstoneproject4.ui.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneproject4.data.model.RecommendedProductX
import com.example.capstoneproject4.databinding.HomeItemProductBinding
import java.util.Calendar

class ProductsAdapter(
    private val context: Context,
    private val products: List<RecommendedProductX>,
    private val onAddRoutine: (productId: String, productName: String, time: String) -> Unit // Pass productId and time
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = HomeItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(context, products[position], onAddRoutine)
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(private val binding: HomeItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            product: RecommendedProductX,
            onAddRoutine: (String, String, String) -> Unit // Tambahkan productName
        ) {
            Glide.with(binding.root.context)
                .load(product.Image)
                .into(binding.productImage)
            binding.productName.text = product.product_name ?: "No Name Available"
            binding.productDescription.text = product.product_type
            binding.productPrice.text = "Rp. ${product.price}.000"

            binding.addToCartButton.setOnClickListener {
                val calendar = Calendar.getInstance()
                TimePickerDialog(context, { _, hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    val productName = product.product_name ?: "Unknown Product"
                    onAddRoutine(product.id, productName, time) // Kirimkan productId, productName, dan time
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }
        }
    }

    fun submitList(newProducts: List<RecommendedProductX>) {
        (products as MutableList<RecommendedProductX>).apply {
            clear()
            addAll(newProducts)
        }
        notifyDataSetChanged()
    }
}

