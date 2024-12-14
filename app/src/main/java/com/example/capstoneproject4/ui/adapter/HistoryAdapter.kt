package com.example.capstoneproject4.ui.adapter

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onDeleteClick: (AnalysisResultEntity) -> Unit
) : ListAdapter<AnalysisResultEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val analysisImage: ImageView = view.findViewById(R.id.iv_analysis_image)
        private val skinTypeText: TextView = view.findViewById(R.id.tv_skin_type)
        private val issuesText: TextView = view.findViewById(R.id.tv_issues)
        private val timestampText: TextView = view.findViewById(R.id.tv_timestamp)

        fun bind(item: AnalysisResultEntity, onDeleteClick: (AnalysisResultEntity) -> Unit) {
            Log.d("HistoryAdapter", "Binding item: $item")

            val uri = Uri.parse(item.imageUri)
            val context = itemView.context

            // Mengatasi masalah SecurityException dengan menyalin file sementara
            val tempFile = createTempFileFromUri(context, uri)
            if (tempFile != null) {
                Glide.with(context)
                    .load(tempFile)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(analysisImage)
            } else {
                Log.e("HistoryAdapter", "Failed to load image for URI: $uri")
            }

            val skinTypeFormatted = item.skinType.split("(")
            if (skinTypeFormatted.size == 2) {
                val type = skinTypeFormatted[1].split(",")[0].split("=")[1].trim() // Extract 'Oily'
                val confidence = skinTypeFormatted[1].split(",")[1].split("=")[1].replace(")", "").trim() // Extract '0.88817984'
                skinTypeText.text = "${type.capitalize()} ${(confidence.toFloat() * 100).toInt()}%"
            } else {
                skinTypeText.text = "Unknown Type"
            }

            // Filter skin issues dengan confidence > 60%
            val highConfidenceIssues = item.skinIssues
                .filter { it.confidence > 0.60 }
                .joinToString("\n") { it.issue }

            issuesText.text = if (highConfidenceIssues.isNotEmpty()) {
                highConfidenceIssues
            } else {
                "No issues above 60%"
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = Date(item.timestamp)
            timestampText.text = "Analyzed at: ${dateFormat.format(date)}"
        }

        private fun createTempFileFromUri(context: Context, uri: Uri): File? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)
                // Tambahkan waktu saat ini untuk membuat nama file unik
                val timestamp = System.currentTimeMillis()
                val tempFileName = "temp_image_${timestamp}.jpg"
                val tempFile = File(context.cacheDir, tempFileName)

                inputStream?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile
            } catch (e: Exception) {
                Log.e("HistoryAdapter", "Error creating temp file from URI: $uri", e)
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        Log.d("HistoryAdapter", "Creating ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("HistoryAdapter", "onBindViewHolder called for position $position with item: $item")
        holder.bind(item, onDeleteClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<AnalysisResultEntity>() {
        override fun areItemsTheSame(oldItem: AnalysisResultEntity, newItem: AnalysisResultEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AnalysisResultEntity, newItem: AnalysisResultEntity): Boolean {
            return oldItem == newItem
        }
    }
}
