package com.example.capstoneproject4.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject4.R

class SkinDiseaseAdapter :
    RecyclerView.Adapter<SkinDiseaseAdapter.SkinDiseaseViewHolder>() {

    private val items = mutableListOf<SkinDiseaseItem>()

    // Fungsi untuk memasukkan daftar baru ke adapter
    fun submitList(newItems: List<SkinDiseaseItem>) {
        // Filter item dengan confidence > 80%
        val filteredItems = newItems.filter { it.confidence > 50 }
        items.clear()
        items.addAll(filteredItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinDiseaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_item_progress, parent, false)
        return SkinDiseaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkinDiseaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // Data class untuk item penyakit kulit
    data class SkinDiseaseItem(val label: String, val confidence: Float)

    // ViewHolder untuk memuat tampilan setiap item
    class SkinDiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val diseaseText: TextView = itemView.findViewById(R.id.tv_disease_progress_text)
        private val diseaseBackground: TextView = itemView.findViewById(R.id.tv_disease_background)
        private val diseaseForeground: TextView = itemView.findViewById(R.id.tv_disease_foreground)

        fun bind(item: SkinDiseaseItem) {
            // Format teks dengan confidence dan label
            diseaseText.text = String.format("%.2f%% (%s)", item.confidence, item.label)

            // Mengatur lebar foreground progress bar berdasarkan confidence
            diseaseBackground.viewTreeObserver.addOnGlobalLayoutListener {
                val layoutParams = diseaseForeground.layoutParams
                layoutParams.width =
                    (diseaseBackground.width * (item.confidence / 100)).toInt()
                diseaseForeground.layoutParams = layoutParams
            }
        }
    }
}