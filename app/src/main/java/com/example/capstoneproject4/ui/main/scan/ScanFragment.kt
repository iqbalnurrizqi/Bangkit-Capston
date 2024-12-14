package com.example.capstoneproject4.ui.main.scan

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ScanFragment : Fragment() {

    private lateinit var selectedImageUri: Uri
    private lateinit var viewModel: ScanViewModel
    private lateinit var imageView: ImageView
    private val pickImageRequestCode = 100
    private val captureImageRequestCode = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ScanViewModel::class.java]
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).showToolbarAndBottomNavigation()

        // Tampilkan dialog saat fragment dimulai
        showCustomDialog()

        // Tombol-tombol dan interaksi lainnya
        imageView = view.findViewById(R.id.image_placeholder)
        val btnOpenGallery: Button = view.findViewById(R.id.btn_open_gallery)
        val btnOpenCamera: Button = view.findViewById(R.id.btn_open_camera)
        val btnAnalyze: Button = view.findViewById(R.id.btn_analyze)

        btnOpenGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, pickImageRequestCode)
        }

        btnOpenCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, captureImageRequestCode)
        }

        btnAnalyze.setOnClickListener {
            if (::selectedImageUri.isInitialized) {
                val bundle = Bundle().apply {
                    putString("imageUri", selectedImageUri.toString())
                }
                findNavController().navigate(R.id.action_scanFragment_to_resultFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Please select an image first.", Toast.LENGTH_SHORT).show()
            }
        }

        // Observasi perubahan URI pada ViewModel
        viewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                imageView.setImageURI(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            pickImageRequestCode -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    selectedImageUri = data.data!!
                    imageView.setImageURI(selectedImageUri)
                }
            }
            captureImageRequestCode -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val bitmap = data.extras?.get("data") as Bitmap
                    selectedImageUri = saveBitmapAsUri(bitmap)
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun saveBitmapAsUri(bitmap: Bitmap): Uri {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(file)
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom_popup, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.show()
    }
}