package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.DialogWalkHistoryMapBinding
import java.io.File
import java.util.UUID

class WalkHistoryMapDialog : DialogFragment() {
    private var _binding: DialogWalkHistoryMapBinding? = null
    private val binding get() = _binding!!
    private var walkHistoryMap: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWalkHistoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupView() {
        Glide.with(this)
            .load(walkHistoryMap)
            .error(R.drawable.img_map_default)
            .fallback(R.drawable.img_map_default)
            .into(binding.ivMap)
    }

    private fun setupListener() {
        binding.ivShareBtn.setOnClickListener {
            shareImage()
        }
    }

    private fun shareImage() {
        val sharedImagesDir = File(requireContext().cacheDir, "sharedImages")
        sharedImagesDir.mkdirs()

        val imageFile = File(sharedImagesDir, "history_image.jpg")

        Glide.with(this)
            .asBitmap()
            .load(walkHistoryMap)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        imageFile.outputStream().use {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }

                        val imageUri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.nbcfinalteam2.ddaraogae.fileprovider",
                            imageFile
                        )

                        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                            type = "image/jpeg"
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(imageUri))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.home_history_dialog_map_content))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        startActivity(Intent.createChooser(intent, getString(R.string.home_history_dialog_map_chooser)))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), R.string.home_history_dialog_map_fail_message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    //Noting to do
                }
            })
    }

    fun setEnlargementOfImage(image: String) {
        walkHistoryMap = image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}