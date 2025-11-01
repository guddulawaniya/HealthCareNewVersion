package com.asyscraft.alzimer_module

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.adapter.AdapterViewAlbum
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityAlbumViewBinding
import com.asyscraft.alzimer_module.utils.GridSpacing
import com.careavatar.core_network.base.BaseActivity
import com.intuit.sdp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Album_view : BaseActivity() {

    private lateinit var binding: ActivityAlbumViewBinding

    private lateinit var AdapterViewAlbum: AdapterViewAlbum
    private var datalist: MutableList<String> = mutableListOf()

    private val PICK_IMAGE_REQUEST = 1001



    private val viewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id").toString()


        binding.editIcon.setOnClickListener{


            binding.Done.visibility = View.VISIBLE
            binding.editIcon.visibility = View.GONE
            binding.edittext.visibility = View.VISIBLE
            binding.text.visibility = View.GONE
            binding.back.visibility = View.GONE
            binding.delete.visibility = View.VISIBLE

            // ✅ Enable edit mode in adapter and refresh
            AdapterViewAlbum.isEditMode = true


            // 🔄 Reconfigure RecyclerView to GridLayoutManager
            val gridLayoutManager = GridLayoutManager(this, 3)
            binding.recyclerview.layoutManager = gridLayoutManager

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen._10sdp)
            if (binding.recyclerview.itemDecorationCount == 0) {
                binding.recyclerview.addItemDecoration(GridSpacing(3, spacingInPixels, true))
            }

            binding.recyclerview.adapter = AdapterViewAlbum
            AdapterViewAlbum.notifyDataSetChanged()
        }


        binding.Done.setOnClickListener{

            val title = binding.title.text.toString()
            val description = binding.edittext.text.toString()

            // ✅ Convert your selected URIs to File list
            // Use datalist if it holds file paths
            val imageFiles = datalist
                .filter { it.startsWith("/data") || it.startsWith("/storage") || it.startsWith("content://") || it.startsWith("/") } // local paths only
                .map { File(it) }


            viewModel.UpdateAlbumDetails(id, title, description, imageFiles)

        }

        binding.delete.setOnClickListener{


            viewModel.DeleteAlbum(id)
        }

        binding.back.setOnClickListener{
            finish()
        }


        viewModel.GetAlbumDetails(id)


        setupRecyclerViewAlbum()

        observeResponsegetalbumdetails()
        observeUpdateAlbumResponse()
        observedeleteResponse()
    }





    private fun setupRecyclerViewAlbum() {
        val isEdit = false  // default state

        // ✅ Initialize adapter FIRST
        AdapterViewAlbum = AdapterViewAlbum(this, datalist, isEditMode = isEdit) {
            pickImageFromGallery()
        }

        // ✅ Set layout manager and adapter based on mode
        if (isEdit) {
            val gridLayoutManager = GridLayoutManager(this, 3)
            binding.recyclerview.layoutManager = gridLayoutManager

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen._10sdp)
            binding.recyclerview.addItemDecoration(GridSpacing(3, spacingInPixels, true))
        } else {
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerview.layoutManager = linearLayoutManager
        }

        binding.recyclerview.adapter = AdapterViewAlbum
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    val file = uriToFile(this, imageUri)
                    if (file != null) {
                        datalist.add(file.absolutePath)  // ✅ Store file path or file itself based on your adapter
                    }
                }
                AdapterViewAlbum.notifyDataSetChanged()
            }
            // Single image fallback
            else if (data.data != null) {
                val imageUri = data.data!!
                val file = uriToFile(this, imageUri)
                if (file != null) {
                    datalist.add(file.absolutePath)  // ✅ Store file path or file
                    AdapterViewAlbum.notifyItemInserted(datalist.size - 1)
                }
            }
        }
    }


    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




    private fun observeResponsegetalbumdetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetAlbumDetail.observe(this@Album_view) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Album_view)
                            Log.d("AlbumDetails", "⏳ Loading album details...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                binding.title.setText(data.data.title)
                                binding.text.setText(data.data.description)
                                binding.edittext.setText(data.data.description)

                                if(data.data.description == "" || data.data.description == null ){
                                    binding.nodescriptionimg.visibility = View.VISIBLE
                                }else{
                                    binding.nodescriptionimg.visibility = View.GONE
                                }

                                datalist.clear()
                                datalist.addAll(data.data.thumbnail)
                                AdapterViewAlbum.notifyDataSetChanged()
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to fetch album details", this@Album_view)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlbumDetails", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Album_view)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Album_view)
                        }
                    }
                }
            }
        }
    }



    private fun observeUpdateAlbumResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.updatealbumdetail.observe(this@Album_view) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Album_view)
                            Log.d("AlbumUpdate", "⏳ Updating album...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                Toast.makeText(this@Album_view, "Album updated successfully!", Toast.LENGTH_SHORT).show()

                                binding.Done.visibility = View.GONE
                                binding.editIcon.visibility = View.VISIBLE
                                binding.edittext.visibility = View.GONE
                                binding.text.visibility = View.VISIBLE
                                binding.back.visibility = View.VISIBLE
                                binding.delete.visibility = View.GONE
                                AdapterViewAlbum.isEditMode = false

                                // Set layout manager to horizontal
                                val layoutManager = LinearLayoutManager(this@Album_view, LinearLayoutManager.HORIZONTAL, false)
                                binding.recyclerview.layoutManager = layoutManager

                                // Remove any existing item decoration
                                if (binding.recyclerview.itemDecorationCount > 0) {
                                    binding.recyclerview.removeItemDecorationAt(0)
                                }

                                binding.recyclerview.adapter = AdapterViewAlbum
                                AdapterViewAlbum.notifyDataSetChanged()

                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to update album", this@Album_view)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlbumUpdate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Album_view)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Album_view)
                        }
                    }
                }

                viewModel.error.observe(this@Album_view) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@Album_view, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    private fun observedeleteResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.deletealbum.observe(this@Album_view) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Album_view)
                            Log.d("AlbumDelete", "⏳ Deleting album...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                Toast.makeText(this@Album_view, "Album Deleted Successfully!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to delete album", this@Album_view)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlbumDelete", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Album_view)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Album_view)
                        }
                    }
                }

                viewModel.error.observe(this@Album_view) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@Album_view, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}